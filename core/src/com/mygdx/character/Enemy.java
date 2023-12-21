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
 * implements the Runnable interface for multithreading. It contains properties for the game map,
 * the list of bullets and heroes. It also contains methods for setting the game map, bullets and
 * heroes, updating the enemy's state, and running the enemy's AI. The enemy's AI attacks the hero
 * with the minimum health points and moves randomly. The enemy can be controlled by AI.
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
   * moving randomly.
   */
  public void update() {
    attackMinHp(gameState.getBullets(), gameState.getHeroes());
    synchronized (gameState.getMap()) {
      randomMove(gameState.getMap(), true);
    }
  }

  /**
   * This method runs the enemy's AI. If the enemy is dead, it changes the enemy's texture.
   * Otherwise, it updates the enemy's state.
   */
  @Override
  public void run() {
    if (!isDead()) {
      update();
    }
  }

  @JsonIgnore
  @Override
  public boolean isDead() {
    return super.isDead();
  }

  @JsonIgnore
  @Override
  public Rectangle getBound() {
    return super.getBound();
  }
}
