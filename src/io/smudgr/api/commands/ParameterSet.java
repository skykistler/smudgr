package io.smudgr.api.commands;

import java.util.ArrayList;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.util.PropertyMap;
import io.smudgr.app.threads.AppThread;
import io.smudgr.engine.param.BooleanParameter;
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

	// Set the batch updates per second
	private static final int PARAMETER_UPDATE_RATE = 8;

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
		param.setValue(data.get("value"));

		// Why not
		if (param instanceof BooleanParameter)
			System.gc();

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

		private PropertyMap batch = new PropertyMap("batch");
		private String parameterTypeId;

		private ArrayList<PropertyMap> toRemove = new ArrayList<PropertyMap>();

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

			// Store the parameter type id for map mgmt
			if (currentBatch.size() > 0)
				parameterTypeId = currentBatch.get(0).getTypeCategoryIdentifier();

			// Serialize parameters to property maps
			for (Parameter param : currentBatch) {
				PropertyMap existing = null;

				for (PropertyMap map : batch.getChildren(parameterTypeId)) {
					// If the ID matches, use that existing map
					if (mapIsParam(map, param)) {
						existing = map;
						break;
					}
				}

				if (existing == null) {
					batch.add(new PropertyMap(param));
				} else {
					param.save(existing);
				}
			}

			// Remove any children that weren't updated
			toRemove.clear();
			for (PropertyMap map : batch.getChildren(parameterTypeId)) {
				boolean contains = false;

				for (Parameter param : currentBatch) {
					if (mapIsParam(map, param)) {
						contains = true;
						break;
					}
				}

				if (!contains)
					toRemove.add(map);
			}

			for (PropertyMap remove : toRemove)
				batch.removeChild(remove);

			// Send the batch packet
			ApiMessage batchPacket = ApiMessage.normalize(batch);
			sendMessage(ApiMessage.ok(getCommand() + ".batch", batchPacket));
		}

		@Override
		protected void printStatus() {
		}

		private boolean mapIsParam(PropertyMap map, Parameter param) {
			return map.getAttribute(PropertyMap.PROJECT_ID_ATTR).equals(getProject().getId(param) + "");
		}

	}

}
