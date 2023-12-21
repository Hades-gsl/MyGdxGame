package com.mygdx.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Hades
 *     <p>invoked by {@link com.mygdx.character.Character#attack(float, float, List)}
 */
@Getter
@Setter
@NoArgsConstructor
public class CharacterAttack extends GameEvent {
  float x;
  float y;
  int atk;
  float speedX;
  float speedY;
  float rotation;

  public CharacterAttack(
      float x, float y, int atk, float speedX, float speedY, float rotation, Type type) {
    this.x = x;
    this.y = y;
    this.atk = atk;
    this.speedX = speedX;
    this.speedY = speedY;
    this.rotation = rotation;
    this.type = type;
  }
}
