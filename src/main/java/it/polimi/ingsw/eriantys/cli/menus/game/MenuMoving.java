package it.polimi.ingsw.eriantys.cli.menus.game;

import it.polimi.ingsw.eriantys.cli.menus.MenuEnum;
import it.polimi.ingsw.eriantys.cli.views.IslandsView;
import it.polimi.ingsw.eriantys.model.enums.TurnPhase;

import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;

public class MenuMoving extends MenuGame {
  public MenuMoving() {
    super();
    showOptions();
  }

  @Override
  protected void showOptions() {
    showViewOptions(out);

    if (isMyTurn()) {
      out.println("T - Move mother nature");
    }
    out.print("Make a choice: ");
  }

  @Override
  public MenuEnum show() {

    while (true) {

      String choice = getNonBlankString();

      handleViewOptions(choice);

      if (isMyTurn()) {
        switch (choice) {
          // Move mother nature a certain amount
          case "T", "t" -> {
            // Check of the Turn phase
            if (!game().getTurnPhase().equals(TurnPhase.MOVING))
              break;

            // Shows islands
            out.println("Playing Field: ");
            new IslandsView(islands(), motherPosition()).draw(out);

            // Gets the amount
            out.print(
                MessageFormat.format("Insert the amount of mother nature movements (max {0}): ", me().getMaxMovement()));

            int amount = getNumber();

            // Send action
            if (controller.sender().sendMoveMotherNature(amount)) {
              waitForGreenLight();
              new IslandsView(islands(), motherPosition()).draw(out);
              return MenuEnum.PICKING_CLOUD;
            }
            out.println("Invalid input parameters. Valid movement:" + me().getMaxMovement() + ".");
            showOptions();
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
  }
}
