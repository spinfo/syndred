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

public class RbnfTask extends Task {

	private Thread ebnf;

	private Shared shared;

	public RbnfTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) throws ExecutionException {
		super(input, output, parser);

		ebnf = new Thread(() -> {
			while (!Thread.interrupted())
				Ebnf.root = Ebnf.syntaxDrivenParse();
		});

		shared = new Shared();
		shared.setGrammar(parser.getGrammar().chars().mapToObj(i -> (char) i).collect(Collectors.toList()));

		try {
			Ebnf.init(shared);
			ebnf.start();
		} catch (Throwable thrown) {
			throw new ExecutionException(thrown);
		}
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
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
		shared.backTrack = position < prev.size();
		sharedText.setParsePos(position);
		sharedText.setRichChars(next);

		if (position < next.size())
			DraftState.del(state, "Error");

		try {
			while (Shared.maxPosInParse < 0 && (shared.backTrack || sharedText.getParsePos() < sharedText.getTextLen()))
				Thread.sleep(100);
		} catch (InterruptedException error) {
			throw new ParseException(error.getMessage(), Shared.maxPosInParse);
		}

		if (Shared.maxPosInParse >= 0)
			DraftState.add(state, "error", Shared.maxPosInParse, next.size() - Shared.maxPosInParse);

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
