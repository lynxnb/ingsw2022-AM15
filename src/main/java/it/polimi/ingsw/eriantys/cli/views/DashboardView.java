package it.polimi.ingsw.eriantys.cli.views;

import it.polimi.ingsw.eriantys.cli.CustomPrintStream;
import it.polimi.ingsw.eriantys.cli.utils.BoxSymbols;
import it.polimi.ingsw.eriantys.model.RuleBook;
import it.polimi.ingsw.eriantys.model.entities.Dashboard;
import it.polimi.ingsw.eriantys.model.entities.Player;
import it.polimi.ingsw.eriantys.model.entities.ProfessorHolder;
import it.polimi.ingsw.eriantys.model.entities.Students;
import it.polimi.ingsw.eriantys.model.enums.HouseColor;
import org.fusesource.jansi.Ansi;

import java.util.LinkedList;

import static it.polimi.ingsw.eriantys.cli.utils.PrintUtils.*;

/**
 * A class that draws a {@link Dashboard} to the given PrintStream.
 */
public class DashboardView extends View {
  private final int MAX_TOWER;
  private final int MAX_STUDENTS;

  private final Dashboard dashboard;
  private final Player player;
  private final ProfessorHolder professors;

  public DashboardView(Player player, RuleBook ruleBook, ProfessorHolder professors) {
    this.MAX_TOWER = ruleBook.dashboardTowerCount;
    this.MAX_STUDENTS = ruleBook.entranceSize;
    this.dashboard = player.getDashboard();
    this.player = player;
    this.professors = professors;
  }

  @Override
  public void draw(CustomPrintStream o) {
    // Separator
    o.println();

    // Draw content
    o.append(drawDashboard());
  }

  public String drawDashboard() {
    StringBuilder stringBuilder = new StringBuilder();
    LinkedList<HouseColor> entranceList = dashboard.getEntrance().toLinkedList();
    Students dining = dashboard.getDiningHall();

    // First row
    stringBuilder
        .append(printCentredNickname(player.getNickname() + "'s─dashboard"))
        .append(System.lineSeparator());

    // Second row
    stringBuilder.append("├───────╦──1-2-3-4-5-6-7-8-9-10─╤─────╦───────┤").append(System.lineSeparator());

    // Content rows
    for (var color : HouseColor.values()) {
      stringBuilder.append(BoxSymbols.VERTICAL.glyph)
          .append(PADDING_DOUBLE)
          .append(getEntranceOccupation(color.ordinal(), entranceList))
          .append(PADDING)
          .append(BoxSymbols.DOUBLE_VERTICAL.glyph)
          .append(PADDING_DOUBLE)
          .append(getDiningOccupation(dining.getCount(color), color))
          .append(PADDING_DOUBLE)
          .append(BoxSymbols.VERTICAL.glyph)
          .append(PADDING_DOUBLE)
          .append(getProfOccupation(professors.hasProfessor(dashboard.towerColor(), color), color))
          .append(PADDING_DOUBLE)
          .append(BoxSymbols.DOUBLE_VERTICAL.glyph)
          .append(PADDING_DOUBLE)
          .append(getTowerOccupation(color.ordinal(), dashboard.towerCount()))
          .append(PADDING_DOUBLE)
          .append(BoxSymbols.VERTICAL.glyph)
          .append(System.lineSeparator());
    }

    // Last row
    stringBuilder.append("╰───────╩───────────────────────╧─────╩───────╯").append(System.lineSeparator());

    return stringBuilder.toString();
  }

  private String getEntranceOccupation(int row, LinkedList<HouseColor> entrance) {
    StringBuilder out = new StringBuilder();

    if (row <= MAX_STUDENTS / 2) {
      for (int i = 0; i < 2 && i < MAX_STUDENTS; i++) {
        HouseColor nextStudent = entrance.poll();
        if (nextStudent == null) {
          if ((row * 2 + i) > MAX_STUDENTS - 1)
            out.append(PADDING_DOUBLE);
          else
            out.append(EMPTY_CHAR + PADDING);
        } else {
          out.append(colored(STUDENT_CHAR, nextStudent)).append(PADDING);
        }
      }
    } else {
      return PADDING_TRIPLE + PADDING;
    }

    return out.toString();
  }

  private String getDiningOccupation(int count, HouseColor c) {
    return (getColorString(c) + (STUDENT_CHAR + " ").repeat(count)
        + (EMPTY_CHAR + " ").repeat(10 - count)).stripTrailing()
        + Ansi.ansi().reset().toString();
  }

  private String getProfOccupation(boolean isProf, HouseColor color) {
    return colored(isProf ? PROF_CHAR : EMPTY_CHAR, color);
  }

  private String getTowerOccupation(int row, int towerCount) {
    if (row < MAX_TOWER / 2) {
      int remaining = towerCount - row * 2;
      int emptyRemaining = (row + 1) * 2 - towerCount;

      return ((colored(TOWER_CHAR, dashboard.towerColor()) + " ").repeat(clamp(remaining, 0, 2))
          + (EMPTY_CHAR + " ").repeat(clamp(emptyRemaining, 0, 2))).stripTrailing();
    } else {
      return PADDING_TRIPLE;
    }
  }

  private String printCentredNickname(String row) {
    StringBuilder top = new StringBuilder("╭─────────────────────────────────────────────╮");
    String start = "├───────╦──1";


    return top.replace(start.length(), start.length() + row.length(), row).toString();
  }

}
