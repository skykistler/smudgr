package io.smudgr.api.commands;

import java.util.ArrayList;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.app.threads.AppThread;
import io.smudgr.engine.param.Parameter;
import io.smudgr.engine.param.ParameterObserver;

/**
 * Set the current value of a specified parameter.
 * Takes id and value as arguments.
 *
 * Used for both updating the back-end value from the UI, and updating the UI
 * value from the back-end.
 *
 * @see ParameterObserver
 */
public class ParameterSet implements ApiCommand, ParameterObserver {

	@Override
	public String getCommand() {
		return "parameter.set";
	}

	// Send out the batch every 10 seconds
	private static final int PARAMETER_UPDATE_RATE = 10;

	private ArrayList<Parameter> nextBatch;
	private ArrayList<Parameter> currentBatch;

	/**
	 * Attach this command as an observer of all parameter changes.
	 */
	@Override
	public void onInit() {
		getProject().getParameterObserverNotifier().attach(this);

		nextBatch = new ArrayList<Parameter>();
		currentBatch = new ArrayList<Parameter>();

		ParameterBatchThread batchUpdater = new ParameterBatchThread();
		batchUpdater.start();
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		Parameter param = (Parameter) getProject().getItem((int) data.getNumber(PropertyMap.PROJECT_ID_ATTR));
		param.setValue(data.get("value"), this);

		return null;
	}

	@Override
	public void parameterUpdated(Parameter param) {
		synchronized (ParameterSet.class) {
			if (!nextBatch.contains(param))
				nextBatch.add(param);
		}
	}

	class ParameterBatchThread extends AppThread {

		/**
		 * Instantiate the {@link ParameterBatchThread}
		 */
		public ParameterBatchThread() {
			super("Parameter Batch Updater");
			setTarget(PARAMETER_UPDATE_RATE);
		}

		@Override
		protected void execute() {
			if (nextBatch.size() == 0)
				return;

			// Thread-safe batch swap
			synchronized (ParameterSet.class) {
				ArrayList<Parameter> swap = nextBatch;

				currentBatch.clear();
				nextBatch = currentBatch;

				currentBatch = swap;
			}

			// Serialize every parameter
			// TODO: reuse property maps for some mem improvement
			PropertyMap batch = new PropertyMap("batch");
			for (Parameter param : currentBatch) {
				batch.add(new PropertyMap(param));
			}

			// Send the batch packet
			ApiMessage batchPacket = ApiMessage.normalize(batch);
			sendMessage(ApiMessage.ok(getCommand() + ".batch", batchPacket));
		}

		@Override
		protected void printStatus() {
		}

	}

}
