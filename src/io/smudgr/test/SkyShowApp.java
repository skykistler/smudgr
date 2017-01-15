package io.smudgr.test;

import io.smudgr.app.AppStart;
import io.smudgr.app.controller.Controller;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.coord.ColumnCoords;
import io.smudgr.engine.alg.coord.RowCoords;
import io.smudgr.engine.alg.op.ChannelBleed;
import io.smudgr.engine.alg.op.DataBend;
import io.smudgr.engine.alg.op.PixelShift;
import io.smudgr.engine.alg.op.PixelSort;
import io.smudgr.engine.alg.op.SpectralShift;
import io.smudgr.engine.alg.select.RangeSelect;
import io.smudgr.extensions.automate.controls.AutomatorControl;
import io.smudgr.extensions.cef.view.WebsocketView;

public class SkyShowApp extends AppStart {

	static String projectPath = "data/show.smudge";

	static boolean overwriteSmudge = false;

	static String sourcePath = "data/testing";

	static String outputPath = "data";

	static String device = "Arturia BeatStep Pro";
	static boolean deviceServer = false;

	static int fullscreenDisplay = -1;
	static boolean monitor = false;

	public void buildSmudge() {
		Smudge smudge = Controller.getInstance().getProject().getSmudge();

		Algorithm colum_sort_alg = new Algorithm();
		RangeSelect pixelsort_range = new RangeSelect();
		colum_sort_alg.add(pixelsort_range);
		PixelSort column_sort = new PixelSort();
		colum_sort_alg.add(column_sort);
		colum_sort_alg.add(new ColumnCoords());
		pixelsort_range.getParameter("Range Length").setValue(0);
		bind(pixelsort_range.getParameter("Range Length"));
		bind(colum_sort_alg.getParameter("Enable"));
		colum_sort_alg.getParameter("Enable").setValue(false);
		smudge.add(colum_sort_alg);

		Algorithm databend_alg = new Algorithm();
		RangeSelect databend_range = new RangeSelect();
		databend_alg.add(databend_range);
		DataBend databend = new DataBend();
		databend.getParameter("Amount").setValue(2);
		bind(databend.getParameter("Amount"));
		databend_range.getParameter("Range Length").setValue(1);
		databend_alg.add(databend);
		AutomatorControl automator1 = addAutomator("Animate", databend.getParameter("Target"));
		bind(automator1);
		bind(databend_alg.getParameter("Enable"));
		databend_alg.getParameter("Enable").setValue(false);
		smudge.add(databend_alg);

		Algorithm spectral_alg = new Algorithm();
		SpectralShift spectral_shift = new SpectralShift();
		bind(spectral_shift.getParameter("Colors"));
		bind(spectral_shift.getParameter("Palette"));
		spectral_alg.add(spectral_shift);
		AutomatorControl automator4 = addAutomator("Beat Sync", spectral_shift.getParameter("Shift"));
		bind(automator4);
		bind(spectral_alg.getParameter("Enable"));
		spectral_alg.getParameter("Enable").setValue(false);
		smudge.add(spectral_alg);

		Algorithm shift_rows = new Algorithm();
		shift_rows.add(new RowCoords());
		PixelShift pixel_shift_rows = new PixelShift();
		bind(pixel_shift_rows.getParameter("Reverse"));
		bind(pixel_shift_rows.getParameter("Intervals"));
		AutomatorControl automator2 = addAutomator("Animate", pixel_shift_rows.getParameter("Amount"));
		bind(automator2);
		shift_rows.add(pixel_shift_rows);
		bind(shift_rows.getParameter("Enable"));
		shift_rows.getParameter("Enable").setValue(false);
		smudge.add(shift_rows);

		Algorithm shift_columns = new Algorithm();
		shift_columns.add(new ColumnCoords());
		PixelShift pixel_shift_columns = new PixelShift();
		bind(pixel_shift_columns.getParameter("Intervals"));
		bind(pixel_shift_columns.getParameter("Reverse"));
		shift_columns.add(pixel_shift_columns);
		AutomatorControl automator3 = addAutomator("Animate", pixel_shift_columns.getParameter("Amount"));
		bind(automator3);
		bind(shift_columns.getParameter("Enable"));
		shift_columns.getParameter("Enable").setValue(false);
		smudge.add(shift_columns);

		bind(smudge.getParameter("Source Speed"));
		bind(smudge.getParameter("Downsample"));

		bind(Controller.getInstance().getAppControl("Source Switcher"));
		bind(Controller.getInstance().getAppControl("Source Set Switcher"));
		bind(Controller.getInstance().getAppControl("Save Project"));
	}

	public SkyShowApp() {
		super(projectPath, sourcePath, outputPath, device, overwriteSmudge, deviceServer);

		fullscreenView(fullscreenDisplay);
		if (monitor)
			monitorView();

		Controller.getInstance().add(new WebsocketView());

		start();

		Smudge smudge = Controller.getInstance().getProject().getSmudge();
		Algorithm chan_bleed = new Algorithm();
		ChannelBleed bleed = new ChannelBleed();
		chan_bleed.add(bleed);
		bleed.getParameter("Shift Amount").setContinuous(true);
		chan_bleed.getParameter("Enable").setValue(false);
		addAutomator("Animate", bleed.getParameter("Shift Amount"));
		smudge.add(chan_bleed);
	}

	public static void main(String[] args) {
		new SkyShowApp();
	}

}
