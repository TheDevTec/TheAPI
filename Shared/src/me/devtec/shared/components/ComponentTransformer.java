package me.devtec.shared.components;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public interface ComponentTransformer<T> {
	public default T fromString(String string) {
		return fromComponent(ComponentAPI.fromString(string));
	}
	
	public Component toComponent(T value);
	
	public default Component toComponent(T[] value) {
		Component comp = new Component("");
		List<Component> extra = new ArrayList<>();
		for(T t : value) {
			extra.add(toComponent(t));
		}
		comp.setExtra(extra);
		return comp;
	}
	
	public T fromComponent(Component component);

	public T fromComponent(List<Component> components);
	
	public default T[] fromComponents(Component component) {
		return (T[])new Object[] {fromComponent(component)};
	}
	
	public default T[] fromComponents(List<Component> components) {
		return (T[])new Object[] {fromComponent(components)};
	}
}
