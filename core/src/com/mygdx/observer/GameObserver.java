package com.mygdx.observer;

import com.mygdx.event.CharacterAttack;
import com.mygdx.event.CharacterMove;
import com.mygdx.event.GameEvent;
import com.mygdx.event.HeroAttack;

/**
 * This interface defines the methods that a game observer must implement. A game observer is an
 * object that is notified when certain events occur in the game. The observer can then respond to
 * these events in an appropriate manner. The observer pattern is used to decouple the classes in
 * the game from each other. This makes the game easier to modify and understand. The methods in
 * this interface correspond to the different types of events that can occur in the game.
 *
 * @author Hades
 */
public interface GameObserver {
  /**
   * Handles a generic game event.
   *
   * @param event The game event to handle.
   */
  void handleEvent(GameEvent event);

  /**
   * Handles a character move event. This event is triggered when a character moves in the game.
   *
   * @param event The character move event to handle.
   */
  void handleEvent(CharacterMove event);

  /**
   * Handles a character attack event. This event is triggered when a character attacks in the game.
   *
   * @param event The character attack event to handle.
   */
  void handleEvent(CharacterAttack event);

  /**
   * Handles a hero attack event. This event is triggered when a hero attacks in the game.
   *
   * @param event The hero attack event to handle.
   */
  void handleEvent(HeroAttack event);
}
