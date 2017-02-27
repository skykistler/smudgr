package io.smudgr.app;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

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
import io.smudgr.extensions.cef.view.CefView;
import io.smudgr.extensions.cef.view.WebsocketView;

/**
 * Garbage test class that bootstraps a bundled CEF smudgr build
 */
public class smudgr extends AppStart {

	/**
	 * Start the application
	 *
	 * @param args
	 *            {@code String[]}
	 */
	public static void main(String[] args) {
		boolean debug = true;
		try {
			if (debug) {
				FileOutputStream err = new FileOutputStream(Controller.getInstance().getAppPath() + "/smudgr_errors.log");
				System.setErr(new PrintStream(err));

				FileOutputStream out = new FileOutputStream(Controller.getInstance().getAppPath() + "/smudgr.log");
				System.setOut(new PrintStream(out));
			} else {
				PrintStream nullStream = new PrintStream(new OutputStream() {
					@Override
					public void write(int b) {
					}
				});
				System.setErr(nullStream);
				System.setOut(nullStream);
			}

			new smudgr(debug);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private smudgr(boolean debug) {
		super("", "data", "", "Arturia BeatStep Pro", true, false);

		Controller.getInstance().add(new WebsocketView());
		Controller.getInstance().add(new CefView(debug));

		start();
	}

	@Override
	public void buildRack() {
		Rack rack = Controller.getInstance().getProject().getRack();

		// addStraightPixelShift(rack, true);
		// addStraightPixelShift(rack, false);

		addSpectralShift(rack);

		addConvergePixelShift(rack);

		addDatabend(rack);

		addStraightPixelSort(rack);

		// addChannelBleed(rack);

		addMarbeler(rack);

		addStraightPixelSort(rack);

		addAutomator("auto-downsampler", null);
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
	}

	/**
	 *
	 * @param rack
	 *            Rack
	 */
	public void addMarbeler(Rack rack) {
		Marbeler marb = new Marbeler();
		getOperationAlgorithm(rack, marb);
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

}
