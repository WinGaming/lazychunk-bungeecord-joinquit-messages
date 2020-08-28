package eu.spiforge.lazychunks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class JoinQuitMessages extends Plugin implements Listener {

	private static final JsonObject configObject;
	private static final File configFile;
	
	static {
		// Create configuration-folder if it doesn't exists
		File configFolder = new File("configs");
		if (!configFolder.exists()) configFolder.mkdirs();
		
		configFile = new File("./configs/join_quit_messages.json");
		if (!configFile.exists()) {
			try { configFile.createNewFile(); }
			catch (IOException ex) { System.err.println("Failed to create missing config file! "); ex.printStackTrace(); }
		}
		
		JsonElement parsedElement = null;
		
		try {
			JsonParser fileParser = new JsonParser();
			parsedElement = fileParser.parse(new FileReader(configFile));
		} catch (JsonIOException e) {
			System.err.println("Failed to read json from file. This is not a problem with the content of the file!");
			System.err.println("Falling back to defaults...");
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			System.err.println("Failed to parse json!");
			System.err.println("#(" + e.getMessage() + ")");
			System.err.println("Falling back to defaults...");
		} catch (FileNotFoundException e) {
			System.err.println("Could not find config file! This is a error!");
			System.err.println("Falling back to defaults...");
		}
		
		configObject = (parsedElement != null && parsedElement.isJsonObject()) ? parsedElement.getAsJsonObject() : new JsonObject();
		
		boolean needsSaving = false;
		for(Entry<String, JsonElement> entry : Defaults.DEFAULT_CONFIG.entrySet()) {
			if (!configObject.has(entry.getKey())) {
				needsSaving = true;
				configObject.add(entry.getKey(), entry.getValue());
			}
		}
		
		if (needsSaving) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
				writer.write(configObject.toString());
				writer.close();
			} catch (IOException e) {
				System.err.println("Failed to save restored config file!");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onEnable() {
		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
	}
	
	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent event) {
		String sTranslationKey = configObject.get(Consts.CONFIG_KEY_FORMATTING_COLOR_TRANSLATIONKEY).getAsString();
		char translationKey = sTranslationKey.length() == 0 ? '&' : sTranslationKey.charAt(0);
		
		String configMessage = configObject.get(Consts.CONFIG_KEY_MESSAGE_QUIT).getAsString();
		String coloredMessage = ChatColor.translateAlternateColorCodes(translationKey, configMessage);
		ProxyServer.getInstance().broadcast(new TextComponent(coloredMessage.replace("%NAME%", event.getPlayer().getName())));
	}
	
	@EventHandler
	public void onJoin(PostLoginEvent event) {
		String sTranslationKey = configObject.get(Consts.CONFIG_KEY_FORMATTING_COLOR_TRANSLATIONKEY).getAsString();
		char translationKey = sTranslationKey.length() == 0 ? '&' : sTranslationKey.charAt(0);
		
		String configMessage = configObject.get(Consts.CONFIG_KEY_MESSAGE_JOIN).getAsString();
		String coloredMessage = ChatColor.translateAlternateColorCodes(translationKey, configMessage);
		ProxyServer.getInstance().broadcast(new TextComponent(coloredMessage.replace("%NAME%", event.getPlayer().getName())));
	}
}