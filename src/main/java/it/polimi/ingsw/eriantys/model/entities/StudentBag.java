package it.polimi.ingsw.eriantys.model.entities;

import it.polimi.ingsw.eriantys.model.enums.HouseColor;

import java.io.Serializable;
import java.util.Random;

import static it.polimi.ingsw.eriantys.loggers.Loggers.modelLogger;

public class StudentBag implements Serializable {
  private final Students students;
  private final Random rand = new Random();

  public StudentBag() {
    students = new Students();
  }

  /**
   * Copy constructor which performs a deep copy of the student bag.
   *
   * @param bag The student bag to make a copy of.
   */
  public StudentBag(StudentBag bag) {
    students = bag.getStudents().getCopy();
  }

  /**
   * Initializes the amount of students in the bag to a certain amount for every color
   */
  public void initStudents(int amount) {
    students.setStudents(new Students());
    for (int i = 0; i < amount; i++) {
      for (HouseColor c : HouseColor.values())
        students.addStudent(c);
    }
  }

  public Students getStudents() {
    return students;
  }

  public void addStudent(HouseColor color) {
    students.addStudent(color);
  }

  /**
   * Returns a random student from students
   *
   * @return HouseColor
   */
  public HouseColor takeRandomStudent() {
    HouseColor student;

    do {
      student = HouseColor.values()[rand.nextInt(HouseColor.values().length)];
      if (students.getCount(student) != 0) {
        if (students.tryRemoveStudent(student)) {
          return student;
        }
      }
    } while (!students.isEmpty());
    modelLogger.error("Missing students from bag. Game should've ended.");
    return null;
  }

  /**
   * Removes given students from bag
   */
  public boolean removeStudents(Students s) {
    // try to remove the students all at once
//    if (!this.students.tryRemoveStudents(s)) {
//      // if it can't do that (no students left in the bag) try to remove them one by one
//      modelLogger.warn("GAME SHOULD END. NO STUDENTS LEFT IN THE BAG");
//      for (HouseColor c : HouseColor.values()) {
//        int count = s.getCount(c);
//        while (count > 0) {
//          count--;
//          students.tryRemoveStudent(c);
//        }
//      }
//    }
    return this.students.tryRemoveStudents(s);
  }

  public boolean isEmpty() {
    return students.isEmpty();
  }
}
