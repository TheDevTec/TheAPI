package me.devtec.shared.components;

import java.util.ArrayList;
import java.util.List;

public interface ComponentTransformer<T> {
	public default T fromString(String string) {
		return this.fromComponent(ComponentAPI.fromString(string));
	}

	public Component toComponent(T value);

	public default Component toComponent(T[] value) {
		Component comp = new Component("");
		List<Component> extra = new ArrayList<>();
		for (T t : value)
			extra.add(this.toComponent(t));
		comp.setExtra(extra);
		return comp;
	}

	public T fromComponent(Component component);

	public T fromComponent(List<Component> components);

	public T[] fromComponents(Component component);

	public T[] fromComponents(List<Component> components);
}
