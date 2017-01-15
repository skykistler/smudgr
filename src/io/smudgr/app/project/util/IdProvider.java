package io.smudgr.app.project.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.smudgr.app.project.ProjectItem;

public class IdProvider {
	private static final int MAX_ID = 100000;

	private HashMap<Integer, ProjectItem> idToItem = new HashMap<Integer, ProjectItem>();
	private HashMap<ProjectItem, Integer> itemToId = new HashMap<ProjectItem, Integer>();

	private ArrayList<Integer> available_ids = new ArrayList<Integer>(MAX_ID);
	private Random idPicker = new Random();

	private ArrayList<ProjectItem> toAdd = new ArrayList<ProjectItem>();
	private boolean loading;

	public IdProvider() {
		for (int i = 1; i <= MAX_ID; i++)
			available_ids.add(i);

		loading = true;
	}

	public void finishLoading() {
		loading = false;

		for (ProjectItem item : toAdd) {
			add(item);
		}

		toAdd.clear();
	}

	public void add(ProjectItem item) {
		if (getId(item) > -1)
			return;

		if (loading) {
			if (!toAdd.contains(item))
				toAdd.add(item);
			return;
		}

		put(item, getNewId());
	}

	public void put(ProjectItem item, int id) {
		if (id < 0) {
			System.out.println("Can't set " + item + " to negative ID: " + id);
			return;
		}

		ProjectItem curr = idToItem.get(id);
		if (curr == item)
			return;

		if (curr != null) {
			System.out.println("Project ID collision: " + item + " trying to replace " + curr + " at ID: " + id);
			return;
		}

		if (itemToId.containsKey(item))
			remove(item);

		idToItem.put(id, item);
		itemToId.put(item, id);

		consumeId(id);
	}

	public void remove(ProjectItem item) {
		int id = getId(item);
		itemToId.remove(item);
		idToItem.remove(id);

		if (id < 0 || available_ids.contains(id))
			return;

		available_ids.add(id);
	}

	public ProjectItem getItem(int id) {
		return idToItem.get(id);
	}

	public int getId(ProjectItem item) {
		if (!itemToId.containsKey(item))
			return -1;

		return itemToId.get(item);
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
