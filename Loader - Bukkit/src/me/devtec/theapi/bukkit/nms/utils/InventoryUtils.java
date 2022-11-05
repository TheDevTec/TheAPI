package me.devtec.theapi.bukkit.nms.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.ItemGUI;

public class InventoryUtils {

	public enum DestinationType {
		PLAYER_INV_CUSTOM_INV, CUSTOM_INV, PLAYER_INV_ANVIL;
	}

	/**
	 * @apiNote Modify ItemStacks in the "contents" field and then return list of
	 *          modified slots
	 **/
	public static List<Integer> shift(int clickedSlot, @Nullable Player whoShift, @Nullable HolderGUI holder, @Nullable ClickType clickType, DestinationType type, List<Integer> ignoredSlots,
			ItemStack[] contents, ItemStack shiftItem) {
		if (shiftItem == null || shiftItem.getType() == Material.AIR)
			return Collections.emptyList();
		List<Integer> ignoreSlots = ignoredSlots == null ? Collections.emptyList() : ignoredSlots;
		List<Integer> modifiedSlots = new ArrayList<>();
		List<Integer> corruptedSlots = new ArrayList<>();
		int total = shiftItem.getAmount();
		int state = BukkitLoader.getNmsProvider().getContainerStateId(holder.getContainer(whoShift));
		for (int slot = 0; slot < contents.length; ++slot) {
			ItemStack i = contents[slot];
			if (i == null || i.getType() == Material.AIR || i.getAmount() >= i.getMaxStackSize())
				continue;
			if (type == DestinationType.CUSTOM_INV && ignoreSlots.contains(slot))
				continue;
			if (i.getAmount() < i.getMaxStackSize() && i.getType() == shiftItem.getType() && i.getItemMeta().equals(shiftItem.getItemMeta()) && i.getDurability() == shiftItem.getDurability()) {
				if (holder != null && whoShift != null && clickType != null && holder.onInteractItem(whoShift, i, i, clickType, slot, type == DestinationType.CUSTOM_INV)) {
					corruptedSlots.add(slot);
					continue;
				}
				int size = i.getAmount() + shiftItem.getAmount();
				if (size > i.getMaxStackSize()) {
					shiftItem.setAmount(size - i.getMaxStackSize());
					i.setAmount(64);
					total = shiftItem.getAmount();
					modifiedSlots.add(slot);
					continue;
				}
				total = 0;
				i.setAmount(size);
				modifiedSlots.add(slot);
				if (total != 0 && total == shiftItem.getAmount())
					corruptedSlots.add(clickedSlot);
				for (int s : corruptedSlots)
					BukkitLoader.getPacketHandler().send(whoShift, BukkitLoader.getNmsProvider().packetSetSlot(BukkitLoader.getNmsProvider().getContainerId(holder.getContainer(whoShift)), s, state,
							BukkitLoader.getNmsProvider().getSlotItem(holder.getContainer(whoShift), s)));
				return modifiedSlots;
			}
		}
		int firstEmpty = InventoryUtils.findFirstEmpty(whoShift, holder, clickType, corruptedSlots, type, ignoreSlots, contents);
		if (firstEmpty != -1) {
			contents[firstEmpty] = shiftItem;
			total = 0;
			modifiedSlots.add(firstEmpty);
		} else if (total != 0) {
			modifiedSlots.add(-1); // self
			shiftItem.setAmount(total);
		}
		if (total != 0 && total == shiftItem.getAmount())
			corruptedSlots.add(clickedSlot);
		for (int slot : corruptedSlots)
			BukkitLoader.getPacketHandler().send(whoShift, BukkitLoader.getNmsProvider().packetSetSlot(BukkitLoader.getNmsProvider().getContainerId(holder.getContainer(whoShift)), slot, state,
					BukkitLoader.getNmsProvider().getSlotItem(holder.getContainer(whoShift), slot)));
		return modifiedSlots;
	}

