package me.devtec.theapi.bukkit.game.resourcepack;

import me.devtec.shared.components.Component;

import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Optional;

public class ResourcePackOffer {
    private static final char[] HEXDUMP_TABLE = new char[256 * 4];

    static {
        char[] DIGITS = "0123456789abcdef".toCharArray();
        for (int i = 0; i < 256; i++) {
            HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 0x0F];
            HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 0x0F];
        }
    }

    private String url;
    private String sha1;
    private Optional<Component> prompt;
    private boolean force;

    public static ResourcePackOffer url(String url) {
        return new ResourcePackOffer(url);
    }

    private ResourcePackOffer(String url) {
        this.url = url;
        prompt = Optional.empty();
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
            byte[] array = createSha1();
            sha1 = hexDump(array, array.length);
            return this;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String hexDump(byte[] array, int length) {
        if (length == 0) {
            return "";
        }

        char[] buf = new char[length << 1];

        int srcIdx = 0;
        int dstIdx = 0;
        for (; srcIdx < length; srcIdx++, dstIdx += 2) {
            System.arraycopy(
                    HEXDUMP_TABLE, (array[srcIdx] & 0xFF) << 1,
                    buf, dstIdx, 2);
        }

        return new String(buf);
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
        fis.close();
        return digest.digest();
    }
}
