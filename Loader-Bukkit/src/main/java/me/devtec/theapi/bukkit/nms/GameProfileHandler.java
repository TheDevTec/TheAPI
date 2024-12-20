package me.devtec.theapi.bukkit.nms;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import me.devtec.theapi.bukkit.BukkitLoader;

public class GameProfileHandler {
    @Getter
    private String username;
    private UUID uuid;

    @Getter
    private final Map<String, PropertyHandler> properties = new HashMap<>();

    public static GameProfileHandler of(String username, UUID uuid) {
        GameProfileHandler profile = new GameProfileHandler();
        profile.setUsername(username);
        profile.setUUID(uuid);
        return profile;
    }

    public static GameProfileHandler of(String username, UUID uuid, PropertyHandler textures) {
        GameProfileHandler profile = of(username,uuid);
        profile.properties.put("textures", textures);
        return profile;
    }

    /**
     * @apiNote Change username of GameProfile
     */
    public GameProfileHandler setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * @return GameProfile's UUID
     * @apiNote Get UUID of GameProfile
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * @apiNote Change UUID of GameProfile
     */
    public GameProfileHandler setUUID(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * @apiNote Set skin textures properties
     */
    public GameProfileHandler setTextures(PropertyHandler textures) {
        properties.put("textures", textures);
        return this;
    }

    /**
     * @apiNote Get handle of GameProfile
     */
    public Object getGameProfile() {
        return BukkitLoader.getNmsProvider().toGameProfile(this);
    }

    @Getter
    public static class PropertyHandler {
        private String name;
        private String values;
        private String signature;

        public static PropertyHandler of(String name, String values) {
            return of(name, values, null);
        }

        public static PropertyHandler of(String name, String values, String signature) {
            PropertyHandler property = new PropertyHandler();
            property.name = name;
            property.values = values;
            property.signature = signature;
            return property;
        }

        /**
         * @apiNote Set property values
         */
        public PropertyHandler setValues(String value) {
            values = value;
            return this;
        }

        /**
         * @apiNote Set property signature
         */
        public PropertyHandler setSignature(String value) {
            signature = value;
            return this;
        }
    }
}