	/**
	 * @apiNote Find first empty slot in the "contents" field and then return empty
	 *          slot (air/null slot)
	 **/
	public static int findFirstEmpty(@Nullable Player whoShift, @Nullable HolderGUI holder, @Nullable ClickType clickType, List<Integer> corruptedSlots, DestinationType type,
			List<Integer> ignoredSlots, ItemStack[] contents) {
		List<Integer> ignoreSlots = ignoredSlots == null ? Collections.emptyList() : ignoredSlots;
		switch (type) {
		case PLAYER_INV_ANVIL:
			for (int i = 9; i < contents.length - 1; ++i) {
				if (ignoreSlots.contains(i))
					continue;
				if (contents[i] == null || contents[i].getType() == Material.AIR)
					return i;
			}
			for (int i = 0; i < 9; ++i) {
				if (ignoreSlots.contains(i))
					continue;
				if (contents[i] == null || contents[i].getType() == Material.AIR)
					return i;
			}
			return -1;
		case CUSTOM_INV:
			int slot = 0;
			for (ItemStack i : contents) {
				if (ignoreSlots.contains(slot++)) {
					corruptedSlots.add(slot - 1);
					continue;
				}
				if (i == null || i.getType() == Material.AIR) {
					if (holder != null && whoShift != null && clickType != null && holder.onInteractItem(whoShift, i, i, clickType, slot - 1, true)) {
						corruptedSlots.add(slot - 1);
						continue;
					}
					return slot - 1;
				}
			}
			return -1;
		case PLAYER_INV_CUSTOM_INV:
			for (int i = 8; i > -1; --i) {
				if (ignoreSlots.contains(i))
					continue;
				if (contents[i] == null || contents[i].getType() == Material.AIR)
					return i;
			}
			for (int i = contents.length - 1; i > -1; --i) {
				if (ignoreSlots.contains(i))
					continue;
				if (contents[i] == null || contents[i].getType() == Material.AIR)
					return i;
			}
		}
		return -1;
	}

	/**
	 * @apiNote Not usable for normal users. Only for devs modifying
	 *          PacketPlayInWindowClick - convert clicked slot into bukkit slot
	 **/
	public static int convertToPlayerInvSlot(int slot) {
		if (slot <= 26)
			return slot + 9;
		return slot - 27;
	}

	/**
	 * @apiNote Not usable for normal users. Only for devs modifying
	 *          PacketPlayInWindowClick - build ClickType by mouse & shift click
	 **/
	public static ClickType buildClick(int type, int mouse) {
		boolean shift = type == 2; // QUICK_MOVE
		boolean pickup = false;

		if (type == 1) { // QUICK_CRAFT
			if (mouse == 1)
				mouse = 0;
			if (mouse == 5)
				mouse = 1;
			if (mouse == 9)
				mouse = 2;
		}

		if (shift) {
			if (pickup)
				switch (mouse) {
				case 0:
					return ClickType.SHIFT_LEFT_PICKUP;
				case 1:
					return ClickType.SHIFT_RIGHT_PICKUP;
				default:
					throw new NoSuchFieldError("Doesn't exist ClickType for shift middle click");
				}
			else
				switch (mouse) {
				case 0:
					return ClickType.SHIFT_LEFT_DROP;
				case 1:
					return ClickType.SHIFT_RIGHT_DROP;
				default:
					throw new NoSuchFieldError("Doesn't exist ClickType for shift middle click");
				}
		} else if (pickup)
			switch (mouse) {
			case 0:
				return ClickType.LEFT_PICKUP;
			case 1:
				return ClickType.RIGHT_PICKUP;
			default:
				return ClickType.MIDDLE_PICKUP;
			}
		else
			switch (mouse) {
			case 0:
				return ClickType.LEFT_DROP;
			case 1:
				return ClickType.RIGHT_DROP;
			default:
				return ClickType.MIDDLE_DROP;
			}
	}

	public static boolean useItem(Player player, HolderGUI gui, int slot, ClickType mouse) {
		ItemGUI itemGui = gui.getItemGUI(slot);
		boolean stolen = itemGui == null || !itemGui.isUnstealable();
		if (itemGui != null)
			itemGui.onClick(player, gui, mouse);
		return !stolen;
	}
}
