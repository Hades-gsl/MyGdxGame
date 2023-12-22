package com.mygdx.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.config.Config;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is responsible for managing the textures used in the game. It loads and stores the
 * textures for heroes, enemies, bullets, and the death animation. It uses the Singleton pattern to
 * ensure that only one instance of the TextureManager exists. This is done by making the
 * constructor private and providing a public method to get the instance.
 *
 * @author Hades
 */
@Getter
public class TextureManager {
  private List<Texture> heroTextures;
  private List<Texture> enemyTextures;
  private Texture bulletTexture;
  private Texture dieTexture;

  /** Private constructor for TextureManager. Calls the initTexture method to load the textures. */
  private TextureManager() {
    initTexture();
  }

  /**
   * Loads the textures for the heroes, enemies, bullets, and the death animation. The textures are
   * stored in CopyOnWriteArrayLists for thread safety.
   */
  private void initTexture() {
    heroTextures = new CopyOnWriteArrayList<>();
    enemyTextures = new CopyOnWriteArrayList<>();
    for (int i = 0; i < Config.INIT_HERO_COUNT; i++) {
      heroTextures.add(
          new Texture(Gdx.files.internal(Config.HERO_PATH + " (" + (i + 1) + ").png")));
    }
    for (int i = 0; i < Config.INIT_ENEMY_COUNT; i++) {
      enemyTextures.add(
          new Texture(Gdx.files.internal(Config.ENEMY_PATH + " (" + (i + 1) + ").png")));
    }

    bulletTexture = new Texture(Gdx.files.internal(Config.BULLET_PATH));
    dieTexture = new Texture(Gdx.files.internal(Config.DIE_PATH));
  }

  /**
   * This class holds the instance of the TextureManager. It is used to implement the Singleton
   * pattern.
   */
  private static class TextureManagerHolder {
    private static final TextureManager INSTANCE = new TextureManager();
  }

  /**
   * Returns the instance of the TextureManager. If the instance does not exist, it is created.
   *
   * @return The instance of the TextureManager
   */
  public static TextureManager getInstance() {
    return TextureManagerHolder.INSTANCE;
  }
}
