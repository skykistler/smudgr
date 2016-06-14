package io.smudgr.project.smudge.alg.math.blend;

public class BitwiseAndBlender implements Blender {

	@Override
	public int blend(int colorA, int colorB) {
		return colorA & colorB;
	}

	public String getName() {
		return "AND";
	}

}
