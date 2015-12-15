package me.skykistler.smudgr.controller.controls;

import me.skykistler.smudgr.Smudge;

public class SaveControl extends Controllable {

	private Smudge smudge;

	public SaveControl(Smudge smudge) {
		super("Save Frame");
		this.smudge = smudge;
	}

	public void midiValue(int value) {
	}

	public void noteOn(int note) {
		smudge.save();
	}

	public void noteOff(int note) {
	}

	public void increment() {
	}

	public void decrement() {
	}

}
