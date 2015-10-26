package io.github.plenglin.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.plenglin.Pong;

public class PongLauncher {
	
	public static void main (String[] arg) {
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = Pong.WIDTH;
		config.height = Pong.HEIGHT;
		config.resizable = false;
		config.title = "Pong of Doom";
		config.x = -1;
		config.y = -1;
		
		new LwjglApplication(new Pong(), config);
		
	}
	
}
