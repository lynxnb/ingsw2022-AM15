package it.polimi.ingsw.eriantys.gui.controllers;

import it.polimi.ingsw.eriantys.controller.Controller;
import it.polimi.ingsw.eriantys.gui.SceneEnum;
import it.polimi.ingsw.eriantys.gui.controllers.section_handlers.*;
import it.polimi.ingsw.eriantys.model.entities.Player;
import it.polimi.ingsw.eriantys.model.enums.GameMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import jfxtras.scene.layout.CircularPane;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.eriantys.controller.EventType.*;
import static it.polimi.ingsw.eriantys.loggers.Loggers.clientLogger;

public class GameSceneController extends FXMLController {
  @FXML
  private TilePane characterCardsTiles;
  @FXML
  private Button playCCButton;
  @FXML
  private StackPane characterCardsPanel;
  @FXML
  private AnchorPane dashboardClient;
  @FXML
  private AnchorPane dashboardClient2;
  @FXML
  private AnchorPane dashboardClient3;
  @FXML
  private AnchorPane dashboardClient4;
  @FXML
  private TilePane dashboardTowers;
  @FXML
  private TilePane dashboardTowers2;
  @FXML
  private TilePane dashboardTowers3;
  @FXML
  private TilePane dashboardTowers4;
  @FXML
  private GridPane profTableGrid;
  @FXML
  private GridPane profTableGrid2;
  @FXML
  private GridPane profTableGrid3;
  @FXML
  private GridPane profTableGrid4;
  @FXML
  private GridPane studentHallGrid;
  @FXML
  private GridPane studentHallGrid2;
  @FXML
  private GridPane studentHallGrid3;
  @FXML
  private GridPane studentHallGrid4;
  @FXML
  private GridPane entranceGrid;
  @FXML
  private GridPane entranceGrid2;
  @FXML
  private GridPane entranceGrid3;
  @FXML
  private GridPane entranceGrid4;
  @FXML
  private VBox debugScreen;
  @FXML
  private VBox cloudBox;
  @FXML
  private CircularPane islandsPane;
  @FXML
  private AnchorPane rootPane;
  @FXML
  private GridPane otherPlayersGrid;
  @FXML
  private StackPane assistCardPanel;
  @FXML
  private TilePane assistCards;
  @FXML
  private VBox playedCardsBox;

  private PlayerGridHandler playerGridHandler;
  private DashboardHandler mainDashboardHandler;
  private final List<EnemyDashboardHandler> enemyDashboardHandlers = new ArrayList<>();
  private CloudsHandler cloudBoxHandler;
  private IslandsHandler islandsPaneHandler;
  private AssistCardHandler assistCardTilesHandler;
  private DebugScreenHandler debugScreenHandler;
  private CharacterCardsHandler characterCardsHandler;

  @Override
  public void start() {
    super.start();
    Controller.get().addListener(this, GAMEDATA_EVENT.tag);
    Controller.get().addListener(this, PLAYER_CONNECTION_CHANGED.tag);
    Controller.get().addListener(this, INTERNAL_SOCKET_ERROR.tag);
    debugScreenHandler = new DebugScreenHandler(debugScreen);
    mainDashboardHandler = new DashboardHandler(Controller.get().getNickname(), studentHallGrid, entranceGrid, profTableGrid, dashboardTowers, debugScreenHandler);
    buildDashboardHandlers();
    playerGridHandler = new PlayerGridHandler(otherPlayersGrid, debugScreenHandler, enemyDashboardHandlers);
    cloudBoxHandler = new CloudsHandler(cloudBox, debugScreenHandler);
    islandsPaneHandler = new IslandsHandler(islandsPane, debugScreenHandler);
    assistCardTilesHandler = new AssistCardHandler(assistCards, playedCardsBox, debugScreenHandler);
    if (Controller.get().getGameState().getRuleBook().gameMode == GameMode.EXPERT)
      buildForExpertMode();
  }

  private void buildDashboardHandlers() {
    List<String> nicknames = Controller.get().getGameState().getPlayers().stream().map(Player::getNickname)
            .filter(nickname -> !nickname.equals(Controller.get().getNickname())).toList();
    try {
      enemyDashboardHandlers.add(new EnemyDashboardHandler(dashboardClient2, nicknames.get(0), studentHallGrid2, entranceGrid2, profTableGrid2, dashboardTowers2, debugScreenHandler));
      enemyDashboardHandlers.add(new EnemyDashboardHandler(dashboardClient3, nicknames.get(1), studentHallGrid3, entranceGrid3, profTableGrid3, dashboardTowers3, debugScreenHandler));
      enemyDashboardHandlers.add(new EnemyDashboardHandler(dashboardClient4, nicknames.get(2), studentHallGrid4, entranceGrid4, profTableGrid4, dashboardTowers4, debugScreenHandler));
    } catch (IndexOutOfBoundsException e) {
      clientLogger.debug("added less than 3 enemy dashboard handlers");
    }
  }

  private void buildForExpertMode() {
    playCCButton.setVisible(true);
    characterCardsHandler = new CharacterCardsHandler(characterCardsPanel, characterCardsTiles, debugScreenHandler);
  }

  @Override
  public void finish() {
    super.finish();
    Controller.get().removeListener(this, GAMEDATA_EVENT.tag);
    Controller.get().removeListener(this, PLAYER_CONNECTION_CHANGED.tag);
    Controller.get().removeListener(this, INTERNAL_SOCKET_ERROR.tag);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getNewValue() != null)
      debugScreenHandler.showMessage((String) evt.getNewValue());
    if (evt.getPropertyName().equals(GAMEDATA_EVENT.tag))
      updateAll();
    else if (evt.getPropertyName().equals(PLAYER_CONNECTION_CHANGED.tag)) {
      playerGridHandler.update();
    } else {
      quitGameAction();
      gui.showSocketError();
    }
  }


  @Override
  public void updateAll() {
    super.updateAll();
    playerGridHandler.update();
    mainDashboardHandler.update();
    islandsPaneHandler.update();
    cloudBoxHandler.update();
    debugScreenHandler.update();
    enemyDashboardHandlers.forEach(SectionHandler::update);
    if (assistCardPanel.isVisible())
      assistCardTilesHandler.update();
    if (characterCardsPanel.isVisible())
      characterCardsHandler.update();
  }

  @FXML
  private void quitGameAction() {
    Controller.get().sender().sendQuitGame();
    gui.setScene(SceneEnum.CREATE_OR_JOIN);
  }

  @FXML
  private void showAssistCards() {
    assistCardTilesHandler.update();
    assistCardPanel.setVisible(true);
    otherPlayersGrid.getClip();
  }

  @FXML
  private void hideAssistCards() {
    assistCardPanel.setVisible(false);
  }

  @FXML
  private void toggleDebugScreen(KeyEvent event) {
    boolean isVisible = !debugScreen.isVisible();
    debugScreenHandler.update();
    if (event.getCode() == KeyCode.F3) {
      clientLogger.debug("debug screen is visible: " + isVisible);
      debugScreen.setVisible(isVisible);
    }
  }

  @FXML
  private void showCharacterCards(ActionEvent actionEvent) {
    //update coard handerl
    characterCardsHandler.update();
    characterCardsPanel.setVisible(true);
  }
}
