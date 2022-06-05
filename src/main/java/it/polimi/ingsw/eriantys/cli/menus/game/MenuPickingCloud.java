package it.polimi.ingsw.eriantys.cli.menus.game;

import it.polimi.ingsw.eriantys.cli.menus.MenuEnum;
import it.polimi.ingsw.eriantys.cli.views.CloudsView;
import it.polimi.ingsw.eriantys.model.enums.TurnPhase;

import java.beans.PropertyChangeEvent;

import static it.polimi.ingsw.eriantys.controller.Controller.getController;

public class MenuPickingCloud extends MenuGame {
  public MenuPickingCloud() {
//    this.nextMenu = new MenuPickAssistantCard(controller);
    showOptions();
    out.print("Make a choice: ");

  }

  @Override
  protected void showOptions() {
    showViewOptions(out);
    if (controller.getGameState().isTurnOf(controller.getNickname())) {
      out.println("Q - Pick cloud");
    } else {
      out.println("It's not your turn, you can see the state of the game tho.");
    }
  }

  @Override
  public MenuEnum show() {

    while (true) {

      String choice = getNonBlankString();

      handleViewOptions(choice);
      switch (choice) {
        case "Q", "q" -> {
          if (!game.getTurnPhase().equals(TurnPhase.PICKING))
            break;

          // Show clouds
          (new CloudsView(controller.getGameState().getPlayingField().getClouds())).draw(out);

          // Gets cloud index
          out.println("Choose cloud index: ");
          int cloudIndex = getNumber();

          // Send action
          if (controller.sender().sendPickCloud(cloudIndex)) {
            waitForGreenLight();
            return MenuEnum.PICK_ASSISTANT;
          }
          out.println("Invalid input parameters");
        }
        default -> out.println("Choose a valid option");
      }
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    super.propertyChange(evt);
    greenLight = true;
  }
}

