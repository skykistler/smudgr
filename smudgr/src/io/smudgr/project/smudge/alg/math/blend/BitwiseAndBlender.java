package io.smudgr.project.smudge.alg.math.blend;

public class BitwiseAndBlender implements Blender {

	public String getName() {
		return "AND";
	}

	public int blend(int colorA, int colorB) {
		return colorA & colorB;
	}

}
