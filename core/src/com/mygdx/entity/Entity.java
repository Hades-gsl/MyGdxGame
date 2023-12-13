package com.mygdx.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Hades
 */
@Getter
@Setter
public abstract class Entity implements Serializable {
  private float x;
  private float y;
  private int hp;
  private int atk;
  private transient Sprite sprite;

  public Entity(float x, float y, int hp, int atk, Texture texture) {
    this.x = x;
    this.y = y;
    this.hp = hp;
    this.atk = atk;
    sprite = new Sprite(texture);
  }

  public void move(float x, float y) {
    setX(x);
    setY(y);
  }

  public boolean isDead() {
    return hp <= 0;
  }

  public void render(SpriteBatch batch) {
    sprite.setPosition(x, y);
    sprite.draw(batch);
  }

  public Rectangle getBound() {
    return sprite.getBoundingRectangle();
  }
}
