package it.polimi.ingsw.eriantys.cli.menus.game;

import it.polimi.ingsw.eriantys.cli.menus.MenuEnum;
import it.polimi.ingsw.eriantys.cli.views.CharacterCardsView;
import it.polimi.ingsw.eriantys.controller.Controller;
import it.polimi.ingsw.eriantys.model.GameState;
import it.polimi.ingsw.eriantys.model.entities.character_cards.CharacterCard;
import it.polimi.ingsw.eriantys.model.entities.character_cards.CharacterCardEnum;
import it.polimi.ingsw.eriantys.model.entities.character_cards.ColorInputCards;
import it.polimi.ingsw.eriantys.model.entities.character_cards.IslandInputCards;

import java.beans.PropertyChangeEvent;
import java.util.List;

import static it.polimi.ingsw.eriantys.controller.EventType.INPUT_ENTERED;
import static it.polimi.ingsw.eriantys.model.enums.HouseColor.*;

public class MenuEffect extends MenuGame {
  public MenuEffect() {
    super();
    addListeningEvents();
  }

  private void addListeningEvents() {
    eventsToBeListening.forEach(eventType -> controller.addListener(this, eventType.tag));
  }

  private void removeListeningEvents() {
    eventsToBeListening.forEach(eventType -> controller.removeListener(this, eventType.tag));
  }

  @Override
  protected void showOptions() {
  }

  /**
   * @return MenuEnum.PLACING if a character card was played
   */
  @Override
  public MenuEnum show() {
    // Choose the cards
    if (!chooseCharacterCard())
      return null;

    // Show CC descriptions
    CharacterCard cc = game().getPlayingField().getPlayedCharacterCard();

    ParamBuilder paramBuilder = new ParamBuilder();

    while (true) {
      // If the card requires color input
      if (cc instanceof ColorInputCards) {
        // Get color input
        out.print("Insert color. ");
        new MenuStudentColor().show(paramBuilder);
        ((ColorInputCards) cc).setColor(paramBuilder.getChosenColor());
      }

      // If the card requires island index input
      if (cc instanceof IslandInputCards) {
        // View islands
        islandsView().draw(out);
        // Get island index input
        out.print("Insert island index: ");
        int index = getNumber() - 1;  // Index correction
        ((IslandInputCards) cc).setIslandIndex(index);
      }

      // Send the action<
      if (controller.sender().sendActivateEffect(cc)) {
        waitForGreenLight();
        out.println("Card activated. ", GREEN);
        new CharacterCardsView(List.of(cc)).draw(out);
        if (studentsLeftToMove() == 0) {
          removeListeningEvents();
          return MenuEnum.MOVING;
        }
      }

      // Print reasons why action is invalid
      boolean isUsed = game().getPlayingField().isCharacterCardUsed(cc.getCardEnum());
      if (me().getCoins() < cc.getCost(isUsed))
        out.println("Not enough coins", YELLOW);
      else
        out.println("You're in the wrong phase.", RED);
    }

  }

  private int studentsLeftToMove() {
    int myCount = me().getDashboard().getEntrance().getCount();
    int finalCount = rules().entranceSize - rules().playableStudentCount;
    return myCount - finalCount;
  }

  private boolean chooseCharacterCard() {
    // Show playable CC
    out.println();
    while (true) {
      playersView().draw(out);
      characterCardsView().draw(out);

      // Choose CC
      out.print("Choose a character card: ");
      int ccIndex = getNumber();

      out.println("Card chosen: ");
      new CharacterCardsView(List.of(ccs().get(ccIndex))).draw(out);
      out.print("Description: ");
      out.println(ccs().get(ccIndex).getCardEnum().getDescription());
      out.println();

      String choice;
      do {
        out.print("Continue (y/n)? ");
        choice = getNonBlankString();
      } while (!choice.equalsIgnoreCase("y") && !choice.equalsIgnoreCase("n"));

      if (choice.equalsIgnoreCase("n"))
        return false;

      // Purchasable check
      CharacterCardEnum chosenCard = ccs().get(ccIndex).getCardEnum();
      boolean isUsed = game().getPlayingField().isCharacterCardUsed(chosenCard);
      if (me().getCoins() < ccs().get(ccIndex).getCost(isUsed)) {
        out.println("Not enough coins", RED);
        return false;
      }

      // Send the action
      if (controller.sender().sendChooseCharacterCard(ccIndex)) {
        waitForGreenLight();
        out.println("Card chosen.", GREEN);
        return true;
      }
      out.println("Choose a valid card or not enough coins", RED);
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    super.propertyChange(evt);
    if (evt.getPropertyName().equals(INPUT_ENTERED.tag))
      inputGreenLight = true;
    greenLight = true;
  }
}
