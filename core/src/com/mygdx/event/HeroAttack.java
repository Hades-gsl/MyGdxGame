package com.mygdx.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Hades
 */
@Getter
@Setter
@NoArgsConstructor
public class HeroAttack extends GameEvent {
  String id;
  float x;
  float y;

  public HeroAttack(String id, float x, float y, Type type) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.type = type;
  }
}
