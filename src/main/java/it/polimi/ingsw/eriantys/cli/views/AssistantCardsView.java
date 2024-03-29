package it.polimi.ingsw.eriantys.cli.views;

import it.polimi.ingsw.eriantys.cli.CustomPrintStream;
import it.polimi.ingsw.eriantys.model.entities.Player;
import it.polimi.ingsw.eriantys.model.enums.AssistantCard;
import it.polimi.ingsw.eriantys.model.enums.HouseColor;

import java.text.MessageFormat;
import java.util.List;

import static it.polimi.ingsw.eriantys.cli.utils.BoxSymbols.VERTICAL;
import static it.polimi.ingsw.eriantys.cli.utils.PrintUtils.PADDING;
import static it.polimi.ingsw.eriantys.cli.utils.PrintUtils.PADDING_DOUBLE;

public class AssistantCardsView extends View {
  private final List<AssistantCard> cards;
  private final int maxColumns = 4;
  private final String PADDING_FROM_EACH_CARD = PADDING_DOUBLE;


  public AssistantCardsView(Player player) {
    this.cards = player.getCards();
  }

  public AssistantCardsView(List<AssistantCard> cards) {
    this.cards = cards;
  }

  @Override
  public void draw(CustomPrintStream o) {
    StringBuilder stringBuilder = new StringBuilder();

    try {
      // Gets the height of one single stamp
      int rows = drawAssistantCard(cards.get(0), 0).split(System.lineSeparator()).length;

      // Populate the matrix
      String[][] matrix = new String[cards.size()][rows];
      for (int i = 0; i < cards.size(); i++) {
        matrix[i] = drawAssistantCard(cards.get(i), (i + 1)).split(System.lineSeparator());
      }

      int progression = 0;
      while (cards.size() - progression >= maxColumns) {

        // Build the stripes
        for (int row = 0; row < rows; row++) {
          for (int cardIndex = progression; cardIndex < progression + maxColumns; cardIndex++) {
            stringBuilder
                .append(matrix[cardIndex][row])
                .append(PADDING_FROM_EACH_CARD);
          }
          stringBuilder.append(System.lineSeparator());
        }
        progression += maxColumns;
      }

      // Build last stripes
      if (progression < cards.size()) {
        for (int row = 0; row < rows; row++) {
          for (int cardIndex = progression; cardIndex < cards.size(); cardIndex++) {
            stringBuilder
                .append(matrix[cardIndex][row])
                .append(PADDING_FROM_EACH_CARD);
          }
          stringBuilder.append(System.lineSeparator());
        }
      }
    } catch (IndexOutOfBoundsException e) {
      stringBuilder = new StringBuilder(nothingToShow());
      stringBuilder.append(System.lineSeparator());
    }

    o.append(System.lineSeparator());

    // Title of the section
    o.println(centredTitle("ASSISTANT CARDS"), HouseColor.YELLOW);

    // Write the content
    o.append(stringBuilder);

    // Writes a "-" separator
    o.println(centredTitle("-"), HouseColor.YELLOW);
  }

  private String drawAssistantCard(AssistantCard card, int index) {
    StringBuilder stringBuilder = new StringBuilder();

    // First row
    if (index < 10)
      stringBuilder.append(MessageFormat.format("╭─{0}─────────╮", index));
    else
      stringBuilder.append(MessageFormat.format("╭─{0}────────╮", index));
    stringBuilder.append(System.lineSeparator());

    // Second row
    stringBuilder
        .append(VERTICAL.glyph)
        .append(PADDING)
        .append(MessageFormat.format("Value: {0}", card.value));

    if (card.value < 10)
      stringBuilder.append(PADDING_DOUBLE);
    else
      stringBuilder.append(PADDING);

    stringBuilder
        .append(VERTICAL.glyph)
        .append(System.lineSeparator());

    // Third row
    stringBuilder
        .append(VERTICAL.glyph)
        .append(PADDING)
        .append(MessageFormat.format("Moves: {0}", card.movement));

    if (card.movement < 10)
      stringBuilder.append(PADDING_DOUBLE);
    else
      stringBuilder.append(PADDING);

    stringBuilder
        .append(VERTICAL.glyph)
        .append(System.lineSeparator());

    // Forth row
    stringBuilder.append("╰───────────╯");

    return stringBuilder.toString();

  }

  private String centredTitle(String title) {
    int baseRowLength = ("╰───────────╯" + PADDING_FROM_EACH_CARD).repeat(maxColumns).length() - PADDING_FROM_EACH_CARD.length();
    int nPadding = (baseRowLength / 2) - (int) (Math.floor((double) title.length() / 2));

    return "-".repeat(nPadding) + title + "-".repeat(nPadding - 1);
  }

  private String nothingToShow() {
    String content = " Nothing to show ";
    int baseRowLength = ("╰───────────╯" + PADDING_FROM_EACH_CARD).repeat(maxColumns).length() - PADDING_FROM_EACH_CARD.length();
    int spaces = baseRowLength - content.replace(" ", "").length();
    int nWord = content.split(" ").length;

    return content.replace(" ", " ".repeat(spaces / (nWord)));
  }
}
