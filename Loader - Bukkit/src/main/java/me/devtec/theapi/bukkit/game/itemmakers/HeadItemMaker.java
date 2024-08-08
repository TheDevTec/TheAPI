package me.devtec.theapi.bukkit.game.itemmakers;

import com.google.common.collect.Multimap;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.annotations.Nullable;
import me.devtec.shared.json.Json;
import me.devtec.shared.utility.StreamUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.nms.GameProfileHandler;
import me.devtec.theapi.bukkit.nms.GameProfileHandler.PropertyHandler;
import me.devtec.theapi.bukkit.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

public class HeadItemMaker extends ItemMaker {
    private static Object hdbApi;
    private static int HDB_TYPE;

    static {
        if (Ref.getClass("me.arcaniax.hdb.api.HeadDatabaseAPI") != null) {
            hdbApi = new HeadDatabaseAPI();
            HDB_TYPE = 1; // paid
        }
    }

    private static final Material skull = XMaterial.PLAYER_HEAD.parseMaterial();
    private static final String URL_FORMAT = "https://api.mineskin.org/generate/url?url=%s&%s";
    private static final Method createProfile = Ref.method(Bukkit.class,"createProfile",UUID.class);
    private static Constructor<?> constructor = Ref.constructor(Ref.getClass("com.mojang.authlib.properties.Property"), String.class, String.class, String.class);
    private static final Method setProperty = Ref.method(Ref.getClass("com.destroystokyo.paper.profile.PlayerProfile"),"setProperty",Ref.getClass("com.destroystokyo.paper.profile.ProfileProperty"));
    private static final Method setPlayerProfile = Ref.method(SkullMeta.class,"setPlayerProfile",Ref.getClass("com.destroystokyo.paper.profile.PlayerProfile"));
    private static final Constructor<?> profileProperty= Ref.constructor(Ref.getClass("com.destroystokyo.paper.profile.ProfileProperty"),String.class,String.class);

    private String owner;
    /**
     * 0 = offlinePlayer 1 = player.values 2 = url.png
     */
    private int ownerType;

    public HeadItemMaker() {
        super(skull);
    }

    @Override
    public Map<String, Object> serializeToMap() {
        Map<String, Object> map = super.serializeToMap();
        if (owner != null) {
            map.put("head.type", getFormattedOwnerType());
            map.put("head.owner", owner);
        }
        return map;
    }

    public HeadItemMaker skinName(String name) {
        owner = name;
        ownerType = 0;
        return this;
    }

    public HeadItemMaker skinValues(String name) {
        owner = name;
        ownerType = 1;
        return this;
    }

    public HeadItemMaker skinUrl(String name) {
        owner = name;
        ownerType = 2;
        return this;
    }

    public HeadItemMaker skinHDB(String id) {
        if (hdbApi != null) {
            owner = getBase64OfId(id);
            ownerType = 1;
        } else {
            owner = id;
            ownerType = 0;
        }
        return this;
    }

    @Nullable
    public String getHeadOwner() {
        return owner;
    }

    /**
     * @return int Head owner type
     * @apiNote Return's head owner type. 0 = Name 1 = Values 2 = Url
     */
    public int getHeadOwnerType() {
        return ownerType;
    }

    public String getFormattedOwnerType() {
        switch (ownerType) {
            case 0:
                return "PLAYER_NAME";
            case 1:
                return "VALUES";
            case 2:
                return "URL";
        }
        return "PLAYER_NAME";
    }

    @Override
    public ItemMaker clone() {
        HeadItemMaker maker = (HeadItemMaker) super.clone();
        maker.owner = owner;
        maker.ownerType = ownerType;
        return maker;
    }

