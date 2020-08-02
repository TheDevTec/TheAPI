package me.DevTec.Placeholders;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public abstract class PlaceholderPreRegister extends PlaceholderExpansion {
	public abstract String onRequest(Player player, String placeholder);

	private final String author, prefix, version;
	
	
	public PlaceholderPreRegister(String author, String prefix, String version) {
		this.author=author;
		this.prefix=prefix;
		this.version=version;
	}
	
	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public String getIdentifier() {
		return prefix;
	}

	@Override
	public String getVersion() {
		return version;
	}

    @Override
    public String onPlaceholderRequest(Player s, String identifier){
        if(identifier==null||identifier.trim().isEmpty())return "";
        return onRequest(s, identifier);
    }
}
