package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.Project;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.SmudgeComponent;

/**
 * Remove a {@link SmudgeComponent} from its parent {@link Smudge} and the
 * current {@link Project}.
 */
public class SmudgeComponentRemove implements ApiCommand {

	@Override
	public String getCommand() {
		return "smudge.component.remove";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		int id = (int) data.getNumber("id");

		SmudgeComponent component = (SmudgeComponent) getProject().getItem(id);
		boolean success = component.getParent().remove(component);

		data.put("smudge", getProject().getId(component.getParent()));

		if (success)
			return ApiMessage.success(getCommand(), data);
		else
			return ApiMessage.failed(getCommand(), data);
	}

}