    @Override
    protected ItemMeta apply(ItemMeta meta) {
        if (!(meta instanceof SkullMeta))
            return super.apply(meta);
        SkullMeta iMeta = (SkullMeta) meta;
        String finalValue = owner;
        if (finalValue != null)
            switch (ownerType) {
                case 0: // Player
                    iMeta.setOwner(finalValue);
                    break;
                case 2: // Url
                    finalValue = fromUrl(owner);
                case 1: { // Values
                    if (finalValue == null) break;
                    byte[] decodedBytes = decode(finalValue);
                    long mostSignificant = 0;
                    long leastSignificant = 0;
                    for (int i = 0; i < 8; ++i)
                        mostSignificant = mostSignificant << 8 | decodedBytes[i] & 0xff;
                    for (int i = 8; i < 16; ++i)
                        leastSignificant = leastSignificant << 8 | decodedBytes[i] & 0xff;
                    UUID uuid = new UUID(mostSignificant, leastSignificant);
                    if (Ref.isNewerThan(16) && Ref.serverType() == ServerType.PAPER) {
                        Object profile = Ref.invokeStatic(createProfile,uuid);
                        Ref.invoke(profile,setProperty,Ref.newInstance(profileProperty,"textures", finalValue));
                        Ref.invoke(iMeta,setPlayerProfile,profile);
                    } else if (Ref.isNewerThan(17)) {
                        PlayerProfile profile = Bukkit.createPlayerProfile(uuid,"");
                        @SuppressWarnings("unchecked")
                        Multimap<String, Object> props = (Multimap<String, Object>) Ref.get(profile, SKIN_PROPERTIES);
                        props.removeAll("textures");
                        Object property = Ref.newInstance(constructor, "textures", finalValue, null);
                        props.put("textures", property);
                        iMeta.setOwnerProfile(profile);
                    } else
                        Ref.set(iMeta, PROFILE_FIELD, BukkitLoader.getNmsProvider().toGameProfile(GameProfileHandler.of("", uuid, PropertyHandler.of("textures", finalValue))));
                    break;
                }
                default: // New dimension
                    break;
            }
        return super.apply(iMeta);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (owner != null)
            hash = hash * 33 + owner.hashCode();
        return hash * 33 + ownerType;
    }

    private static final String BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    public static byte[] decode(String input) {
        int padding = countPaddingChars(input);
        int length = input.length();
        int outputLength = length * 6 / 8 - padding;

        byte[] output = new byte[outputLength];

        int buffer = 0;
        int bufferLength = 0;
        int index = 0;

        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (c == '=')
                break; // Padding detected, end of input
            int value = BASE64_ALPHABET.indexOf(c);
            if (value == -1)
                throw new IllegalArgumentException("Invalid Base64 character: " + c);

            buffer = buffer << 6 | value;
            bufferLength += 6;

            if (bufferLength >= 8) {
                bufferLength -= 8;
                output[index++] = (byte) (buffer >> bufferLength & 0xFF);
            }
        }

        if (index != outputLength)
            throw new IllegalArgumentException("Input length is not a multiple of 4.");

        return output;
    }

    private static int countPaddingChars(String input) {
        int count = 0;
        int length = input.length();
        for (int i = length - 1; i >= 0; i--)
            if (input.charAt(i) == '=')
                count++;
            else
                break;
        return count;
    }

    @SuppressWarnings("unchecked")
    public static String fromUrl(String url) {
        try {
            java.net.URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "DevTec-JavaClient");
            HttpURLConnection conn = (HttpURLConnection) new URL(String.format(URL_FORMAT, url, "name=DevTec&model=steve&visibility=1")).openConnection();
            conn.setRequestProperty("User-Agent", "DevTec-JavaClient");
            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestMethod("POST");
            conn.connect();
            Map<String, Object> text = (Map<String, Object>) Json.reader().simpleRead(StreamUtils.fromStream(new GZIPInputStream(conn.getInputStream())));
            return (String) ((Map<String, Object>) ((Map<String, Object>) text.get("data")).get("texture")).get("value");
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String getBase64OfId(String headOwner) {
        if (HDB_TYPE == 1)
            return ((HeadDatabaseAPI) hdbApi).getBase64(headOwner);
        return null;
    }

    public static boolean hasHDB() {
        return hdbApi != null;
    }
}