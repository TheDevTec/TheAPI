package me.devtec.shared.components;

import java.util.List;

public interface Adventure<T> {
	public T toBaseComponent(Component component);

	public T toBaseComponent(List<Component> components);

	public T[] toBaseComponents(Component component);

	public T[] toBaseComponents(List<Component> components);
}
