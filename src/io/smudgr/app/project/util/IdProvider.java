package io.smudgr.app.project.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.smudgr.app.project.Project;
import io.smudgr.app.project.ProjectItem;

/**
 * Tracks {@link ProjectItem} items for the project by managing a
 * {@link ProjectItem} map paired with unique integer IDs.
 */
public class IdProvider {
	private static final int ID_START = 1000;
	private static final int ID_BATCH_SIZE = 100;

	private int highestId = 0;

	private HashMap<Integer, ProjectItem> idToItem = new HashMap<Integer, ProjectItem>();
	private HashMap<ProjectItem, Integer> itemToId = new HashMap<ProjectItem, Integer>();

	private List<Integer> available_ids = Collections.synchronizedList(new ArrayList<Integer>(ID_BATCH_SIZE));
	private Random idPicker = new Random();

	private ArrayList<ProjectItem> toAdd = new ArrayList<ProjectItem>();
	private boolean loading;

	/**
	 * Instantiate a new {@link IdProvider}
	 */
	public IdProvider() {

		/*
		 * Make sure to start at least from ID_START. Nothing should break if
		 * the project contains IDs lower than this, but generation should
		 * always start at ID_START
		 */

		consumeId(ID_START);
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
		if (contains(item))
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
	 * Remove a {@link ProjectItem} item from the ID map. The old ID won't be
	 * used again.
	 *
	 * @param item
	 *            {@link ProjectItem}
	 * @see Project#remove(ProjectItem)
	 */
	public void remove(ProjectItem item) {
		int id = getId(item);
		itemToId.remove(item);
		idToItem.remove(id);

		consumeId(id);
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

	/**
	 * Gets whether the given {@link ProjectItem} is already contained.
	 *
	 * @param item
	 *            {@link ProjectItem}
	 * @return {@code true} if the project contains this item, {@code false} if
	 *         otherwise
	 */
	public boolean contains(ProjectItem item) {
		return getId(item) > -1;
	}

	/**
	 * Gets an available ID never used in this project.
	 * 
	 * @return
	 */
	private int getNewId() {
		int index = idPicker.nextInt(available_ids.size());
		int id = available_ids.get(index);

		return id;
	}

	/**
	 * Manages a list of available/unused IDs, and ensures the given ID will not
	 * be used again. If the given ID is higher than the current highest unused
	 * ID (i.e. a project file was loaded), then all available IDs are discarded
	 * and regenerated, starting after that ID.
	 * <p>
	 * Because no new IDs are actually assigned until all previous IDs are
	 * loaded, this is a quick and easy way to manage a small number of unique
	 * IDs while avoiding collisions with previously used IDs.
	 * 
	 * @param id
	 *            Must be greater than -1
	 */
	private void consumeId(int id) {
		if (id < 0)
			return;

		synchronized (available_ids) {
			/*
			 * If an ID higher than the highest available ID was used, clear
			 * available IDs and generate higher IDs to avoid collisions.
			 */
			if (id > highestId) {
				highestId = id;
				available_ids.clear();
			}

			// Remove the id from the list of available ids, if it exists
			for (int i = 0; i < available_ids.size(); i++)
				if (available_ids.get(i) == id) {
					available_ids.remove(i);
					break;
				}

			// Generate next batch of IDs if none left or cleared
			if (available_ids.size() == 0) {
				for (int i = 1; i <= ID_BATCH_SIZE; i++)
					available_ids.add(highestId + i);

				highestId = available_ids.get(available_ids.size() - 1);
			}
		}
	}

}
