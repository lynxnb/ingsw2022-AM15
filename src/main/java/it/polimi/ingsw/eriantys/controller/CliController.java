package it.polimi.ingsw.eriantys.controller;

import it.polimi.ingsw.eriantys.cli.menus.Menu;
import it.polimi.ingsw.eriantys.cli.menus.MenuIterator;
import it.polimi.ingsw.eriantys.cli.menus.lobby.MenuChooseNickname;
import it.polimi.ingsw.eriantys.cli.menus.lobby.MenuConnect;
import it.polimi.ingsw.eriantys.cli.menus.lobby.MenuCreateOrJoin;
import it.polimi.ingsw.eriantys.cli.menus.lobby.MenuLobby;
import it.polimi.ingsw.eriantys.network.Client;
import org.fusesource.jansi.Ansi;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CliController extends Controller {
  PrintStream out = System.out;
  Scanner in = new Scanner(System.in);

  public CliController(Client networkClient) {
    super(networkClient);
  }

  @Override
  public void showError(String error) {
    out.println(Ansi.ansi().fgRed().a(error).reset());
  }

  @Override
  public void firePropertyChange(EventType event) {
    listenerHolder.firePropertyChange(event.tag, null, null);
  }

  @Override
  public void run() {
    MenuIterator menus = new MenuIterator();
    while (true) {
      menus.menuAction(in, out);
      menus.next();
    }
  }
}

