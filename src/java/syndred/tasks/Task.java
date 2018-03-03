package syndred.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import syndred.entities.Editor;
import syndred.entities.Parser;
import syndred.logic.DraftState;
import syndred.logic.Instance;

public abstract class Task implements AutoCloseable, Callable<Parser> {

	protected abstract Editor parse(Editor editor) throws Exception;

	protected final Instance instance;

	protected boolean initialized;

	protected Thread thread;

	public Task(Instance instance) throws ExecutionException {
		this.instance = instance;
	}

	@Override
	public Parser call() {
		Editor editor = instance.value(Editor.class);
		Parser parser = instance.value(Parser.class);
		thread = Thread.currentThread();

		try (Task task = this) {
			while (!initialized)
				Thread.sleep(100);

			while (!thread.isInterrupted()) {
				System.out.println("-----> TASK TAKE(" + Thread.currentThread().getId() + ")");
				editor = instance.input.take();
				DraftState.deleteRange(editor, "Error");
				DraftState.deleteRange(editor, "Success");
				instance.pipe(parse(editor));
			}
		} catch (InterruptedException expected) {
		} catch (Throwable thrown) {
			parser.setError(thrown.getMessage());
		}

		System.out.println("-----> TASK END(" + Thread.currentThread().getId() + ")");

		return parser;
	}

	@Override
	public void close() throws Exception {
	}

}
