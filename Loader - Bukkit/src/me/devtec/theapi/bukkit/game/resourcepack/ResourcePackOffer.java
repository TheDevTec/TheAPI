package me.devtec.theapi.bukkit.game.resourcepack;

import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Optional;

import io.netty.buffer.ByteBufUtil;
import me.devtec.shared.components.Component;

public class ResourcePackOffer {

	private String url;
	private String sha1;
	private Optional<Component> prompt;
	private boolean force;

	public static ResourcePackOffer url(String url) {
		return new ResourcePackOffer(url);
	}

	private ResourcePackOffer(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public ResourcePackOffer setHash​(String sha1) {
		this.sha1 = sha1;
		return this;
	}

	public ResourcePackOffer generateHash() {
		try {
			sha1 = ByteBufUtil.hexDump(createSha1());
			return this;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getHash() {
		return sha1;
	}

	public ResourcePackOffer setPrompt(Component prompt) {
		this.prompt = Optional.ofNullable(prompt);
		return this;
	}

	public Optional<Component> getPrompt() {
		return prompt;
	}

	public ResourcePackOffer setShouldForce​(boolean force) {
		this.force = force;
		return this;
	}

	public boolean isShouldForce() {
		return force;
	}

	private byte[] createSha1() throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		InputStream fis = new URL(url).openStream();
		int n = 0;
		byte[] buffer = new byte[8192];
		while (n != -1) {
			n = fis.read(buffer);
			if (n > 0)
				digest.update(buffer, 0, n);
		}
		return digest.digest();
	}
}
