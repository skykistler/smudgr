package io.smudgr.api.commands;

import io.smudgr.api.ApiCommand;
import io.smudgr.api.ApiMessage;
import io.smudgr.engine.Rack;
import io.smudgr.engine.Smudge;

/**
 * Remove the {@link Smudge} with the given ID from the current {@link Rack}
 */
public class RackSmudgeRemove implements ApiCommand {

	@Override
	public String getCommand() {
		return "rack.smudge.remove";
	}

	@Override
	public ApiMessage execute(ApiMessage data) {
		int id = (int) data.getNumber("id");

		Smudge smudge = (Smudge) getProject().getItem(id);
		boolean success = getProject().getRack().remove(smudge);

		data.put("rack", getProject().getId(getProject().getRack()));

		if (success)
			return ApiMessage.success(getCommand(), data);
		else
			return ApiMessage.failed(getCommand(), data);
	}

}
