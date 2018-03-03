package syndred.tasks;

import java.util.concurrent.ExecutionException;

import syndred.entities.Editor;
import syndred.logic.Instance;

public class EchoTask extends Task {

	public EchoTask(Instance instance) throws ExecutionException {
		super(instance);
		initialized = true;
	}

	@Override
	protected Editor parse(Editor editor) throws Exception {
		return editor;
	}

}
