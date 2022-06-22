package it.polimi.ingsw.eriantys.cli.menus.game;

import it.polimi.ingsw.eriantys.cli.menus.MenuEnum;
import it.polimi.ingsw.eriantys.cli.views.CloudsView;
import it.polimi.ingsw.eriantys.model.enums.TurnPhase;

import java.beans.PropertyChangeEvent;

import static it.polimi.ingsw.eriantys.loggers.Loggers.clientLogger;

public class MenuPickingCloud extends MenuGame {
  public MenuPickingCloud() {
    super();
    showOptions();
  }

  @Override
  protected void showOptions() {
    showViewOptions(out);

    if (isMyTurn()) {
      if (game().isLastPlayer(me()))
        out.println("You're the last player");
      out.println("Q - Pick cloud");
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
          case "Q", "q" -> {
            if (!game().getTurnPhase().equals(TurnPhase.PICKING)) {
              clientLogger.warn("I'm not in right phase");
              break;
            }

            // Show clouds
            new CloudsView(clouds()).draw(out);

            // Gets cloud index
            out.println("Choose cloud index: ");
            int cloudIndex = getNumber() - 1; // Index correction

            boolean amILastPlayer = game().isLastPlayer(me());
            // Send action
            if (controller.sender().sendPickCloud(cloudIndex)) {
              waitForGreenLight();
              // Send refill cloud
              if (amILastPlayer)
                controller.sender().sendRefillCloud();
              return MenuEnum.PICK_ASSISTANT;
            }
            out.println("Invalid input parameters");
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
  }
}

