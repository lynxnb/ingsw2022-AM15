package it.polimi.ingsw.eriantys.model.entities;

import it.polimi.ingsw.eriantys.model.enums.HouseColor;
import it.polimi.ingsw.eriantys.model.enums.TowerColor;

import java.io.Serializable;
import java.util.*;

import static it.polimi.ingsw.eriantys.loggers.Loggers.modelLogger;

public record TeamsInfluenceTracer(EnumMap<TowerColor, Integer> teamsInfluence) implements Serializable {

  public void setInfluence(TowerColor team, int influence) {
    teamsInfluence.put(team, influence);
  }

  /**
   * Return the influence of the given team on this island
   *
   * @param team
   * @return influence
   */
  public Integer getInfluence(TowerColor team) {
    try {
      int influence = teamsInfluence.get(team);
      modelLogger.debug(String.valueOf(teamsInfluence.get(team)));
      return influence;
    } catch (NullPointerException e) {
      modelLogger.error("INFLUENCE HAS NOT BEEN SET YET");
      return 0;
    }
  }

  /**
   * Returns an Optional TowerColor representing the team with the most influence on this island.
   * Return empty in case of draw.
   *
   * @return Return and Optional of TowerColor
   */
  public Optional<TowerColor> getMostInfluential() {
    // Get the most influential team
    modelLogger.debug(this.toString());
    try {
      Map.Entry<TowerColor, Integer> maxEntry = Collections.max(teamsInfluence.entrySet(), Map.Entry.comparingByValue());
      // Check if 2 teams have the same influence value and return an empty Optional if so
      for (var t : teamsInfluence.entrySet()) {
        // If there's another team with the same max influence
        if (maxEntry.getValue().equals(t.getValue()) && !maxEntry.getKey().equals(t.getKey())) {
          // Then there's no most influential team
          modelLogger.debug("Most influential: " + Optional.empty());
          return Optional.empty();
        }
      }
      modelLogger.debug("Most influential: " + maxEntry.getKey());
      return Optional.of(maxEntry.getKey());
    } catch (NoSuchElementException e) {
      modelLogger.error("INFLUENCES HAVE NOT BEEN SET YET");
      return Optional.empty();
    }
  }

  /**
   * Updates the teams influence based on the given island info and the professor holder
   *
   * @param island
   * @param professorHolder
   */
  public void updateInfluence(Island island, ProfessorHolder professorHolder) {
    // Initiate influences
    for (var color : TowerColor.values()) setInfluence(color, 0);
    // Updates influences
    for (TowerColor team : TowerColor.values()) {
      int influence = 0;

      // Update influence based on color
      for (var color : HouseColor.values()) {
        if (professorHolder.hasProfessor(team, color))
          influence += island.getStudents().getCount(color);
      }
      // Update influence based on tower
      if (island.getTowerColor().isPresent() && island.getTowerColor().get().equals(team))
        influence += island.getTowerCount();

      setInfluence(team, influence);
    }
//    modelLogger.debug(this);
  }

  @Override
  public String toString() {
    StringBuilder print = new StringBuilder();
    for (var color : TowerColor.values())
      print.append("\nTeam: ").append(color).append(" Influence: ").append(teamsInfluence.get(color));
    return "\nTeamsInfluenceTracer: " + print;
  }
}
