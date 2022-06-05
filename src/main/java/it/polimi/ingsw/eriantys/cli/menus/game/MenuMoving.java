package it.polimi.ingsw.eriantys.cli.menus.game;

import it.polimi.ingsw.eriantys.cli.menus.MenuEnum;
import it.polimi.ingsw.eriantys.cli.views.IslandsView;
import it.polimi.ingsw.eriantys.model.enums.TurnPhase;

import java.beans.PropertyChangeEvent;

public class MenuMoving extends MenuGame {
  public MenuMoving() {
    super();
  }

  @Override
  protected void showOptions() {
    showViewOptions(out);
    if (controller.getGameState().isTurnOf(controller.getNickname())) {
      out.println("T - Move mother nature");
    }
  }

  @Override
  public MenuEnum show() {

    while (true) {
      showOptions();

      String choice = getNonBlankString();

      handleViewOptions(choice);

      if (controller.getGameState().isTurnOf(controller.getNickname())) {
        switch (choice) {
          // Move mother nature a certain amount
          case "T", "t" -> {
            // Check of the Turn phase
            if (!game.getTurnPhase().equals(TurnPhase.MOVING))
              break;

            // Shows islands
            out.println("Playing Field: ");
            new IslandsView(islands, motherPosition).draw(out);

            // Gets the amount
            out.println("Insert the amount of mother nature movements: ");
            int amount = getNumber();

            // Send action
            if (controller.sender().sendMoveMotherNature(amount)) {
              waitForGreenLight();
              return MenuEnum.PICKING_CLOUD;
            }
            out.println("Invalid input parameters");
          }

          default -> {
          }
        }
      }
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    super.propertyChange(evt);
    greenLight = true;

    clearConsole();

    new IslandsView(islands, motherPosition).draw(out);
  }
}
