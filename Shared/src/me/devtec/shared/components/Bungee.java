package me.devtec.shared.components;

import java.util.List;

@SuppressWarnings("unchecked")
public interface Bungee<T> {
	public T toBaseComponent(Component component);

	public T toBaseComponent(List<Component> components);
	
	public default T[] toBaseComponents(Component component) {
		return (T[])new Object[] {toBaseComponent(component)};
	}
	
	public default T[] toBaseComponents(List<Component> components) {
		return (T[])new Object[] {toBaseComponent(components)};
	}
}
