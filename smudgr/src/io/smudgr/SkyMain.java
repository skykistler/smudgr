package io.smudgr;

import io.smudgr.alg.PixelSort;

public class SkyMain {

	public static void main(String[] args) {
		Smudge smudge = new Smudge("test", "galaxies.jpg");
		smudge.downsample(2);

		PixelSort pixelSort = new PixelSort();
		smudge.addAlgorithm(pixelSort);

		Smudgr.startSmudge(smudge);
	}

}
