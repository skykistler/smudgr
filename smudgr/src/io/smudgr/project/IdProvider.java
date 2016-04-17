package io.smudgr.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class IdProvider {
	private static final int MAX_ID = 100000;

	private HashMap<Integer, ProjectElement> idToElement = new HashMap<Integer, ProjectElement>();
	private HashMap<ProjectElement, Integer> elementToId = new HashMap<ProjectElement, Integer>();

	private ArrayList<Integer> available_ids = new ArrayList<Integer>(MAX_ID);
	private Random idPicker = new Random();

	private ArrayList<ProjectElement> toAdd = new ArrayList<ProjectElement>();
	private boolean loading;

	public IdProvider() {
		for (int i = 1; i <= MAX_ID; i++)
			available_ids.add(i);

		loading = true;
	}

	public void finishLoading() {
		loading = false;

		for (ProjectElement element : toAdd) {
			add(element);
			toAdd.remove(element);
		}
	}

	protected void add(ProjectElement element) {
		if (loading) {
			if (!toAdd.contains(element))
				toAdd.add(element);
			return;
		}

		if (getId(element) > -1)
			return;

		put(element, getNewId());
	}

	public void put(ProjectElement element, int id) {
		if (id < 0) {
			System.out.println("Can't set " + element + " to negative ID: " + id);
			return;
		}

		ProjectElement curr = idToElement.get(id);
		if (curr != null) {
			System.out.println("Project ID collision: " + element + " trying to replace " + curr + " at ID: " + id);
			return;
		}

		if (elementToId.containsKey(element))
			remove(element);

		idToElement.put(id, element);
		elementToId.put(element, id);

		consumeId(id);
	}

	public void remove(ProjectElement element) {
		remove(getId(element));
	}

	public void remove(int id) {
		if (available_ids.contains(id))
			return;

		elementToId.remove(getElement(id));
		idToElement.remove(id);

		available_ids.add(id);
	}

	public ProjectElement getElement(int id) {
		return idToElement.get(id);
	}

	public int getId(ProjectElement element) {
		if (!elementToId.containsKey(element))
			return -1;

		return elementToId.get(element);
	}

	private int getNewId() {
		int index = idPicker.nextInt(available_ids.size());
		int id = available_ids.get(index);

		return id;
	}

	private void consumeId(int id) {
		for (int i = 0; i < available_ids.size(); i++)
			if (available_ids.get(i) == id) {
				available_ids.remove(i);
				return;
			}
	}

}
