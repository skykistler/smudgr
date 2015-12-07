package me.skykistler.smudgr;

import me.skykistler.smudgr.alg.CubicMarbeler;
import me.skykistler.smudgr.alg.PixelSort;
import me.skykistler.smudgr.alg.PixelSort.SortType;
import me.skykistler.smudgr.view.View;

public class Main {

	public static void main(String[] args) {
		View view = new View();

		Smudge smudge = new Smudge(view, "house1.png");
		smudge.addAlgorithm(new PixelSort(SortType.LUMA));
		smudge.addAlgorithm(new CubicMarbeler());

		view.setSmudge(smudge);
		view.init();
	}

}
