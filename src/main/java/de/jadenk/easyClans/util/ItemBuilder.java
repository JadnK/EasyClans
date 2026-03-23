package de.jadenk.easyClans.util;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemBuilder {

    private final ItemStack currentItem;
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.currentItem = new ItemStack(material);
        this.itemMeta = this.currentItem.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this.currentItem = new ItemStack(material, amount);
        this.itemMeta = this.currentItem.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.currentItem = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        itemMeta.setDisplayName(colorize(name));
        return this;
    }

    public ItemBuilder addEnchantmentGlow() {
        itemMeta.setEnchantmentGlintOverride(true);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        currentItem.setAmount(amount);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder setLore(String lore) {
        itemMeta.setLore(Collections.singletonList(colorize(lore)));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        List<String> colored = new ArrayList<>();
        for (String line : lore) {
            colored.add(colorize(line));
        }
        itemMeta.setLore(colored);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder addLore(String lore) {
        List<String> loreList = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();

        if (loreList == null) {
            loreList = new ArrayList<>();
        }

        loreList.add(colorize(lore));
        itemMeta.setLore(loreList);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean value) {
        itemMeta.setUnbreakable(value);
        return this;
    }

    public ItemBuilder setCustomModelData(int data) {
        itemMeta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder setColor(Color color) {
        if (itemMeta instanceof LeatherArmorMeta meta) {
            meta.setColor(color);
        }
        return this;
    }

    public ItemBuilder setSkullOwner(String name) {
        if (itemMeta instanceof SkullMeta meta) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            meta.setOwningPlayer(offlinePlayer);
        }
        return this;
    }

    public ItemBuilder setSkullOwner(OfflinePlayer player) {
        if (itemMeta instanceof SkullMeta meta) {
            meta.setOwningPlayer(player);
        }
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder hideAllFlags() {
        itemMeta.addItemFlags(
                ItemFlag.HIDE_ATTRIBUTES,
                ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_PLACED_ON,
                ItemFlag.HIDE_DYE
        );
        return this;
    }

    public ItemStack build() {
        currentItem.setItemMeta(itemMeta);
        return currentItem;
    }

    private String colorize(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "§");
    }
}