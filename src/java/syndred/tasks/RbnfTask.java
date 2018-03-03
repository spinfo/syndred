package syndred.tasks;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import CP.Ebnf.Ebnf;
import syndred.entities.Editor;
import syndred.entities.Parser;
import syndred.logic.DraftState;
import syndred.logic.Instance;
import texts.RichChar;
import texts.Shared;
import texts.Texts;
import tree.TreeNode;

public class RbnfTask extends Task {

	private Thread ebnf;

	private TreeNode root;

	private Shared shared;

	public RbnfTask(Instance instance) throws ExecutionException {
		super(instance);

		String grammar = instance.value(Parser.class).getGrammar();
		List<Character> list = grammar.chars().mapToObj(i -> (char) i).collect(Collectors.toList());
		UncaughtExceptionHandler threadHandler = new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thrower, Throwable thrown) {
				Parser parser = instance.value(Parser.class);
				parser.setError(thrown.getClass().getName() + ": " + thrown.getMessage());
				parser.setRunning(false);
				instance.pipe(parser);
				thread.interrupt();
			}
		};

		ebnf = new Thread(() -> {
			shared = new Shared();
			shared.getSharedText().setGrammar(list);

			try {
				initialized = Ebnf.init(shared);

				while (!ebnf.isInterrupted()) {
					root = Ebnf.syntaxDrivenParse();

					System.out.println("========== SUCCESS(" + Thread.currentThread().getId() + ")");

					while (initialized)
						Thread.sleep(100);
				}
			} catch (Throwable thrown) {
				threadHandler.uncaughtException(ebnf, thrown);
			}
		});

		try {
			ebnf.setUncaughtExceptionHandler(threadHandler);
			ebnf.start();
		} catch (Throwable thrown) {
			throw new ExecutionException(thrown);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void close() throws Exception {
		System.out.println("!!!!!!!!!! CLOSE CALLED(" + Thread.currentThread().getId() + ")");
		while (ebnf.isAlive())
			ebnf.stop();
	}

	@Override
	protected Editor parse(Editor editor) throws Exception {
		int position = 0;
		Texts text = shared.getSharedText();
		List<RichChar> next = DraftState.getRichChars(editor);
		List<RichChar> prev = text.getRichChars();

		try {
			while (next.get(position).equals(prev.get(position)))
				position++;
		} catch (IndexOutOfBoundsException expected) {
		}

		initialized = false;
		text.setMaxPos(-1);
		text.setParsePos(/* position */ 0);
		text.setRichChars(next);

		try {
			while (!initialized && text.idle()) {
				System.out.println("========== parse(" + Thread.currentThread().getId() + "): sleep");
				Thread.sleep(500);
			}
		} catch (InterruptedException expected) {
			return editor;
		}

		if (text.getMaxPos() >= 0)
			DraftState.addRange(editor, "Error", text.getMaxPos(), next.size() - text.getMaxPos());
		else if (initialized)
			DraftState.addRange(editor, "Success", 0, next.size());

		editor.setParseTree("");
		return editor;
	}

}
