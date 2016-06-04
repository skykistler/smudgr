package io.smudgr.project.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.smudgr.project.ProjectElement;

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
		}

		toAdd.clear();
	}

	public void add(ProjectElement element) {
		if (getId(element) > -1)
			return;

		if (loading) {
			if (!toAdd.contains(element))
				toAdd.add(element);
			return;
		}

		put(element, getNewId());
	}

	public void put(ProjectElement element, int id) {
		if (id < 0) {
			System.out.println("Can't set " + element + " to negative ID: " + id);
			return;
		}

		ProjectElement curr = idToElement.get(id);
		if (curr == element)
			return;

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
		int id = getId(element);
		elementToId.remove(element);
		idToElement.remove(id);

		if (id < 0 || available_ids.contains(id))
			return;

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
