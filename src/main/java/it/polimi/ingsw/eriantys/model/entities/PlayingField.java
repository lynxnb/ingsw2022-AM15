package it.polimi.ingsw.eriantys.model.entities;

import it.polimi.ingsw.eriantys.model.RuleBook;
import it.polimi.ingsw.eriantys.model.entities.character_cards.CharacterCard;
import it.polimi.ingsw.eriantys.model.enums.GameMode;
import it.polimi.ingsw.eriantys.model.enums.HouseColor;
import it.polimi.ingsw.eriantys.model.enums.TowerColor;

import java.util.*;

import static it.polimi.ingsw.eriantys.loggers.Loggers.modelLogger;

public class PlayingField {
  private final List<Island> islands;
  private final List<Cloud> clouds;
  private final StudentBag studentBag;
  private final ProfessorHolder professorHolder;
  private final TeamsInfluenceTracer teamsInfluence;
  private final List<TowerColor> teams = new ArrayList<>(); // Active tower colors in this game
  private int locks;
  private int bank;
  private int motherNaturePosition;

  private List<CharacterCard> characterCards = new ArrayList<>();
  private CharacterCard playedCharacterCard;

  /**
   * Initializes playing field and all of its components
   */
  public PlayingField(RuleBook ruleBook) {
    professorHolder = new ProfessorHolder(new EnumMap<>(HouseColor.class));
    teamsInfluence = new TeamsInfluenceTracer(new EnumMap<>(TowerColor.class));

    // Island initialization
    islands = new ArrayList<>();
    for (int i = 0; i < RuleBook.ISLAND_COUNT; i++) {
      islands.add(new Island());
    }

    // StudentBag initialization
    studentBag = new StudentBag();
    studentBag.initStudents(RuleBook.STUDENT_PER_COLOR);

    // initializing Clouds
    clouds = new ArrayList<>();
    for (int i = 0; i < ruleBook.cloudCount; i++) {
      clouds.add(new Cloud(new Students()));
    }

    // the cloud count is the same as the number of players
    if (ruleBook.gameMode == GameMode.EXPERT) {
      bank = RuleBook.TOTAL_COINS - ruleBook.cloudCount;
      locks = RuleBook.LOCK_AMOUNT;
    } else {
      bank = 0;
      locks = 0;
    }
    motherNaturePosition = 0;
  }

  public List<Island> getIslands() {
    return islands;
  }

  public ProfessorHolder getProfessorHolder() {
    return professorHolder;
  }

  /**
   * Merges islands[islandIndex] with adjacent islands if they have the same TowerColor<br/>
   * - if the Merge gets applied also motherNaturePosition gets adjusted<br/>
   * - excess locks return to the LockIsland CC<br/>
   */
  public void mergeIslands(int islandIndex) {
    Island nextIsland = islands.get(islandIndex + 1 % islands.size());
    Island prevIsland = (islandIndex == 0) ? islands.get(islands.size() - 1) : islands.get(islandIndex - 1);
    Island currIsland = islands.get(islandIndex);
    modelLogger.debug("\nprev island:" + islands.indexOf(prevIsland));
    modelLogger.debug("\ncurrent island:" + islands.indexOf(currIsland));
    modelLogger.debug("\nnext island:" + islands.indexOf(nextIsland));

    //tries merging next island
    if (nextIsland.getTowerColor().isPresent())
      if (nextIsland.getTowerColor().get() == currIsland.getTowerColor().get())
        merge(currIsland, nextIsland);

    //tries merging prev island
    if (prevIsland.getTowerColor().isPresent())
      if (prevIsland.getTowerColor().get() == currIsland.getTowerColor().get())
        merge(currIsland, prevIsland);

  }

  /**
   * merges secondIsland onto firstIsland
   */
  private void merge(Island firstIsland, Island secondIsland) {
    //manage locks , and eventually return them to CC
    if (secondIsland.isLocked() && firstIsland.isLocked()) {
      locks++;
    } else if (secondIsland.isLocked() && !firstIsland.isLocked()) firstIsland.setLocked(true);

    if (islands.indexOf(secondIsland) <= motherNaturePosition) motherNaturePosition--;
    firstIsland.addStudents(secondIsland.getStudents());
    firstIsland.setTowerCount(firstIsland.getTowerCount() + secondIsland.getTowerCount());
    islands.remove(secondIsland);
  }

  public StudentBag getStudentBag() {
    return studentBag;
  }

  public void addStudentToBag(HouseColor color) {
    studentBag.addStudent(color);
  }

  public Cloud getCloud(int cloudIndex) {
    return clouds.get(cloudIndex);
  }

  public List<Cloud> getClouds() {
    return clouds;
  }

  public Island getIsland(int islandIndex) {
    return islands.get(islandIndex);
  }

  public int getIslandsAmount() {
    return islands.size();
  }

  public void setLocks(int amount) {
    locks = amount;
  }

  public int getLocks() {
    return locks;
  }

  /**
   * Moves motherNature pawn a certain amount of positions
   *
   * @param amount Amount of islands it's wanted to be moved
   */
  public void moveMotherNature(int amount) {
    motherNaturePosition = (motherNaturePosition + amount) % islands.size();
  }

  public int getMotherNaturePosition() {
    return motherNaturePosition;
  }

  /**
   * Returns true if a certain team has a certain professor
   *
   * @param professor
   * @param team
   * @return boolean
   */
  public boolean hasProfessor(HouseColor professor, TowerColor team) {
    return professorHolder.hasProfessor(team, professor);
  }

  public int getHeldProfessorCount(TowerColor team) {
    return professorHolder.getHeldProfessorCount(team);
  }

  public void setProfessorHolder(TowerColor team, HouseColor professor) {
    professorHolder.setProfessorHolder(team, professor);
  }

  /**
   * Adds a team to the active teams list in this game
   *
   * @param team
   */
  public void addTeam(TowerColor team) {
    teams.add(team);
  }

  public void addCoin() {
    bank++;
  }

  public void removeCoin() {
    bank--;
  }


  // CC effect
  // Temporary method, just testing
  public Optional<TowerColor> getMostInfluential(int islandIndex) {
    Island island = islands.get(islandIndex);
    island.updateInfluences(professorHolder);
    return island.getTeamsInfluenceTracer().getMostInfluential();
  }

  public void setPlayedCharacterCard(int ccIndex) {
    playedCharacterCard = characterCards.get(ccIndex);
  }

  public void setPlayedCharacterCard(CharacterCard cc) {
    playedCharacterCard = cc;

    for (int i = 0; i < characterCards.size(); i++) {
      if (cc.getCardEnum().equals(characterCards.get(i).getCardEnum()))
        characterCards.set(i, cc);
    }

  }


  public CharacterCard getPlayedCharacterCard() {
    return playedCharacterCard;
  }

  public List<CharacterCard> getCharacterCards() {
    return characterCards;
  }

  public void setCharacterCards(List<CharacterCard> cards) {
    characterCards = cards;
  }

  public int getBank() {
    return bank;
  }

  public void addCoinsToBank(int amount) {
    bank += amount;
  }
}


