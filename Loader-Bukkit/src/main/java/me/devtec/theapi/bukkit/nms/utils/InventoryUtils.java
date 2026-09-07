package me.devtec.theapi.bukkit.nms.utils;

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

	private static final Map<Integer, ItemStack> EMPTY_MAP = Collections.emptyMap();

	public enum DestinationType {
		GUI, PLAYER, PLAYER_FROM_ANVIL
	}

	/**
	 * @apiNote Modify ItemStacks in the "contents" field and then return map of
	 *          modified slots
	 **/
	public static Pair shift(int clickedSlot, @Nullable Player whoShift, @Nullable HolderGUI holder,
			@Nullable ClickType clickType, DestinationType type, List<Integer> ignoredSlots, ItemStack[] contents,
			ItemStack shiftItem) {

		if (shiftItem == null || shiftItem.getType() == Material.AIR)
			return Pair.of(0, EMPTY_MAP);

		List<Integer> ignoreSlots = ignoredSlots == null ? Collections.<Integer>emptyList() : ignoredSlots;
		Map<Integer, ItemStack> modifiedSlots = null;

		boolean gui = type == DestinationType.GUI;
		boolean interact = holder != null && whoShift != null && clickType != null;
		int total = shiftItem.getAmount();

		for (int slot = 0; slot < contents.length; ++slot) {
			ItemStack item = contents[slot];

			if (item == null || item.getType() == Material.AIR)
				continue;

			int maxStack = item.getMaxStackSize();

			if (item.getAmount() >= maxStack || gui && !ignoreSlots.isEmpty() && ignoreSlots.contains(slot))
				continue;

			if (!equals(item, shiftItem) || (interact && holder.onInteractItem(whoShift, item, item, clickType, slot, gui)))
				continue;

			int size = item.getAmount() + total;

			if (size > maxStack) {
				total = size - maxStack;
				shiftItem.setAmount(total);
				item.setAmount(maxStack);

				if (modifiedSlots == null)
					modifiedSlots = new HashMap<>();

				modifiedSlots.put(slot, item);
				continue;
			}

			total = 0;
			item.setAmount(size);

			if (modifiedSlots == null)
				modifiedSlots = new HashMap<>();

			modifiedSlots.put(slot, item);

			if (holder != null)
				holder.onMultipleIteract(whoShift, gui ? modifiedSlots : EMPTY_MAP, gui ? EMPTY_MAP : modifiedSlots);

			return Pair.of(0, modifiedSlots);
		}

		int firstEmpty = findFirstEmpty(whoShift, holder, clickType, null, type, ignoreSlots, contents, shiftItem);

		if (firstEmpty != -1) {
			contents[firstEmpty] = shiftItem;
			total = 0;

			if (modifiedSlots == null)
				modifiedSlots = new HashMap<>();

			modifiedSlots.put(firstEmpty, shiftItem);
		}

		if (holder != null && modifiedSlots != null)
			holder.onMultipleIteract(whoShift, gui ? modifiedSlots : EMPTY_MAP, gui ? EMPTY_MAP : modifiedSlots);

		return Pair.of(total, modifiedSlots == null ? EMPTY_MAP : modifiedSlots);
	}

	/**
	 * @apiNote Find first empty slot in the "contents" field and then return empty
	 *          slot (air/null/same item slot)
	 **/
	public static int findFirstEmpty(@Nullable Player whoShift, @Nullable HolderGUI holder,
			@Nullable ClickType clickType, @Nullable List<Integer> corruptedSlots, DestinationType type,
			List<Integer> ignoredSlots, ItemStack[] contents, ItemStack shiftItem) {

		List<Integer> ignoreSlots = ignoredSlots == null ? Collections.<Integer>emptyList() : ignoredSlots;

		switch (type) {
		case GUI:
			for (int slot = 0; slot < contents.length; ++slot) {
				if (!ignoreSlots.isEmpty() && ignoreSlots.contains(slot)) {
					if (corruptedSlots != null)
						corruptedSlots.add(slot);
					continue;
				}

				ItemStack item = contents[slot];

				if (item != null && item.getType() != Material.AIR)
					continue;

				if (holder != null && whoShift != null && clickType != null
						&& holder.onInteractItem(whoShift, item, item, clickType, slot, true)) {
					if (corruptedSlots != null)
						corruptedSlots.add(slot);
					continue;
				}

				return slot;
			}
			return -1;

		case PLAYER:
			for (int i = Math.min(8, contents.length - 1); i >= 0; --i) {
				if (!ignoreSlots.isEmpty() && ignoreSlots.contains(i))
					continue;

				ItemStack item = contents[i];

				if (item == null || item.getType() == Material.AIR)
					return i;
			}

			for (int i = contents.length - 1; i > 8; --i) {
				if (!ignoreSlots.isEmpty() && ignoreSlots.contains(i))
					continue;

				ItemStack item = contents[i];

				if (item == null || item.getType() == Material.AIR)
					return i;
			}
			return -1;

		case PLAYER_FROM_ANVIL:
			for (int i = 9; i < contents.length - 1; ++i) {
				if (!ignoreSlots.isEmpty() && ignoreSlots.contains(i))
					continue;

				ItemStack item = contents[i];

				if (item == null || item.getType() == Material.AIR)
					return i;
			}

			for (int i = 0, max = Math.min(9, contents.length); i < max; ++i) {
				if (!ignoreSlots.isEmpty() && ignoreSlots.contains(i))
					continue;

				ItemStack item = contents[i];

				if (item == null || item.getType() == Material.AIR)
					return i;
			}
			return -1;

		default:
			return -1;
		}
	}

	private static boolean equals(ItemStack item, ItemStack second) {
		if (item.getType() != second.getType())
			return false;

		boolean meta = item.hasItemMeta();

		if (meta != second.hasItemMeta())
			return false;

		return !meta || item.getItemMeta().equals(second.getItemMeta());
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
		boolean shift = type == 2;

		if (type == 1) {
			if (mouse == 1)
				mouse = 0;
			if (mouse == 5)
				mouse = 1;
			if (mouse == 9)
				mouse = 2;
		}

		if (shift)
			switch (mouse) {
			case 0:
				return ClickType.SHIFT_LEFT_DROP;
			case 1:
				return ClickType.SHIFT_RIGHT_DROP;
			default:
				throw new NoSuchFieldError("Doesn't exist ClickType for shift middle click");
			}

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

		if (itemGui == null)
			return false;

		boolean unstealable = itemGui.isUnstealable();
		itemGui.onClick(player, gui, mouse);
		return unstealable;
	}
}