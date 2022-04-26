package it.polimi.ingsw.eriantys.model.entities.character_cards.influence_modifiers;

import it.polimi.ingsw.eriantys.model.GameState;
import it.polimi.ingsw.eriantys.model.IGameService;
import it.polimi.ingsw.eriantys.model.entities.Island;
import it.polimi.ingsw.eriantys.model.entities.TeamsInfluenceTracer;
import it.polimi.ingsw.eriantys.model.entities.character_cards.CharacterCard;
import it.polimi.ingsw.eriantys.model.enums.TowerColor;

import java.util.List;
import java.util.Optional;

public class IgnoreTowers implements InfluenceModifierCC, CharacterCard {
  private final static int BASE_COST = 3;
  private final static int INCREMENTED_COST = 4;
  private static int cost = BASE_COST;

  @Override
  public void applyModifier(GameState gameState) {
    List<Island> islands = gameState.getPlayingField().getIslands();

    // For each island, updates the influence by reducing it by the number of tower for the team conqueror
    islands.forEach(island ->{
      int modifier = 0;
      Optional<TowerColor> conquerorTeam = island.getTowerColor();
      TeamsInfluenceTracer teamsInfluence = island.getTeamsInfluenceTracer();
      if(conquerorTeam.isPresent()) {
        modifier -= island.getTowerCount();
        teamsInfluence.setInfluence(conquerorTeam.get(), teamsInfluence.getInfluence(conquerorTeam.get()) + modifier);
      }
    });
  }

  @Override
  public void applyEffect(GameState gameState, IGameService gameService) {
    applyModifier(gameState);
    gameState.getCurrentPlayer().removeCoins(cost);
    gameState.getPlayingField().addCoinsToBank(cost);
    cost = INCREMENTED_COST;
  }

  @Override
  public int getCost() {
    return cost;
  }

  @Override
  public boolean requiresInput() {
    return false;
  }

  @Override
  public boolean isValid(GameState gameState) {
    return gameState.getCurrentPlayer().getCoins() >= cost;
  }
}