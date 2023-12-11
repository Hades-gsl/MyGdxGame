package com.mygdx.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.io.Serializable;

/**
 * @author Hades
 */
public abstract class Entity implements Serializable {
  private float x;
  private float y;
  private int hp;
  private int atk;
  private final transient Sprite sprite;

  public Entity(float x, float y, int hp, int atk, Texture texture) {
    this.x = x;
    this.y = y;
    this.hp = hp;
    this.atk = atk;
    sprite = new Sprite(texture);
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public int getHp() {
    return hp;
  }

  public int getAtk() {
    return atk;
  }

  public Sprite getSprite() {
    return sprite;
  }

  public void setX(float x) {
    this.x = x;
  }

  public void setY(float y) {
    this.y = y;
  }

  public void move(float x, float y) {
    setX(x);
    setY(y);
  }

  public void setHp(int hp) {
    this.hp = hp;
  }

  public void setAtk(int atk) {
    this.atk = atk;
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
