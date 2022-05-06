package it.polimi.ingsw.eriantys.cli.utils;

import it.polimi.ingsw.eriantys.model.enums.HouseColor;
import it.polimi.ingsw.eriantys.model.enums.TowerColor;
import org.fusesource.jansi.Ansi;

public class Util {
  /**
   * Maps a HouseColor to the corresponding Ansi color code using the Jansi library.
   */
  public static String getColorString(HouseColor c) {
    return switch (c) {
      case BLUE -> Ansi.ansi().fgBlue().toString();
      case RED -> Ansi.ansi().fgRed().toString();
      case GREEN -> Ansi.ansi().fgGreen().toString();
      case YELLOW -> Ansi.ansi().fgBrightYellow().toString();
      case PINK -> Ansi.ansi().fgBrightMagenta().toString();
    };
  }

  /**
   * Maps a TowerColor to the corresponding Ansi color code using the Jansi library.
   */
  public static String getColorString(TowerColor t) {
    return switch (t) {
      case WHITE -> Ansi.ansi().fgBrightDefault().toString();
      case GRAY -> Ansi.ansi().fgBrightBlack().toString();
      case BLACK -> Ansi.ansi().fgBlack().toString();
    };
  }

  /**
   * Prints a HouseColor as a colored string.
   */
  public static String printColored(char c, HouseColor color) {
    return getColorString(color) + c + Ansi.ansi().reset().toString();
  }

  /**
   * Prints a HouseColor as a colored string.
   */
  public static String printColored(String s, HouseColor color) {
    return getColorString(color) + s + Ansi.ansi().reset().toString();
  }

  /**
   * Prints a HouseColor as a colored string.
   */
  public static String printColored(char c, TowerColor color) {
    return getColorString(color) + c + Ansi.ansi().reset().toString();
  }

  /**
   * Prints a HouseColor as a colored string.
   */
  public static String printColored(String s, TowerColor color) {
    return getColorString(color) + s + Ansi.ansi().reset().toString();
  }

  /**
   * Clamps a value between a minimum and a maximum value
   * @param value The value to clamp
   * @param min The minimum value
   * @param max The maximum value
   * @return The value if it is between min and max, min or max otherwise
   */
  public static int clamp(int value, int min, int max) {
    return java.lang.Math.max(min, java.lang.Math.min(max, value));
  }
}
