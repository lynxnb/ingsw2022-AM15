package it.polimi.ingsw.eriantys.model;

import it.polimi.ingsw.eriantys.model.entities.Player;
import it.polimi.ingsw.eriantys.model.entities.PlayingField;
import it.polimi.ingsw.eriantys.model.entities.Students;
import it.polimi.ingsw.eriantys.model.enums.GameMode;
import it.polimi.ingsw.eriantys.model.enums.GamePhase;
import it.polimi.ingsw.eriantys.model.enums.TowerColor;
import it.polimi.ingsw.eriantys.model.enums.TurnPhase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameState {
  final private List<Player> players = new ArrayList<>(); // Players in the game

  final private List<Player> turnOrder = new ArrayList<>(); // List of players sorted by their turn order
  final private List<Player> planOrder = new ArrayList<>(); // List of players sorted by their turn order
  private int currentPlayer; // Nickname of the current player
  private GamePhase gamePhase; // Current phase of the game
  private TurnPhase turnPhase; // Current turn phase
  private final RuleBook ruleBook; // Set of rules used in this game
  private PlayingField playingField; // Playing field of this game

  public GameState(int playerCount, GameMode mode) {
    ruleBook = RuleBook.makeRules(mode, playerCount);
    playingField = new PlayingField(ruleBook);
    currentPlayer = 0;
    gamePhase = GamePhase.PLANNING;
    turnPhase = TurnPhase.PLACING;
  }

  /**
   * Adds a player to the player List and planOrder List
   *
   * @param playerName
   * @param towerColor
   */
  public void addPlayer(String playerName, TowerColor towerColor) {
    Students entrance = new Students();
    for (int i = 0; i <= ruleBook.entranceSize; i++)
      entrance.addStudent(playingField.takeStudentFromBag());
    Player newPlayer = new Player(ruleBook, playerName, towerColor, entrance);
    players.add(newPlayer);
    planOrder.add(newPlayer);
    playingField.addTeam(towerColor);
  }

  /**
   * Advances currentPlayer index
   */
  public void advancePlayer() {
    currentPlayer = (currentPlayer + 1) % players.size();
  }

  public List<Player> getPlayers() {
    return players;
  }

  /**
   * returns the current player of current gamePhase
   * @return Player
   */
  public Player getCurrentPlayer() {
    if (getGamePhase() == GamePhase.PLANNING)
      return planOrder.get(currentPlayer);
    if (getGamePhase() == GamePhase.ACTION)
      return turnOrder.get(currentPlayer);
    return null;
  }

  /**
   * returns order
   *
   * @return
   */
  public List<Player> getPlanOrderPlayers() {
    return planOrder;
  }

  /**
   * @return the playing field of this game
   */
  public PlayingField getPlayingField() {
    return playingField;
  }

  /**
   * @return the current phase of the game
   */
  public GamePhase getGamePhase() {
    return gamePhase;
  }

  /**
   * advances to next gamePhase
   */
  public void advanceGamePhase() {
    switch (gamePhase) {
      case ACTION -> {
        gamePhase = GamePhase.PLANNING;
        prepareOrderForActionPhase();
      }
      case PLANNING -> {
        gamePhase = GamePhase.ACTION;
        prepareOrderForNextRound();
      }
    }
  }

  /**
   * @return the current turn phase
   */
  public TurnPhase getTurnPhase() {
    return turnPhase;
  }

  /**
   * advances to next turnPhase (takes into account gameMode)
   */
  public void advanceTurnPhase() {
    if (ruleBook.gameMode == GameMode.NORMAL) {
      switch (turnPhase) {
        case PLACING -> turnPhase = TurnPhase.EFFECT;
        case EFFECT -> turnPhase = TurnPhase.MOVING;
        case MOVING -> turnPhase = TurnPhase.PICKING;
        case PICKING -> turnPhase = TurnPhase.PLACING;
      }
    }
    if(ruleBook.gameMode == GameMode.NORMAL) {
      switch (turnPhase) {
        case PLACING -> turnPhase = TurnPhase.MOVING;
        case MOVING -> turnPhase = TurnPhase.PICKING;
        case PICKING -> turnPhase = TurnPhase.PLACING;
      }
    }
  }

  //TODO la win condition deve fare altre cose credo?
  /**
   * Checks:
   * - if there are still students in the student Bag<br>
   * - if every player still has towers<br>
   * - if every player still has assistant cards<br>
   * - if the island amount is allowed<br>
   * if one of these condition isn't true The Game ends
   */
  public void checkWinCondition() {
    if(playingField.getStudentBag().isEmpty()) gamePhase = GamePhase.WIN;

    for (Player p: players) {
      if(p.getDashboard().noMoreTowers()) gamePhase = GamePhase.WIN;

      if(p.getCards().size() == 0) gamePhase = GamePhase.WIN;
    }

    if (playingField.getIslandsAmount() <= RuleBook.MIN_ISLAND_COUNT) gamePhase = GamePhase.WIN;

  }


  public RuleBook getRuleBook() {
    return ruleBook;
  }

  public List<Player> getTurnOrderPlayers() {
    return turnOrder;
  }

  /**
   * Sorts players by their selected assistant card movement value
   */
  private void prepareOrderForActionPhase() {
    turnOrder.clear();
    turnOrder.addAll(players);
    turnOrder.sort(Comparator.comparingInt(Player::getTurnPriority));
  }

  private void prepareOrderForNextRound() {
    planOrder.clear();
    planOrder.add(turnOrder.get(0));

    int offset = players.indexOf(turnOrder.get(0));
    for (int i = 0; i < players.size(); i++) {
      planOrder.add(players.get((i + offset) % players.size()));
    }
  }

  public boolean isTurnOf(String nickname) {
    return players.get(currentPlayer).getNickname().equals(nickname);
  }
}
