package eu.spiforge.lazychunks;

import com.google.gson.JsonObject;

public class Defaults {
	
	public static final JsonObject DEFAULT_CONFIG;
	
	static {
		DEFAULT_CONFIG = new JsonObject();
		DEFAULT_CONFIG.addProperty(Consts.CONFIG_KEY_MESSAGE_JOIN, "&e%NAME% joined the game");
		DEFAULT_CONFIG.addProperty(Consts.CONFIG_KEY_MESSAGE_QUIT, "&e%NAME% left the game");
		DEFAULT_CONFIG.addProperty(Consts.CONFIG_KEY_FORMATTING_COLOR_TRANSLATIONKEY, "&");
	}
}