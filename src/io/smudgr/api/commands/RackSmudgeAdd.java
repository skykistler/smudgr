package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.app.project.Project;
import io.smudgr.engine.Rack;
import io.smudgr.engine.Smudge;

/**
 * Add a {@link Smudge} to a {@link Rack} in the current
 * {@link Project}.
 */
public class RackSmudgeAdd implements ApiCommand {

	@Override
	public String getCommand() {
		return "rack.smudge.add";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		// Smudge smudge = (Smudge) getProject().getItem((int)
		// data.getNumber("id"));
		//
		// SmudgeComponentLibrary<SmudgeComponent> library =
		// getProject().getSmudgeComponentLibrary();
		// SmudgeComponent component =
		// library.getNewInstance(smudge.getTypeIdentifier(),
		// data.get("component"));
		//
		// smudge.add(component);

		// return ApiMessage.success("smudge.get", ApiMessage.normalize(new
		// PropertyMap(smudge)));

		return ApiMessage.ok("rack.smudge.add", data);
	}

}
