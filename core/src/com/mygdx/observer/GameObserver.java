package com.mygdx.observer;

import com.mygdx.event.CharacterAttack;
import com.mygdx.event.CharacterMove;
import com.mygdx.event.GameEvent;
import com.mygdx.event.HeroAttack;

/**
 * @author Hades
 */
public interface GameObserver {
  void handleEvent(GameEvent event);

  void handleEvent(CharacterMove event);

  void handleEvent(CharacterAttack event);

  void handleEvent(HeroAttack event);
}
