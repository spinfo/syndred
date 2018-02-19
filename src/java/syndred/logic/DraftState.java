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
			range = new InlineStyleRange();
			ranges = block.getInlineStyleRanges();

			if (offset < position + blockLen) {
				range.setLength(Math.min(blockLen, length));
				range.setOffset(Math.max(0, offset - position));
				range.setStyle(style);

				ranges.add(range);
				block.setInlineStyleRanges(ranges);
				length -= range.getLength();
			}

			position += blockLen;
		}
	}

	public static void del(RawDraftContentState state, String style) {
		for (Block block : state.getBlocks())
			block.setInlineStyleRanges(block.getInlineStyleRanges().stream().filter(i -> !i.getStyle().equals(style))
					.collect(Collectors.toList()));
	}

}
