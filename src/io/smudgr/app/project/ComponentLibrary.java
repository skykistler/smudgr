package io.smudgr.app.project;

import java.util.HashMap;

import io.smudgr.smudge.alg.AlgorithmComponent;
import io.smudgr.util.Reflect;

public class ComponentLibrary {

	private HashMap<String, HashMap<String, Class<?>>> components = new HashMap<String, HashMap<String, Class<?>>>();

	public ComponentLibrary() {
		Reflect reflectComponents = new Reflect(AlgorithmComponent.class);

		for (Class<?> c : reflectComponents.get()) {
			try {
				AlgorithmComponent comp = (AlgorithmComponent) c.newInstance();

				HashMap<String, Class<?>> type = components.get(comp.getType());
				if (type == null) {
					type = new HashMap<String, Class<?>>();
					components.put(comp.getType(), type);
				}

				type.put(comp.getName(), c);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public AlgorithmComponent getNewComponent(String type, String name) {
		if (type == null || name == null)
			return null;

		try {
			HashMap<String, Class<?>> ofType = components.get(type);
			if (ofType == null)
				return null;

			Class<?> compClass = ofType.get(name);

			if (compClass == null)
				return null;

			return (AlgorithmComponent) compClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
}
