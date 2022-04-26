package it.polimi.ingsw.eriantys.model.entities.character_cards;

import it.polimi.ingsw.eriantys.model.GameState;
import it.polimi.ingsw.eriantys.model.IGameService;
import it.polimi.ingsw.eriantys.model.entities.PlayingField;

public class LockIsland implements CharacterCard {
  private final int index;
  private static final int BASE_COST = 2;
  private static final int INCREMENTED_COST = 3;
  private static int cost = BASE_COST;

  public LockIsland(int index) {
    this.index = index;
  }

  @Override
  public void applyEffect(GameState gameState, IGameService gameService) {
    PlayingField playingField = gameState.getPlayingField();
    gameState.getCurrentPlayer().removeCoins(cost);
    playingField.addCoinsToBank(cost);
    gameService.lockIsland(gameState.getPlayingField().getIsland(index));
    playingField.setLocks(playingField.getLocks() - 1);
    cost = INCREMENTED_COST;
  }

  @Override
  public boolean requiresInput() {
    return true;
  }

  @Override
  public int getCost() {
    return cost;
  }

  /**
   * Checks:<br>
   * - if the chosen index is allowed<br>
   * - if the chosen island is already locked<br>
   * - if player has enough coins
   */
  @Override
  public boolean isValid(GameState gameState) {
    return gameState.getPlayingField().getIslandsAmount() > index &&
            index >= 0 &&
            !gameState.getPlayingField().getIsland(index).isLocked() &&
            gameState.getCurrentPlayer().getCoins() >= cost &&
            gameState.getPlayingField().getLocks() > 0;

  }


}