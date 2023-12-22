package com.mygdx.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mygdx.config.Config;
import com.mygdx.event.CharacterMove;
import com.mygdx.event.GameEvent;
import com.mygdx.map.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a hero character in the game. It extends the Character class and implements
 * the Runnable interface for multithreading. It contains properties for the game state, the
 * textures of the hero and the bullet, and a flag for AI control. It also contains methods for
 * updating the hero's state, rendering the hero's border, running the hero's AI, checking if the
 * hero is dead, and getting the bounding rectangle of the hero. The hero's state is updated by
 * attacking a position, moving the hero, or attacking the enemy with the minimum health points and
 * moving randomly. The hero's border is rendered with a color specified in the game configuration.
 * The hero's AI is run in a separate thread to avoid blocking the main game thread. The hero is
 * dead if its health points are less than or equal to 0. The bounding rectangle of the hero is used
 * for collision detection.
 *
 * @author Hades
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"gameState", "bulletTexture", "charaterTexture", "dieTexture", "sprite"})
public class Hero extends Character implements Runnable {
  private static final long serialVersionUID = 1L;
  private boolean isAI = true; // Flag indicating if the hero is controlled by AI.

  /**
   * Constructor for the Hero class. It initializes the hero's position, health points, attack
   * points, and textures.
   *
   * @param x The x-coordinate of the hero.
   * @param y The y-coordinate of the hero.
   * @param hp The health points of the hero.
   * @param atk The attack points of the hero.
   * @param heroTexture The texture of the hero.
   * @param bulletTexture The texture of the bullet.
   */
  public Hero(int x, int y, int hp, int atk, Texture heroTexture, Texture bulletTexture) {
    super(x, y, hp, atk, heroTexture, bulletTexture);

    Gdx.app.log("Hero", "x: " + x + ", y: " + y);
  }

  /**
   * This method updates the hero's state by attacking a position.
   *
   * @param x The x-coordinate of the position.
   * @param y The y-coordinate of the position.
   */
  public void update(float x, float y) {
    attack(x, y, gameState.getBullets());
  }

  /**
   * This method updates the hero's position by moving the hero. The movement is done in increments
   * of the cell size defined in the game configuration. The method first checks if the new position
   * is within the game map and is not occupied. If these conditions are met, the hero is moved to
   * the new position and a CharacterMove event is generated to notify observers of the hero's move.
   *
   * @param dx The change in the x-coordinate, must be a multiple of the cell size.
   * @param dy The change in the y-coordinate, must be a multiple of the cell size.
   */
  public void update(int dx, int dy) {
    assert dx % Config.CELL_SIZE == 0 && dy % Config.CELL_SIZE == 0;

    Map map = gameState.getMap();
    synchronized (map) {
      if (map.get((int) (getX() + dx), (int) (getY() + dy)) == 0
          && (getX() + dx) / Config.CELL_SIZE < Config.ROWS / 2) {
        map.set((int) getX(), (int) getY(), 0);
        map.set((int) (getX() + dx), (int) (getY() + dy), 1);
        move(getX() + dx, getY() + dy);

        gameState.notifyObservers(
            new CharacterMove(
                (int) (dx / Config.CELL_SIZE),
                (int) (dy / Config.CELL_SIZE),
                getId(),
                GameEvent.Type.HERO_MOVE));
      }
    }
  }

  /**
   * This method updates the hero's state by attacking the enemy with the minimum health points and
   * moving randomly.
   */
  public void update() {
    attackMinHp(gameState.getBullets(), gameState.getEnemies());
    synchronized (gameState.getMap()) {
      randomMove(gameState.getMap(), false);
    }
  }

  /**
   * This method renders the hero's border.
   *
   * @param batch The SpriteBatch to use for drawing.
   */
  public void renderBorder(SpriteBatch batch) {
    batch.setColor(Config.BACKGROUND_COLOR);
    batch.draw(
        getSprite().getTexture(),
        getSprite().getX() - Config.BORDER_WIDTH,
        getSprite().getY() - Config.BORDER_WIDTH,
        getSprite().getWidth() + 2 * Config.BORDER_WIDTH,
        getSprite().getHeight() + 2 * Config.BORDER_WIDTH);
    batch.setColor(Color.WHITE);
  }

  /**
   * This method runs the hero's AI. If the hero is not controlled by AI or is dead, it does
   * nothing. Otherwise, it updates the hero's state.
   */
  @Override
  public void run() {
    if (isAI && !isDead()) {
      update();
    }
  }

  /**
   * This method checks if the hero is dead. The hero is dead if its health points are less than or
   * equal to 0.
   *
   * @return true if the hero is dead, false otherwise.
   */
  @JsonIgnore
  @Override
  public boolean isDead() {
    return super.isDead();
  }

  /**
   * This method returns the bounding rectangle of the hero. It is used for collision detection.
   *
   * @return The bounding rectangle of the hero.
   */
  @JsonIgnore
  @Override
  public Rectangle getBound() {
    return super.getBound();
  }
}
