package com.mygdx.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.config.Config;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This abstract class represents an entity in the game. It implements the Serializable interface
 * for saving and loading the game state. It contains properties for the position, health points,
 * attack points and sprite of the entity. It also contains methods for moving the entity, checking
 * if the entity is dead, rendering the entity and getting the bounding rectangle of the entity.
 *
 * @author Hades
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class Entity implements Serializable {
  private float x;
  private float y;
  private int hp;
  private int atk;
  private transient Sprite sprite;

  /**
   * Constructor for the Entity class. It initializes the position, health points, attack points and
   * sprite of the entity.
   *
   * @param x The x-coordinate of the entity.
   * @param y The y-coordinate of the entity.
   * @param hp The health points of the entity.
   * @param atk The attack points of the entity.
   * @param texture The texture of the entity.
   */
  public Entity(float x, float y, int hp, int atk, Texture texture) {
    this.x = x;
    this.y = y;
    this.hp = hp;
    this.atk = atk;
    sprite = new Sprite(texture);
  }

  /**
   * This method moves the entity to a new position.
   *
   * @param x The new x-coordinate.
   * @param y The new y-coordinate.
   */
  public void move(float x, float y) {
    assert x % Config.CELL_SIZE == 0 && y % Config.CELL_SIZE == 0;

    setX(x);
    setY(y);
  }

  /**
   * This method checks if the entity is dead. An entity is dead if its health points are less than
   * or equal to 0.
   *
   * @return true if the entity is dead, false otherwise.
   */
  public boolean isDead() {
    return hp <= 0;
  }

  /**
   * This method renders the entity. It sets the position of the sprite and draws it.
   *
   * @param batch The SpriteBatch to use for drawing.
   */
  public void render(SpriteBatch batch) {
    sprite.setPosition(x, y);
    sprite.draw(batch);
  }

  /**
   * This method gets the bounding rectangle of the entity.
   *
   * @return The bounding rectangle of the entity.
   */
  public Rectangle getBound() {
    return sprite.getBoundingRectangle();
  }
}
