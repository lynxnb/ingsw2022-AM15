package it.polimi.ingsw.eriantys.gui.controllers.section_handlers.character_cards;

import it.polimi.ingsw.eriantys.controller.Controller;
import it.polimi.ingsw.eriantys.gui.controllers.section_handlers.DebugScreenHandler;
import it.polimi.ingsw.eriantys.gui.controllers.utils.DataFormats;
import it.polimi.ingsw.eriantys.model.GameState;
import it.polimi.ingsw.eriantys.model.entities.character_cards.CharacterCard;
import it.polimi.ingsw.eriantys.model.entities.character_cards.CharacterCardEnum;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

public class IslandInputCardHandler extends CharacterCardHandler {

  private Label draggableItem;
  private final StackPane cardsPanel;

  public IslandInputCardHandler(StackPane cardPane, CharacterCard card, ImageView crossImg, StackPane cardsPanel, DebugScreenHandler debug) {
    super(cardPane, card, crossImg, debug);
    this.cardsPanel = cardsPanel;
  }

  /**
   * - Calls super.refresh()<br>
   * - Refreshes visibility of the draggable icon.<br>
   * - If there is an attached text to the icon it refreshes it
   */
  @Override
  protected void refresh() {
    super.refresh();
    GameState gameState = Controller.get().getGameState();
    if (!Controller.get().getNickname().equals(gameState.getCurrentPlayer().getNickname())) {
      draggableItem.setVisible(false);
      return;
    }
    CharacterCard playedCard = gameState.getPlayingField().getPlayedCharacterCard();
    // make draggable item visible if this is the chosen CC
    draggableItem.setVisible(playedCard != null && playedCard.getCardEnum() == card.getCardEnum());
    if (card.getCardEnum() == CharacterCardEnum.LOCK_ISLAND) {
      draggableItem.setText("×" + gameState.getPlayingField().getLocks());
    }
  }

  /**
   * Creates a draggable item and creates drag event handlers
   */
  @Override
  protected void create() {
    super.create();
    draggableItem = new Label();
    ImageView graphic = new ImageView();
    graphic.setFitWidth(60);
    graphic.setPreserveRatio(true);
    draggableItem.setVisible(false);

    if (card.getCardEnum() == CharacterCardEnum.LOCK_ISLAND) {
      graphic.setImage(new Image("/assets/realm/lock-icon.png", 60, 0, true, false));
      draggableItem.setText("×" + Controller.get().getGameState().getPlayingField().getLocks());
      draggableItem.getStyleClass().add("label-cc-item");
    } else
      graphic.setImage(new Image("/assets/realm/mother-nature.png", 60, 0, true, false));

    draggableItem.setOnDragDetected((e) -> {
      debug.showMessage(card.getCardEnum() + " drag detected");
      ColorAdjust grayEffect = new ColorAdjust();
      grayEffect.setSaturation(-1);
      draggableItem.setEffect(grayEffect);
      Dragboard db = graphic.startDragAndDrop(TransferMode.MOVE);
      ClipboardContent content = new ClipboardContent();
      content.put(DataFormats.CARD_TO_ISLAND.format, card.getCardEnum());
      db.setContent(content);
      db.setDragView(graphic.getImage());
      // hides character card panel if there is a drag event
      cardsPanel.setVisible(false);
    });

    // show character card panel at the end of a drag event
    draggableItem.setOnDragDone((e) -> {
      draggableItem.setEffect(null);
      cardsPanel.setVisible(e.isDropCompleted());
    });
    draggableItem.setGraphic(graphic);
    cardPane.getChildren().add(draggableItem);
  }

}
