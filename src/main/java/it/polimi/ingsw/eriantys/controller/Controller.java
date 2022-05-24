package it.polimi.ingsw.eriantys.controller;

import it.polimi.ingsw.eriantys.model.GameInfo;
import it.polimi.ingsw.eriantys.model.GameState;
import it.polimi.ingsw.eriantys.model.RuleBook;
import it.polimi.ingsw.eriantys.model.actions.*;
import it.polimi.ingsw.eriantys.model.entities.StudentBag;
import it.polimi.ingsw.eriantys.model.entities.Students;
import it.polimi.ingsw.eriantys.model.entities.character_cards.CharacterCard;
import it.polimi.ingsw.eriantys.model.entities.character_cards.CharacterCardEnum;
import it.polimi.ingsw.eriantys.model.enums.GameMode;
import it.polimi.ingsw.eriantys.model.enums.TowerColor;
import it.polimi.ingsw.eriantys.network.Client;
import it.polimi.ingsw.eriantys.network.Message;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.eriantys.network.MessageType.*;

/**
 * The Controller manages the creation and the sending of messages to the Server
 */
abstract public class Controller implements Runnable {
  public static Controller create(boolean isGui, Client networkClient) {
    if (isGui)
      return new GuiController(networkClient);
    else
      return new CliController(networkClient);
  }
  
  protected GameInfo gameInfo;
  protected Sender sender;
  protected Client networkClient;
  protected GameState gameState;
  
  protected String nickname;
  protected String gameCode;
  
  protected PropertyChangeSupport listenerHolder;
  
  public Controller(Client networkClient) {
    this.networkClient = networkClient;
    sender = new Sender();
    listenerHolder = new PropertyChangeSupport(this);
  }
  
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
  
  public boolean connect(String address, int port) {
    try {
      networkClient.connect(address, port);
      // Launch the network listening thread
      new Thread(networkClient, "socket").start();
    } catch (IOException e) {
      return false;
    }
    return true;
  }
  
  public void initGame() {
    gameState = new GameState(gameInfo.getMaxPlayerCount(), gameInfo.getMode());
    
    for (var playerEntry : gameInfo.getPlayersMap().entrySet())
      gameState.addPlayer(playerEntry.getKey(), playerEntry.getValue());
  }
  
  /**
   * Applies the given {@link GameAction} to the game state.
   *
   * @param action The {@link GameAction} to apply to the game state
   * @return {@code true} if action was valid and was applied successfully, {@code false} otherwise
   */
  public boolean executeAction(GameAction action) {
    synchronized (gameState) {
      if (action.isValid(gameState)) {
        action.apply(gameState);
        return true;
      }
    }
    return false;
  }
  
  abstract public void showError(String error);
  
  /**
   * Add a listener for events with the given tag.
   *
   * @param listener The listener to be subscribed
   * @param tag      The event tag
   */
  public void addListener(PropertyChangeListener listener, String tag) {
    listenerHolder.addPropertyChangeListener(tag, listener);
  }
  
  public abstract void fireChanges(EventEnum event);
  
  /**
   * Removes the given listener.
   *
   * @param listener Listener to be unsubscribed
   */
  public void removeListener(PropertyChangeListener listener) {
    listenerHolder.removePropertyChangeListener(listener);
  }
  
  /**
   * Removes the given listener for events with the given tag.
   *
   * @param listener The listener to be unsubscribed
   * @param tag      The event tag
   */
  public void removeListener(PropertyChangeListener listener, String tag) {
    listenerHolder.removePropertyChangeListener(tag, listener);
  }
  
  public PropertyChangeSupport getListenerHolder() {
    return listenerHolder;
  }
  
  public GameInfo getGameInfo() {
    return gameInfo;
  }
  
  public void setGameInfo(GameInfo gameInfo) {
    this.gameInfo = gameInfo;
  }
  
  public GameState getGameState() {
    return gameState;
  }
  
  public String getNickname() {
    return nickname;
  }
  
  public String getGameCode() {
    return gameCode;
  }
  
  public void setGameCode(String gameCode) {
    this.gameCode = gameCode;
  }
  
  public Sender sender() {
    return sender;
  }
  
  public class Sender {
    /**
     * Sends a START_GAME message to the server, containing the initiateGameEntities action. <br>
     */
    public boolean sendStartGame() {
      // Send message for creating server game
      // Initialize the game entities
      
      if (!gameInfo.start())
        return false;
      // Initiate character cards
      List<CharacterCardEnum> characterCardEnums = new ArrayList<>(Arrays.asList(CharacterCardEnum.values()));
      
      // Initiate students on island
      StudentBag bag = new StudentBag();
      bag.initStudents(RuleBook.STUDENT_PER_COLOR_SETUP);
      List<Students> studentsOnIslands = new ArrayList<>();
      for (int i = 0; i < RuleBook.ISLAND_COUNT; i++) {
        studentsOnIslands.add(new Students());
        if (i != 0 && i != 6)
          studentsOnIslands.get(i).addStudent(bag.takeRandomStudent());
      }
      
      // Initiate entrances.
      bag.initStudents(RuleBook.STUDENT_PER_COLOR - RuleBook.STUDENT_PER_COLOR_SETUP);
      List<Students> entrances = new ArrayList<>();
      for (int i = 0; i < gameInfo.getMaxPlayerCount(); i++) {
        entrances.add(new Students());
        for (int j = 0; j < gameState.getRuleBook().entranceSize; j++) {
          entrances.get(i).addStudent(bag.takeRandomStudent());
        }
      }
      
      // Initiate clouds.
      List<Students> cloudsStudents = new ArrayList<>();
      for (int i = 0; i < gameInfo.getMaxPlayerCount(); i++) {
        cloudsStudents.add(new Students());
        for (int j = 0; j < gameState.getRuleBook().playableStudentCount; j++) {
          entrances.get(i).addStudent(bag.takeRandomStudent());
        }
      }
      // Action Creation
      GameAction action = new InitiateGameEntities(entrances, studentsOnIslands, cloudsStudents, characterCardEnums);
      
      networkClient.send(new Message.Builder(START_GAME)
              .action(action)
              .gameInfo(gameInfo)
              .nickname(nickname)
              .build());
      
      return true;
    }
    
