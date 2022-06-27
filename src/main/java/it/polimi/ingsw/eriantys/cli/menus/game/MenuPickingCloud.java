package it.polimi.ingsw.eriantys.cli.menus.game;

import it.polimi.ingsw.eriantys.cli.menus.MenuEnum;
import it.polimi.ingsw.eriantys.cli.views.DashboardView;
import it.polimi.ingsw.eriantys.model.enums.TurnPhase;

import java.beans.PropertyChangeEvent;

import static it.polimi.ingsw.eriantys.cli.utils.PrintUtils.colored;
import static it.polimi.ingsw.eriantys.model.enums.HouseColor.RED;

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
      if (!turnPhase().equals(TurnPhase.PICKING)) {
        // out.println(colored("You're in the wrong phase.", RED));
        return null;
      }

      String choice = getNonBlankString();

      handleViewOptions(choice);
      if (handleDisconnection(choice))
        return MenuEnum.CREATE_OR_JOIN;

      if (isMyTurn()) {
        switch (choice) {
          case "forced_advancement_to_next_menu" -> {
            return null;
          }

          case "Q", "q" -> {
            if (!turnPhase().equals(TurnPhase.PICKING)) {
              out.println(colored("You're in the wrong phase.", RED));
              break;
            }

            // Show my dashboard
            new DashboardView(me(), rules(), professorHolder()).draw(out);
            // Show clouds
            cloudsView().draw(out);

            // Gets cloud index
            out.print("Choose cloud index: ");
            int cloudIndex = getNumber() - 1; // Index correction

            // Send action PickCloud
            if (controller.sender().sendPickCloud(cloudIndex)) {
              waitForGreenLight();
              return MenuEnum.PICK_ASSISTANT;
            }
            out.println(colored("Invalid input parameters", RED));
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

