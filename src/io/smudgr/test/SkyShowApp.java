package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.engine.Rack;
import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.coord.ConvergeCoords;
import io.smudgr.engine.alg.coord.StraightCoords;
import io.smudgr.engine.alg.op.ChannelBleed;
import io.smudgr.engine.alg.op.DataBend;
import io.smudgr.engine.alg.op.Marbeler;
import io.smudgr.engine.alg.op.Operation;
import io.smudgr.engine.alg.op.PixelShift;
import io.smudgr.engine.alg.op.PixelSort;
import io.smudgr.engine.alg.op.SpectralShift;
import io.smudgr.engine.alg.select.RangeSelect;
import io.smudgr.engine.param.NumberParameter;

/**
 * Test file for large smudge used in multiple live shows before.
 */
public class SkyShowApp extends AppStart {

	static String projectPath = "data/show.sproj";

	static boolean overwriteProject = false;

	static String sourcePath = "data/testing";

	static String outputPath = "data/output";

	static String device = "Arturia BeatStep";
	static boolean deviceServer = false;

	static int fullscreenDisplay = -1;
	static boolean monitor = true;

	@Override
	public void buildRack() {
		Rack rack = Controller.getInstance().getProject().getRack();

		// addStraightPixelShift(smudge, true);
		// addStraightPixelShift(smudge, false);

		addSpectralShift(rack);

		// addConvergePixelShift(rack);

		addDatabend(rack);

		// addStraightPixelSort(rack);

		// addChannelBleed(rack);

		addMarbeler(rack);

		addStraightPixelSort(rack);

		// getOperationAlgorithm(rack, new HSVLModifier());
		// getOperationAlgorithm(rack, new ChannelSort());
		// getOperationAlgorithm(rack, new ParticlePush());

		// bind(rack.getParameter("Source Speed"));
		bind(rack.getParameter("Downsample"));
		//
		bind(Controller.getInstance().getAppControl("Source Switcher"));
		// bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Save Project"));
	}

	/**
	 * @param rack
	 *            Rack
	 * @param columns
	 *            boolean
	 */
	public void addStraightPixelShift(Rack rack, boolean columns) {
		PixelShift pixel_shift = new PixelShift();
		Algorithm shift_alg = getOperationAlgorithm(rack, pixel_shift);

		StraightCoords shift_coords = new StraightCoords();
		shift_alg.add(shift_coords);
		shift_coords.getParameter("Vertical").setValue(columns);
		shift_coords.getParameter("Continuous").setValue(false);

		bind(pixel_shift.getParameter("Reverse"));
		bind(pixel_shift.getParameter("Intervals"));
		bind(addAutomator("Animate", pixel_shift.getParameter("Amount")));
	}

	/**
	 * @param rack
	 *            Rack
	 */
	public void addConvergePixelShift(Rack rack) {
		PixelShift pixel_shift = new PixelShift();
		Algorithm shift_alg = getOperationAlgorithm(rack, pixel_shift);

		ConvergeCoords shift_coords = new ConvergeCoords();
		shift_alg.add(shift_coords);
		shift_coords.getParameter("Continuous").setValue(false);

		bind(pixel_shift.getParameter("Reverse"));
		bind(pixel_shift.getParameter("Intervals"));
		bind(addAutomator("Animate", pixel_shift.getParameter("Amount")));
	}

	/**
	 *
	 * @param rack
	 *            Rack
	 */
	public void addStraightPixelSort(Rack rack) {
		PixelSort sort = new PixelSort();
		Algorithm alg = getOperationAlgorithm(rack, sort);

		RangeSelect pixelsort_range = new RangeSelect();
		alg.add(pixelsort_range);
		pixelsort_range.getParameter("Range Length").setValue(0);

		StraightCoords coords = new StraightCoords();
		alg.add(coords);
		coords.getParameter("Vertical").setValue(true);
		coords.getParameter("Continuous").setValue(false);

		bind(pixelsort_range.getParameter("Range Length"));
		bind(sort.getParameter("Reverse"));
		bind(coords.getParameter("Vertical"));
	}

