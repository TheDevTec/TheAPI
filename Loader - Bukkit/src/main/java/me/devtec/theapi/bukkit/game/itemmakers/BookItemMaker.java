package me.devtec.theapi.bukkit.game.itemmakers;

import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.json.Json;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.xseries.XMaterial;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BookItemMaker extends ItemMaker {
    private String author;
    private String title;
    private List<Component> pages;
    private String generation;

    public BookItemMaker(boolean written) {
        super(written ? Material.WRITTEN_BOOK : XMaterial.WRITABLE_BOOK.parseMaterial());
    }

    @Override
    public Map<String, Object> serializeToMap() {
        Map<String, Object> map = super.serializeToMap();
        if (author != null)
            map.put("book.author", author);
        if (title != null)
            map.put("book.title", title);
        if (pages != null) {
            List<String> jsonList = new ArrayList<>();
            for (Component page : pages)
                jsonList.add(Json.writer().simpleWrite(ComponentAPI.toJsonList(page)));
            map.put("book.pages", jsonList);
        }
        if (generation != null)
            map.put("book.generation", generation);
        return map;
    }

    public BookItemMaker author(String author) {
        this.author = ColorUtils.colorize(author);
        return this;
    }

    public BookItemMaker rawAuthor(String author) {
        this.author = author;
        return this;
    }

    @Nullable
    public String getAuthor() {
        return author;
    }

    public BookItemMaker title(String title) {
        this.title = ColorUtils.colorize(title);
        return this;
    }

    public BookItemMaker rawTitle(String title) {
        this.title = title;
        return this;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public BookItemMaker generation(String generation) {
        this.generation = generation;
        return this;
    }

    public BookItemMaker pages(String... pages) {
        return this.pages(Arrays.asList(pages));
    }

    @Nullable
    public String getGeneration() {
        return generation;
    }

    public BookItemMaker pages(List<String> pages) {
        this.pages = new ArrayList<>();
        if (pages != null)
            for (String string : pages)
                this.pages.add(ComponentAPI.fromString(ColorUtils.colorize(string)));
        return this;
    }

    public BookItemMaker rawPages(List<String> pages) {
        this.pages = new ArrayList<>();
        if (pages != null)
            for (String string : pages)
                this.pages.add(ComponentAPI.fromString(string));
        return this;
    }

    public BookItemMaker pagesComp(Component... pages) {
        return this.pagesComp(Arrays.asList(pages));
    }

    public BookItemMaker pagesComp(List<Component> pages) {
        this.pages = pages == null ? null : new ArrayList<>(pages);
        return this;
    }

    @Nullable
    public List<Component> getPages() {
        return pages;
    }

    @Override
    public ItemMaker clone() {
        BookItemMaker maker = (BookItemMaker) super.clone();
        return maker.author(author).pagesComp(pages).generation(generation).title(title);
    }

    @Override
    protected ItemMeta apply(ItemMeta meta) {
        if (!(meta instanceof BookMeta))
            return super.apply(meta);
        BookMeta iMeta = (BookMeta) meta;
        if (author != null)
            iMeta.setAuthor(author);
        if (pages != null)
            if (!Ref.isNewerThan(11) || Ref.serverType() == ServerType.BUKKIT) {
                List<String> page = new ArrayList<>(pages.size());
                for (Component comp : pages)
                    page.add(comp.toString());
                iMeta.setPages(page);
            } else
                for (Component page : pages)
                    iMeta.spigot().addPage((BaseComponent[]) ComponentAPI.bungee().fromComponents(page));
        if (Ref.isNewerThan(9) && generation != null)
            iMeta.setGeneration(Generation.valueOf(generation.toUpperCase()));
        if (title != null)
            iMeta.setTitle(title);
        return super.apply(iMeta);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (author != null)
            hash = hash * 33 + author.hashCode();
        if (title != null)
            hash = hash * 33 + title.hashCode();
        if (pages != null)
            hash = hash * 33 + pages.hashCode();
        if (generation != null)
            hash = hash * 33 + generation.hashCode();
        return hash;
    }
}