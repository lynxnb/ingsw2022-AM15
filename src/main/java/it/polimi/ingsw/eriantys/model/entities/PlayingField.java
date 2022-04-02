package it.polimi.ingsw.eriantys.model.entities;

import it.polimi.ingsw.eriantys.RuleBook;
import it.polimi.ingsw.eriantys.model.entities.enums.GameMode;
import it.polimi.ingsw.eriantys.model.entities.enums.HouseColor;
import it.polimi.ingsw.eriantys.model.entities.enums.TowerColor;

import java.util.*;
import java.util.stream.Collectors;

// TODO
public class PlayingField {
    private ArrayList<Island> islands;
    private ArrayList<Cloud> clouds;
    private StudentBag studentBag;
    private EnumMap<TowerColor, Integer> influenceTracer;
    private EnumMap<HouseColor, TowerColor> professorHolder;
    private int coins;
    private int motherNaturePosition;
    /* TODO character card features still need implementation
    private ArrayList<CharacterCard> characterCards;
    private CharacterCard playedCaracterCard;
    */

    /**
     * Initializes playing field and all of its components
     * @param ruleBook
     */
    public PlayingField(RuleBook ruleBook) {
        influenceTracer = new EnumMap<>(TowerColor.class);
        professorHolder = new EnumMap<>(HouseColor.class);
        //initializing islands and student bag
        islands = new ArrayList<Island>();
        studentBag = new StudentBag();
        studentBag.initStudents(2);
        for (int i = 0; i < RuleBook.ISLAND_COUNT; i++) {
            if (i < 8 && i != 5) islands.add(new Island(takeStudentFromBag()));
            else islands.add(new Island());
        }
        //definitive initialization of StudentBag
        studentBag.initStudents(RuleBook.STUDENT_PER_COLOR - 2);
        //initializing Clouds
        clouds = new ArrayList<Cloud>();
        for (int i = 0; i < ruleBook.cloudCount; i++) {
            Students students = new Students();
            for (int j = 0; j < ruleBook.playableStudentCount; j++) {
                HouseColor student = takeStudentFromBag();
                students.addStudent(student);
            }
            clouds.add(new Cloud(students));
        }
        // the cloud count is the same as the number of players
        if (ruleBook.gameMode == GameMode.EXPERT) coins = RuleBook.TOTAL_COINS - ruleBook.cloudCount;
        else coins = 0;
        motherNaturePosition = 0;
    }

    //TODO merge Islands
    private void mergeIslands(int islandIndex) {
    }

    /**
     * takes a student from StudentBag
     * @return HouseColor of the student
     */
    public HouseColor takeStudentFromBag() {
        return studentBag.takeRandomStudent();
    }


    public Cloud getCloud(int cloudIndex) {
        return clouds.get(cloudIndex);
    }

    public Island getIsland(int islandIndex) {
        return islands.get(islandIndex);
    }

    /**
     * moves motherNature pawn a certain amount
     * @param amount
     */
    public void moveMotherNature(int amount) {
        motherNaturePosition = (motherNaturePosition + amount) % islands.size();
    }

    /**
     * refills Clouds with students according to rules
     * @param ruleBook
     */
    public void refillClouds(RuleBook ruleBook) {
        for (Cloud c : clouds) {
            Students students = new Students();
            for (int i = 0; i < ruleBook.playableStudentCount; i++) {
                students.addStudent(takeStudentFromBag());
            }
            c.setStudents(students);
        }
    }

    /**
     * Returns true if a certain team has a certain professor
     * @param professor
     * @param team
     * @return boolean
     */
    public boolean hasProfessor(HouseColor professor, TowerColor team) {
        return professorHolder.get(professor) == team;
    }


    public void setProfessorHolder(TowerColor team, HouseColor professor) {
        professorHolder.put(professor, team);
    }

    public void addCoin() {
        coins++;
    }
    public void removeCoin() {
        coins--;
    }

    // getCharcterCards(),getPlayedCharacterCard()

    /* maybe deprecated?
    public void setTower(player: String){

    }*/

    //TODO influence count
    //public TowerColor getMostInfluential(int islandIndex) {
    //}
}
