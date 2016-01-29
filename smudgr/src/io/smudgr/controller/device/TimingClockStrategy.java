package io.smudgr.controller.device;

import io.smudgr.controller.Controller;
import io.smudgr.controller.controls.Controllable;

public class TimingClockStrategy implements MidiControlStrategy {
	private final long nsInMinute = 60l * 1000000000l;

	private Controller controller;
	private int tickCount = 0;
	private long lastTick = 0;

	private int lastBpm = 120;

	public TimingClockStrategy(Controller c) {
		controller = c;
	}

	public void input(Controllable c, int value) {
		if (tickCount < 8) {
			tickCount++;
			return;
		}

		long now = System.nanoTime();
		long unit = now - lastTick;

		long unitsInMinute = nsInMinute / unit;

		int beatsInMinute = (int) (6.0 * unitsInMinute / 16);

		controller.setBPM(beatsInMinute);

		System.out.println(controller.getBPM() + " bpm");

		lastTick = now;
		tickCount = 0;
	}

}
