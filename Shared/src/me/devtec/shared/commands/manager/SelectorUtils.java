package me.devtec.shared.commands.manager;

import java.util.List;

import me.devtec.shared.commands.selectors.SelectorType;

public interface SelectorUtils {
	public default boolean check(Iterable<String> iterable, String string) {
		for(String text : iterable)
			if(text.equalsIgnoreCase(string))return true;
		return false;
	}
	
	public List<String> buildSelectorKeys(SelectorType[] selectorTypes);
	
	public boolean check(SelectorType[] selectorTypes, String value);

}
