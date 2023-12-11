package com.mygdx.matrix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.config.Config;

import java.io.Serializable;

/**
 * @author Hades
 */
public class Map implements Serializable {
  private final int[][] matrix;

  public Map(int rows, int cols) {
    matrix = new int[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        matrix[i][j] = 0;
      }
    }

    Gdx.app.log("Map", "rows: " + rows + ", cols: " + cols);
  }

  public int get(int x, int y) {
    if (x < 0 || x >= Config.MAP_WIDTH || y < 0 || y >= Config.MAP_HEIGHT) {
      return 2;
    }

    return matrix[(int) (x / Config.CELL_SIZE)][(int) (y / Config.CELL_SIZE)];
  }

  public void set(int x, int y, int value) {
    if (x < 0 || x >= Config.MAP_WIDTH || y < 0 || y >= Config.MAP_HEIGHT) {
      return;
    }

    matrix[(int) (x / Config.CELL_SIZE)][(int) (y / Config.CELL_SIZE)] = value;
  }

  public void render(ShapeRenderer shapeRenderer) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    for (int i = 0; i < Config.ROWS; i++) {
      for (int j = 0; j < Config.COLS; j++) {
        float x = i * Config.CELL_SIZE;
        float y = j * Config.CELL_SIZE;

        if ((i + j) % 2 == 0) {
          shapeRenderer.setColor(Color.LIGHT_GRAY);
        } else {
          shapeRenderer.setColor(Color.DARK_GRAY);
        }

        shapeRenderer.rect(x, y, Config.CELL_SIZE, Config.CELL_SIZE);
      }
    }

    shapeRenderer.setColor(Color.SLATE);
    shapeRenderer.rectLine(
        Config.ROWS / 2 * Config.CELL_SIZE,
        0,
        Config.ROWS / 2 * Config.CELL_SIZE,
        Config.MAP_HEIGHT,
        Config.BORDER_WIDTH);
  }
}
