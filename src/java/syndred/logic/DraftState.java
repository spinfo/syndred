package syndred.logic;

import java.util.List;
import java.util.stream.Collectors;

import syndred.entities.Block;
import syndred.entities.InlineStyleRange;
import syndred.entities.RawDraftContentState;

public class DraftState {

	public static void add(RawDraftContentState state, String style, int offset, int length) {
		int blockLen = 0;
		int position = 0;
		InlineStyleRange range;
		List<InlineStyleRange> ranges;

		for (Block block : state.getBlocks()) {
			if (position > offset + length)
				break;

			blockLen = block.getText().length();
			
			if (offset < position + blockLen) {
				range = new InlineStyleRange();
				range.setLength(Math.min(length + offset - position, blockLen));
				range.setOffset(Math.max(offset - position, 0));
				range.setStyle(style);

				ranges = block.getInlineStyleRanges();
				ranges.add(range);
				block.setInlineStyleRanges(ranges);
			}

			position += blockLen;
		}
	}

	public static void del(RawDraftContentState state, String style) {
		for (Block block : state.getBlocks())
			block.setInlineStyleRanges(block.getInlineStyleRanges().stream().filter(i -> !i.getStyle().equals(style))
					.collect(Collectors.toList()));
	}

	public static void set(RawDraftContentState state, String style, int offset, int length) {
		del(state, style);
		add(state, style, offset, length);
	}

}