	/**
	 *
	 * @param rack
	 *            Rack
	 */
	public void addConvergePixelSort(Rack rack) {
		PixelSort sort = new PixelSort();
		Algorithm alg = getOperationAlgorithm(rack, sort);

		RangeSelect pixelsort_range = new RangeSelect();
		alg.add(pixelsort_range);
		pixelsort_range.getParameter("Range Length").setValue(0);

		ConvergeCoords coords = new ConvergeCoords();
		alg.add(coords);
		coords.getParameter("Continuous").setValue(false);

		bind(pixelsort_range.getParameter("Range Length"));
		bind(sort.getParameter("Reverse"));
	}

	/**
	 *
	 * @param rack
	 *            Rack
	 */
	public void addDatabend(Rack rack) {
		DataBend databend = new DataBend();
		databend.getParameter("Amount").setValue(2);

		Algorithm alg = getOperationAlgorithm(rack, databend);

		StraightCoords coords = new StraightCoords();
		alg.add(coords);
		coords.getParameter("Vertical").setValue(true);
		coords.getParameter("Continuous").setValue(false);

		RangeSelect databend_range = new RangeSelect();
		databend_range.getParameter("Range Length").setValue(1);
		alg.add(databend_range);

		bind(addAutomator("Animate", databend.getParameter("Target")));
		bind(databend.getParameter("Amount"));
		bind(coords.getParameter("Vertical"));
	}

	/**
	 *
	 * @param rack
	 *            Rack
	 */
	public void addSpectralShift(Rack rack) {
		SpectralShift spectral_shift = new SpectralShift();
		getOperationAlgorithm(rack, spectral_shift);

		spectral_shift.getParameter("Colors").setValue(6);

		bind(addAutomator("Beat Sync", spectral_shift.getParameter("Shift")));

		bind(spectral_shift.getParameter("Colors"));
		bind(spectral_shift.getParameter("Palette"));
	}

	/**
	 *
	 * @param rack
	 *            Rack
	 */
	public void addChannelBleed(Rack rack) {
		ChannelBleed bleed = new ChannelBleed();
		((NumberParameter) bleed.getParameter("Shift Amount")).setContinuous(true);

		getOperationAlgorithm(rack, bleed);

		bind(bleed.getParameter("Shift Amount"));
	}

	/**
	 *
	 * @param rack
	 *            Rack
	 */
	public void addMarbeler(Rack rack) {
		Marbeler marb = new Marbeler();
		getOperationAlgorithm(rack, marb);

		bind(marb.getParameter("Frequency"));
		bind(marb.getParameter("Iterations"));
		bind(marb.getParameter("Strength"));
		// bind(marb.getParameter("Seed"));
	}

	/**
	 * Get a new algorithm wrapping the given operation
	 *
	 * @param rack
	 *            {@link Rack}
	 * @param op
	 *            {@link Operation}
	 * @return {@link Algorithm}
	 */
	public Algorithm getOperationAlgorithm(Rack rack, Operation op) {
		Algorithm alg = new Algorithm();

		alg.getParameter("Enable").setValue(false);
		bind(alg.getParameter("Enable"));

		alg.add(op);

		rack.add(alg);

		return alg;
	}

	/**
	 *
	 */
	public SkyShowApp() {
		super(projectPath, sourcePath, outputPath, device, overwriteProject, deviceServer);

		fullscreenView(fullscreenDisplay);
		if (monitor)
			monitorView();

		// Controller.getInstance().add(new WebsocketView());

		start();
	}

	/**
	 * Start application
	 *
	 * @param args
	 *            {@code String[]}
	 */
	public static void main(String[] args) {
		new SkyShowApp();
	}

}
