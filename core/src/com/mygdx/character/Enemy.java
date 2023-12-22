package com.mygdx.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents an enemy character in the game. It extends the Character class and
 * implements the Runnable interface for multithreading. It contains properties for the game state,
 * the textures of the enemy and the bullet, and a flag for the enemy's death. It also contains
 * methods for updating the enemy's state, running the enemy's AI, checking if the enemy is dead,
 * and getting the bounding rectangle of the enemy. The enemy's state is updated by attacking the
 * hero with the minimum health points and moving randomly. The enemy's AI is run in a separate
 * thread to avoid blocking the main game thread. The enemy is dead if its health points are less
 * than or equal to 0. The bounding rectangle of the enemy is used for collision detection.
 *
 * @author Hades
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"gameState", "bulletTexture", "charaterTexture", "dieTexture", "sprite"})
public class Enemy extends Character implements Runnable {
  private static final long serialVersionUID = 1L;

  /**
   * Constructor for the Enemy class. It initializes the enemy's position, health points, attack
   * points, and textures.
   *
   * @param x The x-coordinate of the enemy.
   * @param y The y-coordinate of the enemy.
   * @param hp The health points of the enemy.
   * @param atk The attack points of the enemy.
   * @param enemyTexture The texture of the enemy.
   * @param bulletTexture The texture of the bullet.
   */
  public Enemy(int x, int y, int hp, int atk, Texture enemyTexture, Texture bulletTexture) {
    super(x, y, hp, atk, enemyTexture, bulletTexture);

    Gdx.app.log("Enemy", "x: " + x + ", y: " + y);
  }

  /**
   * This method updates the enemy's state by attacking the hero with the minimum health points and
   * moving randomly. It synchronizes on the game map to avoid concurrent modification exceptions.
   */
  public void update() {
    attackMinHp(gameState.getBullets(), gameState.getHeroes());
    synchronized (gameState.getMap()) {
      randomMove(gameState.getMap(), true);
    }
  }

  /**
   * This method runs the enemy's AI. If the enemy is dead, it does nothing. Otherwise, it updates
   * the enemy's state.
   */
  @Override
  public void run() {
    if (!isDead()) {
      update();
    }
  }

  /**
   * This method checks if the enemy is dead. The enemy is dead if its health points are less than
   * or equal to 0.
   *
   * @return true if the enemy is dead, false otherwise.
   */
  @JsonIgnore
  @Override
  public boolean isDead() {
    return super.isDead();
  }

  /**
   * This method returns the bounding rectangle of the enemy. It is used for collision detection.
   *
   * @return The bounding rectangle of the enemy.
   */
  @JsonIgnore
  @Override
  public Rectangle getBound() {
    return super.getBound();
  }
}
