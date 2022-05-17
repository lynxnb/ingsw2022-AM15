package it.polimi.ingsw.eriantys.model.actions;

import it.polimi.ingsw.eriantys.model.GameService;
import it.polimi.ingsw.eriantys.model.GameState;
import it.polimi.ingsw.eriantys.model.entities.Slot;
import it.polimi.ingsw.eriantys.model.entities.Students;
import it.polimi.ingsw.eriantys.model.enums.GamePhase;
import it.polimi.ingsw.eriantys.model.enums.HouseColor;
import it.polimi.ingsw.eriantys.model.enums.TurnPhase;

public class MoveStudentsToIsland implements GameAction {
  private Students students;
  private int islandIndex;

  public MoveStudentsToIsland(Students students, int islandIndex) {
    this.students = students;
    this.islandIndex = islandIndex;
  }

  /**
   * moves students from entrance to island,
   * if this is the last allowed movement it advances turn phase
   */
  @Override
  public void apply(GameState gameState) {
    Slot currEntrance = gameState.getCurrentPlayer().getDashboard().getEntrance();
    Slot destination = gameState.getPlayingField().getIsland(islandIndex);
    StudentsMovement move = new StudentsMovement(students, currEntrance, destination);
    GameService.placeStudents(move);
    if (gameState.getCurrentPlayer().getDashboard().getEntrance().getCount()
            <= gameState.getRuleBook().entranceSize - gameState.getRuleBook().playableStudentCount)
      gameState.advanceTurnPhase();
  }

  /**
   * Checks if the current player's entrance has enough students for action
   */
  @Override
  public boolean isValid(GameState gameState) {
    Students currEntrance = gameState.getCurrentPlayer().getDashboard().getEntrance();
    for (var color : HouseColor.values()) {
      if (!currEntrance.hasEnough(color, students.getCount(color)))
        return false;
    }
    if (students.getCount() > gameState.getRuleBook().playableStudentCount)
      return false;
    return gameState.getTurnPhase() == TurnPhase.PLACING &&
            gameState.getGamePhase() == GamePhase.ACTION;
  }
}
