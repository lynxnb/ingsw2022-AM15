package it.polimi.ingsw.eriantys.model.actions;

import it.polimi.ingsw.eriantys.model.GameService;
import it.polimi.ingsw.eriantys.model.GameState;
import it.polimi.ingsw.eriantys.model.entities.Player;
import it.polimi.ingsw.eriantys.model.enums.AssistantCard;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Set played the assistant card chosen by the Player.
 */
public class PickAssistantCard implements GameAction {
  private final int cardIndex;

  public PickAssistantCard(int cardIndex) {
    this.cardIndex = cardIndex;
  }

  @Override
  public void apply(GameState gameState) {
    GameService.pickAssistantCard(gameState.getCurrentPlayer(), cardIndex);

    // If all players have set the card the game continues
    Player lastPlayer = gameState.getPlanOrderPlayers().get(gameState.getPlanOrderPlayers().size() - 1);
    if (gameState.getCurrentPlayer().equals(lastPlayer)) {
      gameState.advanceGamePhase();
    }
    gameState.advancePlayer();
  }

  // todo javadoc e testing
  @Override
  public boolean isValid(GameState gameState) {
    Player currPlayer = gameState.getCurrentPlayer();
    try {
      AssistantCard chosenCard = currPlayer.getCards().get(cardIndex);

      // Saves already played cards
      List<AssistantCard> alreadyPlayedCards = new ArrayList<>();
      gameState.getPlayers().forEach(player -> {
        if (player.getChosenCard().isPresent() && !player.equals(currPlayer)) {
          Logger.debug("Card played: {}", player.getChosenCard().get());
          alreadyPlayedCards.add(player.getChosenCard().get());
        }
      });

      // Checks if the card with the same value has been already played
      for (AssistantCard c : alreadyPlayedCards) {
        if (chosenCard.equals(c)) {
          Logger.debug("Same card played {} - {}", c, chosenCard);
          // If so, if I have even one card that has not been played then it's not ok
          for (AssistantCard myCard : currPlayer.getCards()) {
            Logger.debug("Card in my hand: {}", myCard);
            if (!alreadyPlayedCards.contains(myCard)) {
              Logger.debug("it could've been played {}", myCard);
              return false;
            }
          }
        }
        Logger.debug("Not same card played {}", c);
      }
    } catch (IndexOutOfBoundsException e) {
      Logger.warn("\nIndex out of bound for Assistant card list. Message: " + e.getMessage());
      return false;
    }
    return true;
  }
}
