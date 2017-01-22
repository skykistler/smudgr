package io.smudgr.util.source;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import io.smudgr.util.Frame;

/**
 * The {@link SourceSet} source is a container of {@link Source} instances,
 * which uses the current source's {@link Source#getFrame()} for the value of
 * {@link SourceSet#getFrame()}
 */
public class SourceSet implements Source {
	private ArrayList<String> files = new ArrayList<String>();
	private ArrayList<Source> sources = new ArrayList<Source>();
	private int currentSource;

	/**
	 * Create a new {@link SourceSet} using files at a given location.
	 *
	 * @param location
	 *            directory
	 */
	public SourceSet(String location) {
		File directory = new File(location);

		if (!directory.exists()) {
			System.out.println("File " + location + " does not exist!");
			return;
		}

		// If not a directory, just add the one file
		if (!directory.isDirectory())
			files.add(location);
		else {
			String[] list = directory.list();
			Arrays.sort(list);
			for (int i = 0; i < list.length; i++)
				if (!list[i].toLowerCase().equals(".ds_store"))
					files.add(location + "/" + list[i]);
		}

		for (int i = 0; i < files.size(); i++) {
			String path = files.get(i);

			Source s = getSourceLibrary().getSource(path);
			if (s != null) {
				sources.add(s);
			}
		}
	}

	@Override
	public void init() {
		System.out.println("Loading " + files.size() + " source files...");

		for (Source s : sources)
			s.init();
	}

	@Override
	public void update() {
		Source s = getCurrentSource();
		if (s == null)
			return;

		s.update();
	}

	@Override
	public void dispose() {
		for (Source s : sources)
			s.dispose();
	}

	@Override
	public Frame getFrame() {
		Source s = getCurrentSource();
		if (s == null || s == this)
			return null;

		return s.getFrame();
	}

	/**
	 * Gets how many sources are contained in this {@link SourceSet}
	 *
	 * @return amount
	 */
	public int size() {
		return sources.size();
	}

	/**
	 * Gets the current source selected by this {@link SourceSet}
	 *
	 * @return {@link Source}
	 */
	public Source getCurrentSource() {
		if (sources.size() == 0)
			return null;

		return sources.get(currentSource);
	}

	/**
	 * Select the next source in the set as the current {@link Source}
	 *
	 * @see SourceSet#getCurrentSource()
	 */
	public void nextSource() {
		setCurrentSource(currentSource + 1);
	}

	/**
	 * Select the previous source in the set as the current {@link Source}
	 *
	 * @see SourceSet#getCurrentSource()
	 */
	public void previousSource() {
		setCurrentSource(currentSource - 1);
	}

	/**
	 * Selects the given source index as the current {@link Source}.
	 * <p>
	 * If {@code i} is negative or larger than {@link SourceSet#size()}, the
	 * value is wrapped.
	 *
	 * @param i
	 *            index
	 */
	public void setCurrentSource(int i) {
		if (sources.size() == 0) {
			currentSource = 0;
			return;
		}

		if (i == currentSource)
			return;

		Source current = getCurrentSource();
		if (current instanceof Video)
			current.dispose();

		currentSource = i;

		if (currentSource >= sources.size())
			currentSource %= sources.size();
		else if (currentSource < 0)
			currentSource += sources.size();
	}

}
