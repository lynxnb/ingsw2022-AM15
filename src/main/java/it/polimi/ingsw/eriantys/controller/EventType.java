package it.polimi.ingsw.eriantys.controller;

public enum EventType {
  NICKNAME_OK("nickname_ok"),

  START_GAME("start_game"),

  GAMEINFO_EVENT("gameinfo"),
  GAMEDATA_EVENT("gamedata"),
  GAME_ENDED("game_ended"),

  INTERNAL_SOCKET_ERROR("internal_socket_error"),
  ERROR("network_error"),

  PLAYER_CONNECTION_CHANGED("player_connection_changed"),

  INPUT_ENTERED("input_entered")


  ;
  public String tag;

  EventType(String tag) {
    this.tag = tag;
  }

  @Override
  public String toString() {
    return tag;
  }
}
