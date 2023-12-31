package com.mygdx.config;

import com.badlogic.gdx.graphics.Color;

/**
 * This class contains the configuration for the game. It includes the number of rows and columns in
 * the game map, the size of a cell in the game map, the width and height of the game camera, the
 * initial number of heroes and enemies in the game, the speed of the bullets in the game, the width
 * and height of the game map, the health points and attack points of the heroes and enemies, the
 * interval in milliseconds between updates of the game entities, the width of the border in the
 * game map, the background color of the game, the paths of the game assets, the width, height and
 * padding of the buttons in the game, the maximum number of records in the game, the server address
 * and port, the maximum number of connections, and the buffer size. It also contains methods for
 * changing the configuration of the game.
 *
 * @author Hades
 */
public class Config {
  // The number of rows and columns in the game map
  public static float ROWS = 10;
  public static float COLS = 10;

  // The size of a cell in the game map
  public static float CELL_SIZE = 32;

  // The width and height of the game camera
  public static float CAMERA_WIDTH = ROWS * CELL_SIZE + 100;
  public static float CAMERA_HEIGHT = COLS * CELL_SIZE;

  // The initial number of heroes and enemies in the game
  public static int INIT_HERO_COUNT = (int) ((ROWS + COLS) / 4);
  public static int INIT_ENEMY_COUNT = (int) ((ROWS + COLS) / 4);

  // The speed of the bullets in the game
  public static final float BULLET_SPEED = 2;

  // The width and height of the game map
  public static float MAP_WIDTH = ROWS * CELL_SIZE;
  public static float MAP_HEIGHT = COLS * CELL_SIZE;

  // The health points and attack points of the heroes and enemies
  public static final int HERO_HP = 100;
  public static final int HERO_ATK = 10;
  public static final int ENEMY_HP = 100;
  public static final int ENEMY_ATK = 10;

  // The interval in milliseconds between updates of the game entities
  public static final int INTERVAL_MILLI = 2000;

  // The width of the border in the game map
  public static final int BORDER_WIDTH = 3;

  // The background color of the game
  public static final Color BACKGROUND_COLOR = Color.TEAL;

  // The paths of the game assets
  public static final String HERO_PATH = "people/hero";
  public static final String ENEMY_PATH = "people/enemy";
  public static final String BULLET_PATH = "bullet.png";
  public static final String DIE_PATH = "people/die.png";
  public static final String WIN_PATH = "sound/win.mp3";
  public static final String BGM_PATH = "sound/bgm.mp3";
  public static final String SHOOT_PATH = "sound/bullet.mp3";
  public static final String RECORD_PATH = "record/";
  public static final String REPLAY_PATH = "replay/";

  // The width, height and padding of the buttons in the game
  public static final float BUTTON_WIDTH = 150;
  public static final float BUTTON_HEIGHT = 50;
  public static final float BUTTON_PAD = 10;

  // The maximum number of records in the game
  public static final int MAX_RECORD = 10;

  // The address of the server. This is typically an IP address or a domain name.
  public static final String SERVER_ADDRESS = "localhost";

  // The port number on the server to connect to.
  public static final int SERVER_PORT = 9999;

  // The number of hero characters in a multiplayer game.
  public static final int MULTI_HERO_COUNT = 3;

  // The size of the buffer used for network communication. This is typically a power of 2.
  public static final int BUFFER_SIZE = 1024 * 10;

  // The maximum number of simultaneous connections the server will accept.
  public static final int MAX_CONNECTIONS = 3;

  /**
   * This method changes the configuration of the game. It updates the number of rows and columns,
   * the width and height of the camera, the initial number of heroes and enemies, and the width and
   * height of the map.
   *
   * @param rows The new number of rows.
   * @param cols The new number of columns.
   */
  public static void changeConfig(float rows, float cols) {
    ROWS = rows;
    COLS = cols;
    CAMERA_WIDTH = ROWS * CELL_SIZE + 100;
    CAMERA_HEIGHT = COLS * CELL_SIZE;
    INIT_HERO_COUNT = (int) ((ROWS + COLS) / 4);
    INIT_ENEMY_COUNT = (int) ((ROWS + COLS) / 4);
    MAP_WIDTH = ROWS * CELL_SIZE;
    MAP_HEIGHT = COLS * CELL_SIZE;
  }

  /**
   * This method changes the initial number of heroes in the game.
   *
   * @param count The new initial number of heroes.
   */
  public static void changeHeroCount(int count) {
    INIT_HERO_COUNT = count;
  }
}
