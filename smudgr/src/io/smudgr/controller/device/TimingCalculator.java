package io.smudgr.controller.device;

import io.smudgr.controller.Controller;

public class TimingCalculator {
	private final long nsInMinute = 60l * 1000000000l;

	private Controller controller;
	private long lastTick = 0;

	private double avgBPM = 0;
	private int ticks;

	public TimingCalculator(Controller c) {
		controller = c;
	}

	public void tick() {
		long now = System.nanoTime();
		long unit = now - lastTick;

		long unitsInMinute = nsInMinute / unit;

		int beatsInMinute = (int) (unitsInMinute / 24.0);

		if (beatsInMinute > 0 && beatsInMinute <= 300) {
			ticks++;
			avgBPM += beatsInMinute;
		}
		if (ticks > 32) {
			avgBPM /= ticks;

			System.out.println("Avg BPM: " + (int) avgBPM);

			controller.setBPM((int) avgBPM);

			avgBPM = 0;
			ticks = 0;
		}

		lastTick = now;
	}

}
