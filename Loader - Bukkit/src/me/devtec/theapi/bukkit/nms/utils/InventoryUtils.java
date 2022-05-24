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

public class InventoryUtils {

	public enum DestinationType {
		PLAYER_INV_CUSTOM_INV, CUSTOM_INV, PLAYER_INV_ANVIL;
	}

	/**
	 * @apiNote Modify ItemStacks in the "contents" field and then return list of
	 *          modified slots
	 **/
	public static List<Integer> shift(int clickedSlot, @Nullable Player whoShift, @Nullable HolderGUI holder,
			@Nullable ClickType clickType, DestinationType type, List<Integer> ignoreSlots, ItemStack[] contents,
			ItemStack shiftItem) {
		if (shiftItem == null || shiftItem.getType() == Material.AIR)
			return Collections.emptyList();
		if (ignoreSlots == null)
			ignoreSlots = Collections.emptyList();
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
			if (i.getAmount() < i.getMaxStackSize() && i.getType() == shiftItem.getType()
					&& i.getItemMeta().equals(shiftItem.getItemMeta())
					&& i.getDurability() == shiftItem.getDurability()) {
				if (holder != null && whoShift != null && clickType != null
						&& holder.onIteractItem(whoShift, i, clickType, slot, type == DestinationType.CUSTOM_INV)) {
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
					BukkitLoader.getPacketHandler().send(whoShift, BukkitLoader.getNmsProvider().packetSetSlot(
							BukkitLoader.getNmsProvider().getContainerId(holder.getContainer(whoShift)), s, state,
							BukkitLoader.getNmsProvider().getSlotItem(holder.getContainer(whoShift), s)));
				return modifiedSlots;
			}
		}
		int firstEmpty = findFirstEmpty(whoShift, holder, clickType, corruptedSlots, type, ignoreSlots, contents);
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
			BukkitLoader.getPacketHandler().send(whoShift,
					BukkitLoader.getNmsProvider().packetSetSlot(
							BukkitLoader.getNmsProvider().getContainerId(holder.getContainer(whoShift)), slot, state,
							BukkitLoader.getNmsProvider().getSlotItem(holder.getContainer(whoShift), slot)));
		return modifiedSlots;
	}

	/**
	 * @apiNote Find first empty slot in the "contents" field and then return empty
	 *          slot (air/null slot)
	 **/
	public static int findFirstEmpty(@Nullable Player whoShift, @Nullable HolderGUI holder,
			@Nullable ClickType clickType, List<Integer> corruptedSlots, DestinationType type,
			List<Integer> ignoreSlots, ItemStack[] contents) {
		if (ignoreSlots == null)
			ignoreSlots = Collections.emptyList();
		switch (type) {
		case PLAYER_INV_ANVIL:
			for (int i = contents.length - 1; i > 8; --i) {
				if (ignoreSlots.contains(i))
					continue;
				if (contents[i] == null || contents[i].getType() == Material.AIR) {
					return i;
				}
			}
			for (int i = 8; i > -1; --i) {
				if (ignoreSlots.contains(i))
					continue;
				if (contents[i] == null || contents[i].getType() == Material.AIR) {
					return i;
				}
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
					if (holder != null && whoShift != null && clickType != null
							&& holder.onIteractItem(whoShift, i, clickType, slot - 1, true)) {
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
				if (contents[i] == null || contents[i].getType() == Material.AIR) {
					return i;
				}
			}
			for (int i = contents.length - 1; i > -1; --i) {
				if (ignoreSlots.contains(i))
					continue;
				if (contents[i] == null || contents[i].getType() == Material.AIR) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * @apiNote Not usable for normal users. Only for devs modifying
	 *          PacketPlayInWindowClick - convert clicked slot into bukkit slot
	 **/
	public static int convertToPlayerInvSlot(int slot) {
		switch (slot) {
		case 0:
			return 9;
		case 1:
			return 10;
		case 2:
			return 11;
		case 3:
			return 12;
		case 4:
			return 13;
		case 5:
			return 14;
		case 6:
			return 15;
		case 7:
			return 16;
		case 8:
			return 17;
		case 9:
			return 18;
		case 10:
			return 19;
		case 11:
			return 20;
		case 12:
			return 21;
		case 13:
			return 22;
		case 14:
			return 23;
		case 15:
			return 24;
		case 16:
			return 25;
		case 17:
			return 26;
		case 18:
			return 27;
		case 19:
			return 28;
		case 20:
			return 29;
		case 21:
			return 30;
		case 22:
			return 31;
		case 23:
			return 32;
		case 24:
			return 33;
		case 25:
			return 34;
		case 26:
			return 35;
		// hotbar
		case 27:
			return 0;
		case 28:
			return 1;
		case 29:
			return 2;
		case 30:
			return 3;
		case 31:
			return 4;
		case 32:
			return 5;
		case 33:
			return 6;
		case 34:
			return 7;
		case 35:
			return 8;
		default:
			return 0;
		}
	}
}
