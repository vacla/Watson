package eu.minemania.watson.scheduler.tasks;

import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEdit;

public class AddBlockEditTask implements Runnable {
	protected BlockEdit _edit;
	protected boolean   _updateVariables;

	public AddBlockEditTask(BlockEdit edit, boolean updateVariables) {
		_edit = edit;
		_updateVariables = updateVariables;
	}

	@Override
	public void run() {
		DataManager.getEditSelection().getBlockEditSet().addBlockEdit(_edit, _updateVariables);
	}

}
