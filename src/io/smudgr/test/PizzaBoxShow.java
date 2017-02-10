package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.engine.Smudge;
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
public class PizzaBoxShow extends AppStart {

	static String projectPath = "data/pizza";

	static boolean overwriteSmudge = false;

	static String sourcePath = "data/testing";

	static String outputPath = "data";

	static String device = "Arturia BeatStepPro";
	static boolean deviceServer = false;

	static int fullscreenDisplay = -1;
	static boolean monitor = true;

	@Override
	public void buildSmudge() {
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		// addStraightPixelShift(smudge, true);
		// addStraightPixelShift(smudge, false);

		addConvergePixelShift(smudge);

		// addStraightPixelSort(smudge);

		// addDatabend(smudge);

		// addChannelBleed(smudge);

		// addSpectralShift(smudge);

		// addMarbeler(smudge);

		// bind(smudge.getParameter("Source Speed"));
		// bind(smudge.getParameter("Downsample"));
		//
		// bind(Controller.getInstance().getAppControl("Source Switcher"));
		// bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		// bind(Controller.getInstance().getAppControl("Save Project"));
	}

	/**
	 * @param smudge
	 *            Smudge
	 * @param columns
	 *            boolean
	 */
	public void addStraightPixelShift(Smudge smudge, boolean columns) {
		PixelShift pixel_shift = new PixelShift();
		Algorithm shift_alg = getOperationAlgorithm(smudge, pixel_shift);

		StraightCoords shift_coords = new StraightCoords();
		shift_alg.add(shift_coords);
		shift_coords.getParameter("Vertical").setValue(columns);
		shift_coords.getParameter("Continuous").setValue(false);

		bind(pixel_shift.getParameter("Reverse"));
		bind(pixel_shift.getParameter("Intervals"));
		bind(addAutomator("Animate", pixel_shift.getParameter("Amount")));
	}

	/**
	 * @param smudge
	 *            Smudge
	 */
	public void addConvergePixelShift(Smudge smudge) {
		PixelShift pixel_shift = new PixelShift();
		Algorithm shift_alg = getOperationAlgorithm(smudge, pixel_shift);

		ConvergeCoords shift_coords = new ConvergeCoords();
		shift_alg.add(shift_coords);
		shift_coords.getParameter("Continuous").setValue(false);

		bind(pixel_shift.getParameter("Reverse"));
		bind(pixel_shift.getParameter("Intervals"));
		bind(addAutomator("Animate", pixel_shift.getParameter("Amount")));
	}

	/**
	 *
	 * @param smudge
	 *            Smudge
	 */
	public void addStraightPixelSort(Smudge smudge) {
		PixelSort column_sort = new PixelSort();
		Algorithm colum_sort_alg = getOperationAlgorithm(smudge, column_sort);

		RangeSelect pixelsort_range = new RangeSelect();
		colum_sort_alg.add(pixelsort_range);
		pixelsort_range.getParameter("Range Length").setValue(0);

		StraightCoords coords = new StraightCoords();
		colum_sort_alg.add(coords);
		coords.getParameter("Vertical").setValue(true);
		coords.getParameter("Continuous").setValue(false);

		bind(pixelsort_range.getParameter("Range Length"));
		bind(coords.getParameter("Vertical"));
	}

	/**
	 *
	 * @param smudge
	 *            Smudge
	 */
	public void addConvergePixelSort(Smudge smudge) {
		PixelSort sort = new PixelSort();
		Algorithm alg = getOperationAlgorithm(smudge, sort);

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
	 * @param smudge
	 *            Smudge
	 */
	public void addDatabend(Smudge smudge) {
		DataBend databend = new DataBend();
		Algorithm databend_alg = getOperationAlgorithm(smudge, databend);

		RangeSelect databend_range = new RangeSelect();
		databend_range.getParameter("Range Length").setValue(1);
		databend_alg.add(databend_range);

		databend.getParameter("Amount").setValue(2);

		bind(addAutomator("Animate", databend.getParameter("Target")));
		bind(databend.getParameter("Amount"));
	}

	/**
	 *
	 * @param smudge
	 *            Smudge
	 */
	public void addSpectralShift(Smudge smudge) {
		SpectralShift spectral_shift = new SpectralShift();
		getOperationAlgorithm(smudge, spectral_shift);

		spectral_shift.getParameter("Colors").setValue(6);

		bind(addAutomator("Beat Sync", spectral_shift.getParameter("Shift")));

		bind(spectral_shift.getParameter("Colors"));
		bind(spectral_shift.getParameter("Palette"));
	}

	/**
	 *
	 * @param smudge
	 *            Smudge
	 */
	public void addChannelBleed(Smudge smudge) {
		ChannelBleed bleed = new ChannelBleed();
		((NumberParameter) bleed.getParameter("Shift Amount")).setContinuous(true);

		getOperationAlgorithm(smudge, bleed);

		bind(bleed.getParameter("Shift Amount"));
	}

	/**
	 *
	 * @param smudge
	 *            Smudge
	 */
	public void addMarbeler(Smudge smudge) {
		Marbeler marb = new Marbeler();
		getOperationAlgorithm(smudge, marb);

		bind(marb.getParameter("Frequency"));
		bind(marb.getParameter("Iterations"));
		bind(marb.getParameter("Strength"));
		bind(marb.getParameter("Seed"));
	}

	/**
	 * Get a new algorithm wrapping the given operation
	 *
	 * @param smudge
	 *            {@link Smudge}
	 * @param op
	 *            {@link Operation}
	 * @return {@link Algorithm}
	 */
	public Algorithm getOperationAlgorithm(Smudge smudge, Operation op) {
		Algorithm alg = new Algorithm();

		alg.getParameter("Enable").setValue(false);
		bind(alg.getParameter("Enable"));

		alg.add(op);

		smudge.add(alg);

		return alg;
	}

	/**
	 *
	 */
	public PizzaBoxShow() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

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
		new PizzaBoxShow();
	}

}
