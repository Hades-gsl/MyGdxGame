package com.mygdx.event;

import lombok.Getter;

/**
 * @author Hades
 */
@Getter
public class GameEvent {
  public enum Type {
    HERO_MOVE,
    ENEMY_MOVE,
    CHARACTER_ATTACK,
    HERO_ATTACK,
  }

  protected Type type;
}
