package it.polimi.ingsw.eriantys.model.actions;

import it.polimi.ingsw.eriantys.model.GameService;
import it.polimi.ingsw.eriantys.model.GameState;
import it.polimi.ingsw.eriantys.model.IGameService;
import it.polimi.ingsw.eriantys.model.PlayerAction;
import it.polimi.ingsw.eriantys.model.entities.Dashboard;
import it.polimi.ingsw.eriantys.model.entities.Player;
import it.polimi.ingsw.eriantys.model.entities.Students;
import it.polimi.ingsw.eriantys.model.enums.GamePhase;
import it.polimi.ingsw.eriantys.model.enums.HouseColor;
import it.polimi.ingsw.eriantys.model.enums.TurnPhase;

import java.util.ArrayList;

public class DropStudents extends PlayerAction {

  HouseColor studentColor;

  public DropStudents(String nickname, HouseColor color) {
    this.studentColor = color;
    this.playerNickname = nickname;
  }

  /**
   * removes 3 students of a certain HouseColor from the entrance of each player
   * then advances to next TurnPhase;
   * @param gameState
   */
  @Override
  public void apply(GameState gameState, IGameService gameService) {
    final int DROP_STUDENTS_AMOUNT = 3;

    gameService.dropStudents(gameState.getPlayers(), studentColor, DROP_STUDENTS_AMOUNT);
//    for (Player p : gameState.getPlayers()) {
//      Students entranceStudents = p.getDashboard().getEntrance();
//      for (int i = 0 ; i < DROP_STUDENTS_AMOUNT ; i++){
//        if(entranceStudents.getCount(studentColor) != 0)
//          entranceStudents.tryRemoveStudent(studentColor);
//      }
//    }
    gameState.advanceTurnPhase();
  }

  /**
   * checks:<br/>
   * - If current player is the player who did the action<br/>
   * - If the gamePhase is ACTION<br/>
   * - If the turnPhase is EFFECT<br/>
   * @param gameState
   * @return boolean
   */
  @Override
  public boolean isValid(GameState gameState) {
    return gameState.getCurrentPlayer().getNickname().equals(playerNickname) &&
            gameState.getGamePhase() == GamePhase.ACTION &&
            gameState.getTurnPhase() == TurnPhase.EFFECT;
  }
}
