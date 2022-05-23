package it.polimi.ingsw.eriantys.gui.controllers;

import it.polimi.ingsw.eriantys.gui.SceneEnum;
import it.polimi.ingsw.eriantys.network.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.tinylog.Logger;

import java.beans.PropertyChangeEvent;

public class ConnectionController extends FXMLController {
  @FXML
  private Button backButton;
  @FXML
  private Button confirm;
  @FXML
  private Label errorMessage;
  @FXML
  private TextField serverIpField;
  @FXML
  private TextField serverPortField;

  @FXML
  private void backButtonAction(ActionEvent actionEvent) {
    gui.setScene(SceneEnum.MENU);
  }

  /**
   *
   * @param actionEvent
   */
  @FXML
  private void confirmButtonAction(ActionEvent actionEvent) {
    String ipStr = serverIpField.getText();
    String portStr = serverPortField.getText();
    int port = Client.DEFAULT_PORT;
    try {
      if (!portStr.isEmpty())
        port = Integer.parseInt(portStr);
      if (!gui.getController().connect(ipStr, port)) {
        errorMessage.setText("Failed to connect to the server");
        errorMessage.setOpacity(1);
      } else gui.setScene(SceneEnum.CREATE_OR_JOIN);

    } catch (NumberFormatException e) {
      errorMessage.setText("Invalid port, try again.");
      errorMessage.setOpacity(1);
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
  }
}
