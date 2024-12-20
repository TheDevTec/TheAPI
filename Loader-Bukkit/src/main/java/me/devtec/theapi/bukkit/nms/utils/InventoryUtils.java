package me.devtec.theapi.bukkit.nms.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.shared.Pair;
import me.devtec.shared.annotations.Nullable;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.ItemGUI;

public class InventoryUtils {

	public enum DestinationType {
		GUI, PLAYER, PLAYER_FROM_ANVIL
	}

	/**
	 * @apiNote Modify ItemStacks in the "contents" field and then return map of
	 *          modified slots
	 **/
	public static Pair shift(int clickedSlot, @Nullable Player whoShift, @Nullable HolderGUI holder, @Nullable ClickType clickType, DestinationType type, List<Integer> ignoredSlots,
			ItemStack[] contents, ItemStack shiftItem) {
		if (shiftItem == null || shiftItem.getType() == Material.AIR) {
			return Pair.of(0, Collections.emptyMap());
		}
		List<Integer> ignoreSlots = ignoredSlots == null ? Collections.emptyList() : ignoredSlots;
		Map<Integer, ItemStack> modifiedSlots = new HashMap<>();
		List<Integer> corruptedSlots = new ArrayList<>();
		int total = shiftItem.getAmount();
		for (int slot = 0; slot < contents.length; ++slot) {
			ItemStack i = contents[slot];
			if (i == null || i.getType() == Material.AIR || i.getAmount() >= i.getMaxStackSize()) {
				continue;
			}
			if (type == DestinationType.GUI && ignoreSlots.contains(slot)) {
				corruptedSlots.add(slot);
				continue;
			}
			if (i.getAmount() < i.getMaxStackSize() && equals(i, shiftItem)) {
				if (holder != null && whoShift != null && clickType != null && holder.onInteractItem(whoShift, i, i, clickType, slot, type == DestinationType.GUI)) {
					corruptedSlots.add(slot);
					continue;
				}
				int size = i.getAmount() + shiftItem.getAmount();
				if (size > i.getMaxStackSize()) {
					shiftItem.setAmount(size - i.getMaxStackSize());
					i.setAmount(64);
					total = shiftItem.getAmount();
					modifiedSlots.put(slot, i);
					continue;
				}
				total = 0;
				i.setAmount(size);
				modifiedSlots.put(slot, i);
				if (holder != null) {
					holder.onMultipleIteract(whoShift, type == DestinationType.GUI ? modifiedSlots : Collections.emptyMap(), type == DestinationType.GUI ? Collections.emptyMap() : modifiedSlots);
				}
				return Pair.of(total, modifiedSlots);
			}
		}
		int firstEmpty = InventoryUtils.findFirstEmpty(whoShift, holder, clickType, corruptedSlots, type, ignoreSlots, contents, shiftItem);
		if (firstEmpty != -1) {
			contents[firstEmpty] = shiftItem;
			total = 0;
			modifiedSlots.put(firstEmpty, shiftItem);
		} else if (total != 0) {
			shiftItem.setAmount(total);
		}
		if (total != 0 && total == shiftItem.getAmount()) {
			corruptedSlots.add(clickedSlot);
		}
		if (holder != null && !modifiedSlots.isEmpty()) {
			holder.onMultipleIteract(whoShift, type == DestinationType.GUI ? modifiedSlots : Collections.emptyMap(), type == DestinationType.GUI ? Collections.emptyMap() : modifiedSlots);
		}
		return Pair.of(total, modifiedSlots);
	}

	/**
	 * @apiNote Find first empty slot in the "contents" field and then return empty
	 *          slot (air/null/same item slot)
	 **/
	public static int findFirstEmpty(@Nullable Player whoShift, @Nullable HolderGUI holder, @Nullable ClickType clickType, List<Integer> corruptedSlots, DestinationType type,
			List<Integer> ignoredSlots, ItemStack[] contents, ItemStack shiftItem) {
		List<Integer> ignoreSlots = ignoredSlots == null ? Collections.emptyList() : ignoredSlots;
		switch (type) {
		case GUI:
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
		case PLAYER:
			for (int i = 8; i > -1; --i) {
				if (ignoreSlots.contains(i)) {
					continue;
				}
				if (contents[i] == null || contents[i].getType() == Material.AIR) {
					return i;
				}
			}
			for (int i = contents.length - 1; i > 8; --i) {
				if (ignoreSlots.contains(i)) {
					continue;
				}
				if (contents[i] == null || contents[i].getType() == Material.AIR) {
					return i;
				}
			}
			return -1;

		case PLAYER_FROM_ANVIL:
			for (int i = 9; i < contents.length - 1; ++i) {
				if (ignoreSlots.contains(i)) {
					continue;
				}
				if (contents[i] == null || contents[i].getType() == Material.AIR) {
					return i;
				}
			}
			for (int i = 0; i < 9; ++i) {
				if (ignoreSlots.contains(i)) {
					continue;
				}
				if (contents[i] == null || contents[i].getType() == Material.AIR) {
					return i;
				}
			}
		}
		return -1;
	}

	private static boolean equals(ItemStack item, ItemStack second) {
		return item.getType() == second.getType() && item.hasItemMeta() == second.hasItemMeta() && (!item.hasItemMeta() || item.getItemMeta().equals(second.getItemMeta()));
	}

	/**
	 * @apiNote Not usable for normal users. Only for devs modifying
	 *          PacketPlayInWindowClick - convert clicked slot into bukkit slot
	 **/
	public static int convertToPlayerInvSlot(int slot) {
		if (slot <= 26) {
			return slot + 9;
		}
		return slot - 27;
	}

	/**
	 * @apiNote Not usable for normal users. Only for devs modifying
	 *          PacketPlayInWindowClick - build ClickType by mouse & shift click
	 **/
	public static ClickType buildClick(int type, int mouse) {
		boolean shift = type == 2; // QUICK_MOVE

		if (type == 1) { // QUICK_CRAFT
			if (mouse == 1) {
				mouse = 0;
			}
			if (mouse == 5) {
				mouse = 1;
			}
			if (mouse == 9) {
				mouse = 2;
			}
		}

		if (shift) {
			switch (mouse) {
			case 0:
				return ClickType.SHIFT_LEFT_DROP;
			case 1:
				return ClickType.SHIFT_RIGHT_DROP;
			default:
				throw new NoSuchFieldError("Doesn't exist ClickType for shift middle click");
			}
		} else {
			switch (mouse) {
			case 0:
				return ClickType.LEFT_DROP;
			case 1:
				return ClickType.RIGHT_DROP;
			default:
				return ClickType.MIDDLE_DROP;
			}
		}
	}

	public static boolean useItem(Player player, HolderGUI gui, int slot, ClickType mouse) {
		ItemGUI itemGui = gui.getItemGUI(slot);
		boolean stolen = itemGui == null || !itemGui.isUnstealable();
		if (itemGui != null) {
			itemGui.onClick(player, gui, mouse);
		}
		return !stolen;
	}
}
