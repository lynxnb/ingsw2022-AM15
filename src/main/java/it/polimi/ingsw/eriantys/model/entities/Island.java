package it.polimi.ingsw.eriantys.model.entities;

import it.polimi.ingsw.eriantys.model.enums.TowerColor;

import java.util.EnumMap;
import java.util.Optional;

import static it.polimi.ingsw.eriantys.loggers.Loggers.modelLogger;

public class Island extends Slot {
  private Students students = new Students();
  private final TeamsInfluenceTracer teamsInfluence = new TeamsInfluenceTracer(new EnumMap<>(TowerColor.class));
  private Towers towers = new Towers();
  private boolean isLocked = false;

  /**
   * Initialize island with a student on
   *
   * @param students
   */
  public Island(Students students) {
    towers.count = 0;
    towers.color = null;
    this.students.addStudents(students);
//    modelLogger.debug("Island created");
  }

  public Island() {
    towers.count = 0;
    towers.color = null;
//    modelLogger.debug("Island created");
  }

  public void addStudents(Students s) {
    students.addStudents(s);
    modelLogger.debug("Students added on Island");
  }

  public void setTowerColor(TowerColor towerColor) {
    towers.color = towerColor;
  }

  public void setTowerCount(int count) {
    towers.count = count;
  }

  public void setLocked(boolean lock) {
    isLocked = lock;
  }

  public Students getStudents() {
    return students;
  }

  public int getTowerCount() {
    return towers.count;
  }

  public Optional<TowerColor> getTowerColor() {
    return towers.color != null ? Optional.of(towers.color) : Optional.empty();
  }

  public boolean isLocked() {
    return isLocked;
  }

  @Override
  public boolean removeStudentsFromSlot(Students students) {
    return this.students.removeStudentsFromSlot(students);
  }

  @Override
  public void addStudentsToSlot(Students students) {
    this.students.addStudentsToSlot(students);
  }

  public void updateInfluences(ProfessorHolder professorHolder) {
    teamsInfluence.updateInfluence(this, professorHolder);
  }

  public TeamsInfluenceTracer getTeamsInfluenceTracer() {
    return teamsInfluence;
  }
}
