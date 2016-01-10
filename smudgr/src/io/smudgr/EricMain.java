package io.smudgr;

public class EricMain {
	public static void main(String[] args) {

		Smudge smudge = new Smudge("smudgr test", "/teststop/forest.jpg");
		// smudge.downsample(2);

		// SaveControl save = new SaveControl(smudge);

		// ImageSwitcherControl imageSwitcher = new ImageSwitcherControl(smudge, "test");

		// DownsampleControl downsampler = new DownsampleControl(smudge);

		// CubicMarbeler marbeler = new CubicMarbeler();
		// marbeler.bind("Frequency");
		// marbeler.bind("Iterations");
		// marbeler.bind("Strength");
		// marbeler.bind("Seed");
		// marbeler.bind("Offset - X/Y");
		//
		// smudge.addAlgorithm(marbeler);
		//
		// LumaSort lumaSort = new LumaSort();
		// lumaSort.bind("Luma Threshold X");
		// lumaSort.getParameter("Luma Threshold X").setReverse(true);
		// lumaSort.bind("Luma Threshold Y");
		// lumaSort.getParameter("Luma Threshold Y").setReverse(true);
		// lumaSort.bind("Reverse Sort Columns");
		// lumaSort.bind("Reverse Sort Rows");
		// lumaSort.bind("Starting Row Bound");
		// lumaSort.bind("Starting Column Bound");
		// lumaSort.bind("Ending Column Bound");
		// lumaSort.bind("Ending Row Bound");
		// lumaSort.bind("Degree of Rotation");
		//
		// smudge.addAlgorithm(lumaSort);
		//
		// Shift shifter = new Shift();
		// shifter.getParameter("Shift Rows").setValue(false);
		// //shifter.bind("Pixel Shift X");
		// shifter.bind("Pixel Shift Y");
		// shifter.bind("x0");
		// shifter.bind("y0");
		// shifter.bind("x1");
		// shifter.bind("y1");
		// shifter.bind("Amount of X intervals");
		// //shifter.bind("Amount of Y intervals");
		// shifter.bind("Scale for Y");
		// shifter.bind("Direction for Y");
		//
		// smudge.addAlgorithm(shifter);

		// RadialPixelSort radialSort = new RadialPixelSort();
		// radialSort.bind("Luma Threshold");
		// radialSort.getParameter("Luma Threshold").setReverse(true);
		// radialSort.bind("Reverse Sort");
		// radialSort.bind("Starting Row Bound");
		// radialSort.bind("Starting Column Bound");
		// radialSort.bind("Ending Column Bound");
		// radialSort.bind("Ending Row Bound");
		// radialSort.bind("Inner Radius");
		//
		// smudge.addAlgorithm(radialSort);

		// Controller.startSmudge(smudge, "Arturia BeatStep");
	}
}
