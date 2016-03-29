package io.smudgr.midi.messages;

import io.smudgr.controller.controls.Controllable;

public class TimingClockMessage implements MidiMessageStrategy {
	private final long nsInMinute = 60l * 1000000000l;

	private long lastTick = 0;

	private double avgBPM = 0;
	private int ticks;

	public void input(Controllable control, int value) {
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

			// Super secret highly advanced delay compensation technique
			avgBPM++;

			System.out.println("Avg BPM: " + (int) avgBPM);

			control.inputValue((int) avgBPM);

			avgBPM = 0;
			ticks = 0;
		}

		lastTick = now;
	}

}
