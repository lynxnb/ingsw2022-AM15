package it.polimi.ingsw.eriantys.model.actions;

import it.polimi.ingsw.eriantys.model.GameService;
import it.polimi.ingsw.eriantys.model.GameState;
import it.polimi.ingsw.eriantys.model.RuleBook;
import it.polimi.ingsw.eriantys.model.RuleBook;
import it.polimi.ingsw.eriantys.model.entities.Player;
import it.polimi.ingsw.eriantys.model.entities.ProfessorHolder;
import it.polimi.ingsw.eriantys.model.entities.Slot;
import it.polimi.ingsw.eriantys.model.entities.Students;
import it.polimi.ingsw.eriantys.model.enums.GameMode;
import it.polimi.ingsw.eriantys.model.enums.GamePhase;
import it.polimi.ingsw.eriantys.model.enums.HouseColor;
import it.polimi.ingsw.eriantys.model.enums.TurnPhase;

import java.util.List;

public class MoveStudentsToDiningHall extends GameAction {
  private final Students students;

  public MoveStudentsToDiningHall(Students students) {
    this.students = students;
  }

  /**
   * Moves students from entrance to dining hall, updates ProfessorHolder. <br>
   * If this is the last allowed movement it advances turn phase.
   */
  @Override
  public void apply(GameState gameState) {
    // Moves students
    Player player = gameState.getCurrentPlayer();
    Slot currEntrance = player.getDashboard().getEntrance();
    Slot destination = player.getDashboard().getDiningHall();

    //In expert mode, if a student was placed on a coin icon give the player a coin
    if (gameState.getRuleBook().gameMode == GameMode.EXPERT) {
      GameService.updatePlayerCoins(students, player);
    }

    StudentsMovement move = new StudentsMovement(students, currEntrance, destination);
    GameService.placeStudents(move);

    // Updates professor holder
    ProfessorHolder professorHolder = gameState.getPlayingField().getProfessorHolder();

    GameService.updateProfessors(professorHolder, gameState.getDashboards());

    // Manage advance phase
    Students entrance = gameState.getCurrentPlayer().getDashboard().getEntrance();
    RuleBook rules = gameState.getRuleBook();

    if (entrance.getCount() == rules.entranceSize - rules.playableStudentCount)
      gameState.advanceTurnPhase();
  }

  /**
   * Checks if the current player's entrance has enough students for action
   * and if the amount of students wanted to be moved are less than that the one left to
   * move according to the rules
   */
  @Override
  public boolean isValid(GameState gameState) {
    Students currEntrance = gameState.getCurrentPlayer().getDashboard().getEntrance();
    Students currHall = gameState.getCurrentPlayer().getDashboard().getDiningHall();
    for (var color : HouseColor.values()) {
      if (!currEntrance.hasEnough(color, students.getCount(color)))
        return false;
      if (currHall.getCount(color) + students.getCount(color) > RuleBook.HALL_TABLE_SIZE)
        return false;
    }

    int studentsLeftToMove = currEntrance.getCount() - (gameState.getRuleBook().entranceSize - gameState.getRuleBook().playableStudentCount);

    if (students.getCount() > studentsLeftToMove)
      return false;

    if (students.getCount() > gameState.getRuleBook().playableStudentCount)
      return false;

    return gameState.getTurnPhase() == TurnPhase.PLACING &&
            gameState.getGamePhase() == GamePhase.ACTION;
  }
}
