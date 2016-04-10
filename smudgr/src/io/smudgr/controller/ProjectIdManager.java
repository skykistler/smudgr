package io.smudgr.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ProjectIdManager {
	private static final int MAX_ID = 100000;

	private HashMap<Integer, HasProjectId> idToComponent = new HashMap<Integer, HasProjectId>();
	private HashMap<HasProjectId, Integer> componentToId = new HashMap<HasProjectId, Integer>();

	private ArrayList<Integer> available_ids = new ArrayList<Integer>(MAX_ID);
	private Random idPicker = new Random();

	public ProjectIdManager() {
		for (int i = 0; i < MAX_ID; i++)
			available_ids.add(i);
	}

	public void add(HasProjectId component) {
		if (componentToId.containsKey(component))
			return;

		put(component, getNewId());
	}

	public void put(HasProjectId component, int id) {
		HasProjectId curr = idToComponent.get(id);
		if (curr != null)
			throw new IllegalStateException("Project ID collision: " + component + " trying to replace " + curr);

		if (componentToId.containsKey(component))
			remove(component);

		idToComponent.put(id, component);
		componentToId.put(component, id);
		consumeId(id);
	}

	public void remove(HasProjectId component) {
		remove(getId(component));
	}

	public void remove(int id) {
		if (available_ids.contains(id))
			return;

		componentToId.remove(getComponent(id));
		idToComponent.remove(id);

		available_ids.add(id);
	}

	public HasProjectId getComponent(int id) {
		return idToComponent.get(id);
	}

	public int getId(HasProjectId component) {
		return componentToId.get(component);
	}

	private int getNewId() {
		int index = idPicker.nextInt(available_ids.size());
		int id = available_ids.get(index);

		return id;
	}

	private void consumeId(int id) {
		for (int i = 0; i < available_ids.size(); i++) {
			if (available_ids.get(i) == id) {
				available_ids.remove(i);
				return;
			}
		}
	}

	public interface HasProjectId {

		public default ProjectIdManager getIdManager() {
			return BaseController.getInstance().getIdManager();
		}

	}
}
