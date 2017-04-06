package io.smudgr.util.source;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.util.Frame;

/**
 * The {@link SourceSet} source is a container of {@link Source} instances,
 * which uses the current source's {@link Source#getFrame()} for the value of
 * {@link SourceSet#getFrame()}
 */
public class SourceSet implements Source {

	@Override
	public String getTypeIdentifier() {
		return "source-set";
	}

	@Override
	public String getTypeName() {
		return "Source Set";
	}

	@Override
	public String getName() {
		return "Set of " + sources.size() + " source(s)";
	}

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

			add(getSourceLibrary().loadSource(path));
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

	@Override
	public Frame getThumbnail() {
		// TODO: generate some kind of composite preview of sources in this set
		Source s = getCurrentSource();

		if (s != null)
			return s.getThumbnail();

		return null;
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
	 * Adds the given {@link Source} to this {@link SourceSet}.
	 * 
	 * @param source
	 *            {@link Source}
	 */
	public void add(Source source) {
		if (source == null)
			return;

		getProject().add(source);
		getSources().add(source);
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
	 * Selects the given {@link Source} as the current {@link Source}. If this
	 * {@link SourceSet} does not contain the given {@link Source}, no action is
	 * taken.
	 * 
	 * @param source
	 *            {@link Source}
	 */
	public void setCurrentSource(Source source) {
		int index = sources.indexOf(source);
		if (index < 0)
			return;

		setCurrentSource(index);
	}

	/**
	 * Selects the given source index as the current {@link Source}.
	 * <p>
	 * If {@code i} is negative or larger than {@link SourceSet#size()}, the
	 * value is wrapped.
	 *
	 * @param index
	 *            index
	 */
	private void setCurrentSource(int index) {
		if (sources.size() == 0) {
			currentSource = 0;
			return;
		}

		if (index == currentSource)
			return;

		Source current = getCurrentSource();
		if (current instanceof Video)
			current.dispose();

		currentSource = index;

		if (currentSource >= sources.size())
			currentSource %= sources.size();
		else if (currentSource < 0)
			currentSource += sources.size();
	}

	/**
	 * Gets a list of {@link Source} instances contained in this set. These may
	 * or may not be fully loaded into memory.
	 * 
	 * @return {@code ArrayList<Source>}
	 * @see #init()
	 * @see Source#getThumbnail()
	 */
	public ArrayList<Source> getSources() {
		return sources;
	}

	@Override
	public void save(PropertyMap pm) {
		Source.super.save(pm);

		for (Source source : sources)
			pm.add(new PropertyMap(source));
	}

	@Override
	public void load(PropertyMap pm) {
		// TODO: Load sources from property map
	}

}
