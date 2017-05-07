package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.Project;
import io.smudgr.engine.Smudge;
import io.smudgr.engine.SmudgeComponent;
import io.smudgr.engine.SmudgeComponentLibrary;

/**
 * Add a {@link SmudgeComponent} to a {@link Smudge} in the current
 * {@link Project}.
 */
public class SmudgeComponentAdd implements ApiCommand {

	@Override
	public String getCommand() {
		return "smudge.component.add";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		Smudge smudge = (Smudge) getProject().getItem((int) data.getNumber("smudge"));

		SmudgeComponentLibrary<SmudgeComponent> library = getProject().getSmudgeComponentLibrary();
		SmudgeComponent component = library.getNewInstance(smudge.getTypeIdentifier(), data.get("component"));

		smudge.add(component);

		data.put("smudge", getProject().getId(smudge));
		return ApiMessage.success(getCommand(), data);
	}

}
