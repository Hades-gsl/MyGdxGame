package com.mygdx.matrix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.constants.Constants;

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
    if (x < 0 || x >= Constants.MAP_WIDTH || y < 0 || y >= Constants.MAP_HEIGHT) {
      return 2;
    }

    return matrix[(int) (x / Constants.CELL_SIZE)][(int) (y / Constants.CELL_SIZE)];
  }


  public void set(int x, int y, int value) {
    matrix[(int) (x / Constants.CELL_SIZE)][(int) (y / Constants.CELL_SIZE)] = value;
  }

  public void render(ShapeRenderer shapeRenderer){
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    for (int i = 0; i < Constants.ROWS; i++) {
      for (int j = 0; j < Constants.COLS; j++) {
        float x = i * Constants.CELL_SIZE;
        float y = j * Constants.CELL_SIZE;

        if ((i + j) % 2 == 0) {
          shapeRenderer.setColor(Color.LIGHT_GRAY);
        } else {
          shapeRenderer.setColor(Color.DARK_GRAY);
        }

        shapeRenderer.rect(x, y, Constants.CELL_SIZE, Constants.CELL_SIZE);
      }
    }

    shapeRenderer.setColor(Color.SLATE);
    shapeRenderer.rectLine(
            Constants.ROWS / 2 * Constants.CELL_SIZE,
            0,
            Constants.ROWS / 2 * Constants.CELL_SIZE,
            Constants.MAP_HEIGHT,
            Constants.BORDER_WIDTH);
  }
}
