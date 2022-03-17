package me.devtec.theapi;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.proxy.plugin.PluginClassLoader;

import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.AdventureComponentAPI;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.json.Json;
import me.devtec.shared.json.modern.ModernJsonReader;
import me.devtec.shared.json.modern.ModernJsonWriter;
import me.devtec.shared.utility.LibraryLoader;
import me.devtec.shared.utility.StringUtils;
import me.devtec.shared.utility.StringUtils.ColormaticFactory;

@Plugin(id = "theapi", name = "TheAPI", version = "9.0", authors = {"DevTec", "StraikerinaCZ"}, url = "https://www.spigotmc.org/resources/72679/")
public class VelocityLoader {

    @Inject
    public VelocityLoader(ProxyServer server, Logger logger) {
    	initTheAPI();
    }
	
	public static void initTheAPI() {
	    Ref.init(ServerType.VELOCITY, "Velocity"); //Server version
	    ComponentAPI.init(null, new AdventureComponentAPI<>());
	    Json.init(new ModernJsonReader(), new ModernJsonWriter()); //Modern version of Guava
	    API.library = new LibraryLoader() {
	        List<File> loaded = new ArrayList<>();
	        
	        @SuppressWarnings("resource")
	        @Override
	        public void load(File file) {
	            if(isLoaded(file) || !file.exists())return;
	            loaded.add(file);
	            try {
	                new PluginClassLoader(new URL[] {file.toURI().toURL()}).addToClassloaders();
	            } catch (MalformedURLException e) {
	                e.printStackTrace();
	            }
	        }
	        
	        @Override
	        public boolean isLoaded(File file) {
	            return loaded.contains(file);
	        }
	    };
	    API.basics().load();
	    StringUtils.rainbowSplit = Pattern.compile("(&?#[A-Fa-f0-9]{6}([&§][K-Ok-oRr])*|[&§][Xx]([&§][A-Fa-f0-9]){6}([&§][K-Ok-oRr])*|[&§][A-Fa-f0-9K-ORrk-oUuXx]([&§][K-Ok-oRr])*)");
	    StringUtils.color = new ColormaticFactory() {
	        char[] characters = "abcdef0123456789".toCharArray();
	        Random random = new Random();
	        Pattern getLast = Pattern.compile("(&?#[A-Fa-f0-9k-oK-ORrXxUu]{6}|§[Xx](§[A-Fa-f0-9k-oK-ORrXxUu]){6}|§[A-Fa-f0-9k-oK-ORrXxUu]|&[Uu])"), hex = Pattern.compile("(&?#[a-fA-F0-9]{6})");
	        
	        @Override
	        public String gradient(String msg, String fromHex, String toHex) {
	            return API.basics().gradient(msg, fromHex, toHex);
	        }

	        @Override
	        public String generateColor() {
	            StringBuilder b = new StringBuilder("&#");
	            for (int i = 0; i < 6; ++i)
	                b.append(characters[random.nextInt(16)]);
	            return b.toString();
	        }

	        @Override
	        public String[] getLastColors(String text) {
	            return API.basics().getLastColors(getLast, text);
	        }

	        @Override
	        public String replaceHex(String msg) {
	            Matcher match = hex.matcher(msg);
	            while (match.find()) {
	                String color = match.group();
	                if(!color.startsWith("&"))
	                    msg = msg.replace(color, "&"+color);
	            }
	            return msg;
	        }
	    };
	}
}
