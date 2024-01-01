package com.mygdx.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mygdx.character.Character;
import com.mygdx.config.Config;
import com.mygdx.entity.Entity;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * This class represents a bullet in the game. It extends the Entity class. It contains properties
 * for the speed and rotation of the bullet, and a sound that is played when the bullet hits a
 * character. It also contains methods for updating the bullet's state and checking if the bullet is
 * dead. The bullet's state is updated by moving the bullet and checking for collisions with
 * characters. The bullet is dead if it is outside the game map or if it has hit a character.
 *
 * @author Hades
 */
@Getter
@Setter
@JsonIgnoreProperties({"sprite", "sound"})
public class Bullet extends Entity {
  private static final long serialVersionUID = 1L;
  private float speedX; // The speed of the bullet in the x direction.
  private float speedY; // The speed of the bullet in the y direction.
  private float rotation; // The rotation of the bullet.
  private transient Sound sound; // The sound that is played when the bullet hits a character.

  /**
   * Constructor for the Bullet class. It initializes the bullet's position, attack points, speed,
   * rotation, and texture. It also initializes the sound that is played when the bullet hits a
   * character.
   *
   * @param x The x-coordinate of the bullet.
   * @param y The y-coordinate of the bullet.
   * @param atk The attack points of the bullet.
   * @param speedX The speed of the bullet in the x direction.
   * @param speedY The speed of the bullet in the y direction.
   * @param rotation The rotation of the bullet.
   * @param bulletTexture The texture of the bullet.
   */
  public Bullet(
      float x,
      float y,
      int atk,
      float speedX,
      float speedY,
      float rotation,
      Texture bulletTexture) {
    super(x, y, 99, atk, bulletTexture);

    this.speedX = speedX;
    this.speedY = speedY;
    this.rotation = rotation;
    getSprite().setRotation(rotation);

    sound = Gdx.audio.newSound(Gdx.files.internal(Config.SHOOT_PATH));

    Gdx.app.log("Bullet", "x: " + x + ", y: " + y + ", rotation: " + rotation);
  }

  /**
   * This method updates the bullet's state. It moves the bullet and checks for collisions with
   * characters. If the bullet hits a character, it reduces the character's health points by the
   * bullet's attack points, plays a sound, and sets the bullet's health points to 0.
   *
   * @param characters The list of characters.
   */
  public void update(List<? extends Character> characters) {
    move(getX() + speedX, getY() + speedY);

    characters.stream()
        .filter(character -> !character.isDead() && character.getBound().overlaps(getBound()))
        .forEach(
            character -> {
              character.setHp(character.getHp() - getAtk());
              sound.play();
              setHp(0);
              Gdx.app.log("Bullet", "hit " + character.getId() + ", hp: " + character.getHp());

              if (character.isDead()) {
                character.changeDieTexture();
              }
            });
  }

  /**
   * This method checks if the bullet is dead. The bullet is dead if it is outside the game map or
   * if its health points are less than or equal to 0.
   *
   * @return true if the bullet is dead, false otherwise.
   */
  @JsonIgnore
  @Override
  public boolean isDead() {
    float x = getX() + (float) getSprite().getTexture().getWidth() / 2;
    float y = getY() + (float) getSprite().getTexture().getHeight() / 2;
    return x < 0 || x > Config.MAP_WIDTH || y < 0 || y > Config.MAP_HEIGHT || super.isDead();
  }

  /**
   * This method returns the bounding rectangle of the bullet. It is used for collision detection.
   *
   * @return The bounding rectangle of the bullet.
   */
  @JsonIgnore
  @Override
  public Rectangle getBound() {
    return super.getBound();
  }
}
