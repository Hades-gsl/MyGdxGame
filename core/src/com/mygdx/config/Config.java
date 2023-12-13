package com.mygdx.config;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Hades
 */
public class Config {
  public static float ROWS = 10;
  public static float COLS = 10;
  public static float CELL_SIZE = 32;
  public static float CAMERA_WIDTH = ROWS * CELL_SIZE + 100;
  public static float CAMERA_HEIGHT = COLS * CELL_SIZE;
  public static final int CHARACTER_COUNT = 6;
  public static int INIT_HERO_COUNT = (int) ((ROWS + COLS) / 4);
  public static int INIT_ENEMY_COUNT = (int) ((ROWS + COLS) / 4);
  public static final float BULLET_SPEED = 2;
  public static float MAP_WIDTH = ROWS * CELL_SIZE;
  public static float MAP_HEIGHT = COLS * CELL_SIZE;
  public static final int HERO_HP = 100;
  public static final int HERO_ATK = 10;
  public static final int ENEMY_HP = 100;
  public static final int ENEMY_ATK = 10;
  public static final int INTERVAL_MILLI = 2000;
  public static final int BORDER_WIDTH = 3;
  public static final Color BACKGROUND_COLOR = Color.TEAL;
  public static final String HERO_PATH = "people/hero";
  public static final String ENEMY_PATH = "people/enemy";
  public static final String BULLET_PATH = "bullet.png";
  public static final String DIE_PATH = "people/die.png";
  public static final String WIN_PATH = "sound/win.mp3";
  public static final String BGM_PATH = "sound/bgm.mp3";
  public static final String SHOOT_PATH = "sound/bullet.mp3";
  public static final String RECORD_PATH = "record/";
  public static final String REPLAY_PATH = "replay/";
  public static final float BUTTON_WIDTH = 150;
  public static final float BUTTON_HEIGHT = 50;
  public static final float BUTTON_PAD = 10;
  public static final int MAX_RECORD = 10;

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
