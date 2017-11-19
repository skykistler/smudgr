package io.smudgr.engine.alg.op;

import io.smudgr.engine.alg.Algorithm;
import io.smudgr.engine.alg.AlgorithmComponent;
import io.smudgr.util.PixelFrame;

/**
 * The {@link Operation} abstract class is an {@link AlgorithmComponent} that
 * defines behavior for executing a pixel manipulation algorithm. Pixels are
 * passed as a {@link PixelFrame} object that is operated on directly.
 */
public abstract class Operation extends AlgorithmComponent {

	@Override
	public String getComponentTypeName() {
		return "Operation";
	}

	@Override
	public String getComponentTypeIdentifier() {
		return "operation";
	}

	/**
	 * Implementations of the {@link Operation#execute(PixelFrame)} method create a
	 * visual manipulation using the given {@link PixelFrame}. Implementors are
	 * currently responsible for only acting on the parent {@link Algorithm} set
	 * of selected pixels.
	 * <p>
	 * While copies of the given {@link PixelFrame} may be created for computational
	 * purposes, the resulting frame should be stored in the {@link PixelFrame}
	 * instance passed into this method.
	 *
	 * @param img
	 *            {@link PixelFrame}
	 * @see Algorithm#getSelectedPixels()
	 */
	public abstract void execute(PixelFrame img);

}
