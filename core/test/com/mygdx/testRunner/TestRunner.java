package com.mygdx.testRunner;

import static org.mockito.Mockito.mock;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;

public class TestRunner {
  public TestRunner(ApplicationListener applicationListener) {
    Gdx.gl = mock(GL20.class);
    Gdx.gl20 = mock(GL20.class);
    Gdx.gl30 = mock(GL30.class);

    HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
    config.updatesPerSecond = 60;
    new HeadlessApplication(applicationListener, config);
  }

  public void exit() {
    Gdx.app.exit();
  }
}
