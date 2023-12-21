package com.mygdx.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.config.Config;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Hades
 */
@Getter
public class TextureManager {
  private List<Texture> heroTextures;
  private List<Texture> enemyTextures;
  private Texture bulletTexture;
  private Texture dieTexture;

  private TextureManager() {
    initTexture();
  }

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

  private static class TextureManagerHolder {
    private static final TextureManager INSTANCE = new TextureManager();
  }

  public static TextureManager getInstance() {
    return TextureManagerHolder.INSTANCE;
  }
}
