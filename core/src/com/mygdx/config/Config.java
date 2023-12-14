package com.mygdx.config;

import com.badlogic.gdx.graphics.Color;

/**
 * This class contains the configuration for the game. It includes constants for the game settings
 * and paths for the game assets. It also includes a method for changing the configuration. The
 * configuration can be changed at runtime. The changes will affect the game immediately. The
 * configuration is static, so it can be accessed from anywhere in the game without creating an
 * instance of this class.
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

  // The number of characters in the game
  public static final int CHARACTER_COUNT = 6;

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
}
