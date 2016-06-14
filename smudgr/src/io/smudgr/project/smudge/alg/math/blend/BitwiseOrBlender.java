package io.smudgr.project.smudge.alg.math.blend;

public class BitwiseOrBlender implements Blender {

	public int blend(int colorA, int colorB) {
		return colorA | colorB;
	}

	public String getName() {
		return "OR";
	}

}
