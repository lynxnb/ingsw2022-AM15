package it.polimi.ingsw.eriantys.controller.menus.planning;

import it.polimi.ingsw.eriantys.cli.views.AssistantCardsView;
import it.polimi.ingsw.eriantys.controller.Controller;
import it.polimi.ingsw.eriantys.controller.menus.Menu;
import it.polimi.ingsw.eriantys.controller.menus.ParamBuilder;
import it.polimi.ingsw.eriantys.controller.menus.action.MenuPlacing;
import it.polimi.ingsw.eriantys.model.GameState;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuPickAssistantCard extends Menu {

  public MenuPickAssistantCard(GameState game, String playerNickname, Controller controller) {
    this.game = game;
    this.playerNickname = playerNickname;
    this.controller = controller;
  }

  @Override
  public void showOptions() {
    showViewOptions();
    if (playerNickname.equals(game.getCurrentPlayer().getNickname())) {
      System.out.println("A - Choose assistant card");
    }
  }

  @Override
  public void makeChoice(ParamBuilder paramBuilder) {
    Scanner s = new Scanner(System.in);
    boolean done = false;

    do {
      showOptions();
      switch (s.nextLine()) {
        case "A", "a" -> {
          (new AssistantCardsView(game.getPlayer(playerNickname))).draw(System.out);
          int index = -1;
          try {
            System.out.print("Choose card index:");

            done = true;
          } catch (InputMismatchException e) {
            System.out.println("Please insert a number");
          }
          if (!controller.sendPickAssistantCard(index)) {
            System.out.println("Invalid input parameters.");
          }
        }
        default -> System.out.println("Choose a valid option");
      }
    } while (!done);
  }

  @Override
  public Menu nextMenu() {
    return (new MenuPlacing(game, playerNickname, controller));
  }
}