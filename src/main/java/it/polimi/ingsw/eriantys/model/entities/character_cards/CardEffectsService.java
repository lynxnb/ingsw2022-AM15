package it.polimi.ingsw.eriantys.model.entities.character_cards;

import it.polimi.ingsw.eriantys.model.entities.*;
import it.polimi.ingsw.eriantys.model.enums.HouseColor;
import it.polimi.ingsw.eriantys.model.enums.TowerColor;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CardEffectsService implements ICardEffectsService {
  private static ICardEffectsService cardEffects = null;

  public static ICardEffectsService getCardEffectsService() {
    if (cardEffects == null)
      cardEffects = new CardEffectsService();
    return cardEffects;
  }

  @Override
  public void addToInfluence(int amount, List<Island> islands, TowerColor currTeam) {
    List<TeamsInfluenceTracer> teamsInfluenceList = new ArrayList<>();

    islands.forEach((island) ->
            teamsInfluenceList.add(island.getTeamsInfluenceTracer()));

    teamsInfluenceList.forEach((teamsInfluence) -> {
      int modifiedValue = teamsInfluence.getInfluence(currTeam) + amount;
      Logger.debug("New value: " + modifiedValue);
      teamsInfluence.setInfluence(currTeam, modifiedValue);
    });
  }

  @Override
  public void ignoreTowers(List<Island> islands) {
    // For each island, updates the influence by reducing it by the number of tower for the team conqueror
    islands.forEach(island -> {
      int modifier = 0;
      Optional<TowerColor> conquerorTeam = island.getTowerColor();
      TeamsInfluenceTracer teamsInfluence = island.getTeamsInfluenceTracer();
      if (conquerorTeam.isPresent()) {
        modifier -= island.getTowerCount();
        teamsInfluence.setInfluence(conquerorTeam.get(), teamsInfluence.getInfluence(conquerorTeam.get()) + modifier);
      }
    });
  }

  @Override
  public void ignoreColor(List<Island> islands, HouseColor ignoredColor, TowerColor teamOwningIgnoredProfessor) {
    // For each island updates teams' influence based on the ignored color
    islands.forEach((island) -> {
      TeamsInfluenceTracer influenceTracer = island.getTeamsInfluenceTracer();
      Integer ignoredStudentsCount = island.getStudents().getCount(ignoredColor);
      influenceTracer.setInfluence(teamOwningIgnoredProfessor, influenceTracer.getInfluence(teamOwningIgnoredProfessor) - ignoredStudentsCount);
    });
  }

  @Override
  public void dropStudents(List<Students> diningList, HouseColor color, int amount, StudentBag bag) {
    for (var entrance : diningList) {
      for (int i = 0; i < amount; i++) {
        if (entrance.getCount(color) != 0) {
          entrance.tryRemoveStudent(color);
          bag.addStudent(color);
        }
      }
    }
  }

  @Override
  public void forceMotherNatureEffects(int islandIndex, PlayingField field, List<Player> players) {

    if (field.getIsland(islandIndex).isLocked()) {
      field.getIsland(islandIndex).setLocked(false);
      field.setLocks(field.getLocks() + 1);
    } else {
      Optional<TowerColor> mostInfluentialTeam = field.getMostInfluential(islandIndex);
      Island currIsland = field.getIsland(islandIndex);

      if (mostInfluentialTeam.isPresent()) {
        // Set tower color
        TowerColor oldColor = currIsland.getTowerColor().get();
        currIsland.setTowerColor(mostInfluentialTeam.get());

        // If old color != new color => manage player towers
        if (!oldColor.equals(mostInfluentialTeam.get())) {
          for (Player p : players) {

            // Remove towers from conquerors' dashboard
            if (p.getColorTeam() == mostInfluentialTeam.get()) {
              p.getDashboard().removeTowers(currIsland.getTowerCount());
            }

            // Add towers to conquered dashboard
            if (p.getColorTeam() == oldColor) {
              p.getDashboard().addTowers(currIsland.getTowerCount());
            }
          }
        }
        field.mergeIslands(islandIndex);
      }
    }
  }

  @Override
  public void addToMotherNatureMoves(Player currPlayer, int amount) {
    currPlayer.addToMaxMovement(amount);
  }

  @Override
  public void lockIsland(Island island) {
    island.setLocked(true);
  }

  @Override
  public void stealProfessor(Dashboard currPlayerDashboard, List<Dashboard> dashboards, ProfessorHolder professorHolder) {
    Arrays.stream(HouseColor.values()).forEach((color) -> {
      for (var dash : dashboards) {

        // If the curr player has the same amount of students in the dining hall of the other player
        if (currPlayerDashboard.getDiningHall().getCount(color) == dash.getDiningHall().getCount(color)
                // and that player had that specific professor
                && professorHolder.hasProfessor(dash.getTowers().color, color)) {
          // The curr player steals the professor
          professorHolder.setProfessorHolder(currPlayerDashboard.getTowers().color, color);
        }
      }
    });
  }
}
