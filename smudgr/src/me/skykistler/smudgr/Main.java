package me.skykistler.smudgr;

import me.skykistler.smudgr.alg.CubicMarbeler;
import me.skykistler.smudgr.alg.PixelSort;
import me.skykistler.smudgr.alg.PixelSort.SortType;
import me.skykistler.smudgr.view.View;

public class Main {

	public static void main(String[] args) {
		View view = new View();

		Smudge smudge = new Smudge(view, "galaxies.jpg");
		smudge.addAlgorithm(new CubicMarbeler());
		smudge.addAlgorithm(new PixelSort(SortType.LUMA));

		view.setSmudge(smudge);
		view.init();
	}

}
