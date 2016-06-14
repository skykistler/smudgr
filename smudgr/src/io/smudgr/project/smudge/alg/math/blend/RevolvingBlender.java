package io.smudgr.project.smudge.alg.math.blend;

public class RevolvingBlender implements Blender {

	int currentState = 0;

	public int blend(int colorA, int colorB) {
		if (currentState == 1) {
			currentState = 0;
			return colorA;
		} else {
			currentState = 1;
			return colorB;
		}
	}

	public String getName() {
		return "Revolving Blender";
	}

}