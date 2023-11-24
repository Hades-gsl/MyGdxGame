package com.mygdx.matrix;

import com.badlogic.gdx.Gdx;
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
}
