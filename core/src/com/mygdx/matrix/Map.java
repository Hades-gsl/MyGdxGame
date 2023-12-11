package com.mygdx.matrix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.config.config;

/**
 * @author Hades
 */
public class Map {
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
    if (x < 0 || x >= config.MAP_WIDTH || y < 0 || y >= config.MAP_HEIGHT) {
      return 2;
    }

    return matrix[(int) (x / config.CELL_SIZE)][(int) (y / config.CELL_SIZE)];
  }

  public void set(int x, int y, int value) {
    if (x < 0 || x >= config.MAP_WIDTH || y < 0 || y >= config.MAP_HEIGHT) {
      return;
    }

    matrix[(int) (x / config.CELL_SIZE)][(int) (y / config.CELL_SIZE)] = value;
  }

  public void render(ShapeRenderer shapeRenderer) {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    for (int i = 0; i < config.ROWS; i++) {
      for (int j = 0; j < config.COLS; j++) {
        float x = i * config.CELL_SIZE;
        float y = j * config.CELL_SIZE;

        if ((i + j) % 2 == 0) {
          shapeRenderer.setColor(Color.LIGHT_GRAY);
        } else {
          shapeRenderer.setColor(Color.DARK_GRAY);
        }

        shapeRenderer.rect(x, y, config.CELL_SIZE, config.CELL_SIZE);
      }
    }

    shapeRenderer.setColor(Color.SLATE);
    shapeRenderer.rectLine(
        config.ROWS / 2 * config.CELL_SIZE,
        0,
        config.ROWS / 2 * config.CELL_SIZE,
        config.MAP_HEIGHT,
        config.BORDER_WIDTH);
  }
}
