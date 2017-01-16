package io.smudgr.app.project.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.smudgr.app.project.Project;
import io.smudgr.app.project.ProjectItem;

/**
 * Tracks {@link ProjectItem} items for the project by managing a
 * {@link ProjectItem} map paired with unique integer IDs.
 */
public class IdProvider {
	private static final int MAX_ID = 100000;

	private HashMap<Integer, ProjectItem> idToItem = new HashMap<Integer, ProjectItem>();
	private HashMap<ProjectItem, Integer> itemToId = new HashMap<ProjectItem, Integer>();

	private ArrayList<Integer> available_ids = new ArrayList<Integer>(MAX_ID);
	private Random idPicker = new Random();

	private ArrayList<ProjectItem> toAdd = new ArrayList<ProjectItem>();
	private boolean loading;

	/**
	 * Instantiate a new {@link IdProvider}
	 */
	public IdProvider() {
		for (int i = 1; i <= MAX_ID; i++)
			available_ids.add(i);

		loading = true;
	}

	/**
	 * Called when the project has finished to loading in order to add any
	 * {@link ProjectItem} items that were added without IDs during load.
	 */
	public void finishLoading() {
		loading = false;

		for (ProjectItem item : toAdd) {
			add(item);
		}

		toAdd.clear();
	}

	/**
	 * Add a {@link ProjectItem} to the ID map with a new unique ID.
	 * <p>
	 * If the item has an ID, it has already been added, and this method does
	 * nothing.
	 * 
	 * @param item
	 *            {@link ProjectItem} to add
	 * 
	 * @see Project#add(ProjectItem)
	 */
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

	/**
	 * Put a {@link ProjectItem} at the given ID in the ID map.
	 * <p>
	 * If the item has already been added at the given ID, this method does
	 * nothing.
	 * <p>
	 * If another item exists at the given ID, this method prints an error and
	 * does nothing.
	 * <p>
	 * If the item exists at a different ID and the given ID is available, this
	 * method removes the old ID reference and then puts them item at the given
	 * ID.
	 * 
	 * @param item
	 *            {@link ProjectItem}
	 * @param id
	 *            {@code int} Positive unique ID.
	 * @see Project#put(ProjectItem, int)
	 */
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

	/**
	 * Remove a {@link ProjectItem} item from the ID map. The old item ID is
	 * made available for use again.
	 * 
	 * @param item
	 *            {@link ProjectItem}
	 * @see Project#remove(ProjectItem)
	 */
	public void remove(ProjectItem item) {
		int id = getId(item);
		itemToId.remove(item);
		idToItem.remove(id);

		if (id < 0 || available_ids.contains(id))
			return;

		available_ids.add(id);
	}

	/**
	 * Get the {@link ProjectItem} at this ID, or null is doesn't exist.
	 * 
	 * @param id
	 *            {@code int}
	 * @return {@link ProjectItem} or null
	 * @see Project#getItem(int)
	 */
	public ProjectItem getItem(int id) {
		return idToItem.get(id);
	}

	/**
	 * Get the ID associated with a given {@link ProjectItem}, or -1 if the item
	 * doesn't exist.
	 * 
	 * @param item
	 *            {@link ProjectItem}
	 * @return The ID of the {@link ProjectItem}, or -1 if not found.
	 * @see Project#getId(ProjectItem)
	 */
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
