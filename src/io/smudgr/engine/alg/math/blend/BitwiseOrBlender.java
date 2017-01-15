package io.smudgr.engine.alg.math.blend;

public class BitwiseOrBlender implements Blender {

	public String getName() {
		return "OR";
	}

	public int blend(int colorA, int colorB) {
		return colorA | colorB;
	}

}
