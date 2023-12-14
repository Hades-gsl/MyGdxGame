package com.mygdx.matrix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.config.Config;
import lombok.Getter;

import java.io.Serializable;

/**
 * This class represents a game map. It implements the Serializable interface for saving and loading
 * the game state. It contains a 2D matrix that represents the cells of the map. It also contains
 * methods for getting and setting the value of a cell and for rendering the map.
 *
 * @author Hades
 */
@Getter
public class Map implements Serializable {
  private final int[][] matrix;

  /**
   * Constructor for the Map class. It initializes the matrix with the specified number of rows and
   * columns and sets all cells to 0.
   *
   * @param rows The number of rows.
   * @param cols The number of columns.
   */
  public Map(int rows, int cols) {
    matrix = new int[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        matrix[i][j] = 0;
      }
    }

    Gdx.app.log("Map", "rows: " + rows + ", cols: " + cols);
  }

  /**
   * This method gets the value of a cell. If the specified coordinates are outside the map, it
   * returns 2.
   *
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   * @return The value of the cell.
   */
  public int get(int x, int y) {
    if (x < 0 || x >= Config.MAP_WIDTH || y < 0 || y >= Config.MAP_HEIGHT) {
      return 2;
    }

    return matrix[(int) (x / Config.CELL_SIZE)][(int) (y / Config.CELL_SIZE)];
  }

  /**
   * This method sets the value of a cell. If the specified coordinates are outside the map, it does
   * nothing.
   *
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   * @param value The value to set.
   */
  public void set(int x, int y, int value) {
    if (x < 0 || x >= Config.MAP_WIDTH || y < 0 || y >= Config.MAP_HEIGHT) {
      return;
    }

    matrix[(int) (x / Config.CELL_SIZE)][(int) (y / Config.CELL_SIZE)] = value;
  }

  /**
   * This method renders the map. It draws a rectangle for each cell of the map. The color of the
   * rectangle depends on the position of the cell. It also draws a line in the middle of the map.
   *
   * @param shapeRenderer The ShapeRenderer to use for drawing.
   */
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