    /**
     * Send a message to the server with MoveMotherNature action
     */
    public boolean sendPickAssistantCard(int assistantCardIndex) {
      GameAction action = new PickAssistantCard(assistantCardIndex);
      
      if (!action.isValid(gameState))
        return false;
      
      networkClient.send(new Message.Builder(PLAY_ACTION)
              .action(action)
              .nickname(nickname)
              .build());
      return true;
    }
    
    /**
     * Send a message to the server with sendPickCloud action
     *
     * @param cloudIndex Index of the position of the chosen island
     */
    public boolean sendPickCloud(int cloudIndex) {
      GameAction action = new PickCloud(cloudIndex);
      
      if (!action.isValid(gameState))
        return false;
      
      networkClient.send(new Message.Builder(PLAY_ACTION)
              .action(action)
              .nickname(nickname)
              .build());
      return true;
    }
    
    /**
     * Send a message to the server with RefillCloud action
     */
    public void sendRefillCloud() {
      List<Students> cloudsStudents = new ArrayList<>();
      Students temp = new Students();
      StudentBag currentBag = gameState.getPlayingField().getStudentBag();
      
      // Populate clouds with random students from bag
      for (int cloudIter = 0; cloudIter < gameState.getRuleBook().cloudCount; cloudIter++) {
        for (int cloudSizeIter = 0; cloudSizeIter < gameState.getRuleBook().playableStudentCount; cloudSizeIter++) {
          temp.addStudent(currentBag.takeRandomStudent());
        }
        cloudsStudents.add(new Students(temp));
        temp = new Students(); // clear temp
      }
      
      networkClient.send(new Message.Builder(GAMEDATA)
              .action(new RefillClouds(cloudsStudents))
              .gameInfo(gameInfo)
              .build());
    }
    
    /**
     * Send a message to the server with ActivateEffect action
     */
    public boolean sendActivateEffect(CharacterCard cc) {
      GameAction action = new ActivateCCEffect(cc);
      
      if (!action.isValid(gameState))
        return false;
      
      networkClient.send(new Message.Builder(GAMEDATA)
              .action(action)
              .gameInfo(gameInfo)
              .build());
      return true;
    }
    
    /**
     * Send a message to the server with ChooseCharacterCard action
     *
     * @param ccIndex Index of the position of the chosen character card
     */
    public boolean sendChooseCharacterCard(int ccIndex) {
      GameAction action = new ChooseCharacterCard(ccIndex);
      
      if (!action.isValid(gameState))
        return false;
      
      networkClient.send(new Message.Builder(PLAY_ACTION)
              .action(action)
              .nickname(nickname)
              .build());
      return true;
    }
    
    /**
     * Send a message to the server with MoveMotherNature action
     *
     * @param amount Amount of island mother nature is wanted to be moved
     */
    public boolean sendMoveMotherNature(int amount) {
      GameAction action = new MoveMotherNature(amount);
      
      if (!action.isValid(gameState))
        return false;
      
      networkClient.send(new Message.Builder(GAMEDATA)
              .action(action)
              .nickname(nickname)
              .build());
      return true;
    }
    
    /**
     * Send a message to the server with MoveStudentsToDiningHall action
     *
     * @param students Students to move
     */
    public boolean sendMoveStudentsToDiningHall(Students students) {
      GameAction action = new MoveStudentsToDiningHall(students);
      
      if (!action.isValid(gameState))
        return false;
      
      networkClient.send(new Message.Builder(PLAY_ACTION)
              .action(action)
              .nickname(nickname)
              .build());
      return true;
    }
    
    /**
     * Send a message to the server with MoveStudentsToIsland action
     *
     * @param students    Students to move
     * @param islandIndex Index of island destination
     * @return True if the action is valid and sent.
     */
    public boolean sendMoveStudentsToIsland(Students students, int islandIndex) {
      GameAction action = new MoveStudentsToIsland(students, islandIndex);
      
      if (!action.isValid(gameState))
        return false;
      
      networkClient.send(new Message.Builder(PLAY_ACTION)
              .action(action)
              .nickname(nickname)
              .build());
      return true;
    }
    
    public void sendNickname(String nickname) {
      networkClient.send(new Message.Builder(NICKNAME_REQUEST)
              .nickname(nickname)
              .build());
    }
    
    public void sendCreateGame(int numberOfPlayers, GameMode gameMode) {
      gameInfo = new GameInfo(numberOfPlayers, gameMode);
      networkClient.send(new Message.Builder(CREATE_GAME)
              .gameInfo(gameInfo)
              .nickname(nickname)
              .build());
    }
    
    
    public void sendJoinGame(String gameCode) {
      networkClient.send(new Message.Builder(JOIN_GAME)
              .gameInfo(gameInfo)
              .gameCode(gameCode)
              .nickname(nickname)
              .build());
    }
    
    public boolean sendSelectTower(TowerColor color) {
      if (!gameInfo.isTowerColorValid(nickname, color))
        return false;
      gameInfo.addPlayer(nickname, color);
      networkClient.send(new Message.Builder(SELECT_TOWER)
              .gameInfo(gameInfo)
              .nickname(nickname)
              .build());
      return true;
    }
    
  }
}
