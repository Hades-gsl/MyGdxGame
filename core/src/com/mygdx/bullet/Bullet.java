package com.mygdx.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.character.Character;
import com.mygdx.config.config;
import com.mygdx.entity.Entity;
import java.util.List;

/**
 * @author Hades
 */
public class Bullet extends Entity {
  private final float speedX;
  private final float speedY;
  private final Sound sound;

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
    getSprite().setRotation(rotation);

    sound = Gdx.audio.newSound(Gdx.files.internal(config.SHOOT_PATH));

    Gdx.app.log("Bullet", "x: " + x + ", y: " + y + ", rotation: " + rotation);
  }

  // update position and check collision
  public void update(List<? extends Character> characters) {
    move(getX() + speedX, getY() + speedY);

    characters.stream()
        .filter(character -> !character.isDead() && character.getBound().overlaps(getBound()))
        .forEach(
            character -> {
              character.setHp(character.getHp() - getAtk());
              sound.play();
              setHp(0);
            });
  }

  @Override
  public boolean isDead() {
    float x = getX() + (float) getSprite().getTexture().getWidth() / 2;
    float y = getY() + (float) getSprite().getTexture().getHeight() / 2;
    return x < 0 || x > config.MAP_WIDTH || y < 0 || y > config.MAP_HEIGHT || super.isDead();
  }
}
