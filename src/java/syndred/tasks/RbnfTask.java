package syndred.tasks;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import CP.Ebnf.Ebnf;
import syndred.entities.Block;
import syndred.entities.InlineStyleRange;
import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;
import syndred.logic.DraftState;
import texts.RichChar;
import texts.Shared;
import texts.Texts;
import tree.Node;

public class RbnfTask extends Task {

	private Thread ebnf;

	private Shared shared;

	private boolean success;

	public RbnfTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) throws ExecutionException {
		super(input, output, parser);

		ebnf = new Thread(() -> {
			while (!Thread.interrupted()) {
				try {
					Ebnf.root = Ebnf.syntaxDrivenParse();
					success = true;

					while (success)
						Thread.sleep(100);
				} catch (Throwable thrown) {
				}
			}
		});

		shared = new Shared();
		shared.setGrammar(parser.getGrammar().chars().mapToObj(i -> (char) i).collect(Collectors.toList()));
		shared.setRegex("\\u00FC:\n\\u00FD:".chars().mapToObj(i -> (char) i).collect(Collectors.toList()));

		try {
			Ebnf.init(shared);
			ebnf.start();
		} catch (Throwable thrown) {
			throw new ExecutionException(thrown);
		}
	}

	@Override
	public void close() {
		while (!ebnf.isInterrupted() || ebnf.isAlive())
			ebnf.interrupt();
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		DraftState.del(state, "Error");
		DraftState.del(state, "Success");

		Texts sharedText = shared.getSharedText();
		List<RichChar> next = getRichChars(state);
		List<RichChar> prev = sharedText.getRichChars();
		int position = 0;

		try {
			while (next.get(position).equals(prev.get(position)))
				position++;
		} catch (IndexOutOfBoundsException e) {
		}

		Shared.maxPosInParse = -1;
		sharedText.setParsePos(0);
		sharedText.setRichChars(next);
		success = false;

		try {
			while (!success && Shared.maxPosInParse < 0 && sharedText.getParsePos() < sharedText.getTextLen())
				Thread.sleep(100);
		} catch (InterruptedException e) {
			return state;
		}

		if (Shared.maxPosInParse >= 0)
			DraftState.add(state, "Error", Shared.maxPosInParse, next.size() - Shared.maxPosInParse);
		else if (success)
			DraftState.add(state, "Success", 0, next.size());

		state.setParseTree(Node.resultString);
		return state;
	}

	private List<RichChar> getRichChars(RawDraftContentState state) {
		List<RichChar> chars = new LinkedList<RichChar>();

		for (Block block : state.getBlocks()) {
			String text = block.getText();

			for (int i = 0; i < text.length(); i++) {
				RichChar rch = new RichChar(text.charAt(i));

				for (InlineStyleRange range : block.getInlineStyleRanges()) {
					if (range.getOffset() > i || range.getOffset() + range.getLength() <= i)
						continue;

					char[] style = range.getStyle().toCharArray();

					switch (range.getStyle()) {
					case "Bold":
						rch.weight = style;
						break;

					case "Italic":
					case "Underline":
						rch.style = style;
						break;
					}
				}

				chars.add(rch);
			}
		}

		return chars;
	}
}
