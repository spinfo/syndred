package syndred.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import syndred.entities.Block;
import syndred.entities.Editor;
import syndred.entities.Range;
import texts.RichChar;

public class DraftState {

	public static void addRange(Editor editor, String style, int offset, int length) {
		int blockLen = 0;
		int position = 0;
		Range range;
		List<Range> ranges;

		for (Block block : editor.getBlocks()) {
			blockLen = block.getText().length();
			range = new Range();
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

	public static void deleteRange(Editor editor, String style) {
		for (Block block : editor.getBlocks())
			block.setInlineStyleRanges(block.getInlineStyleRanges().stream().filter(i -> !i.getStyle().equals(style))
					.collect(Collectors.toList()));
	}

	public static List<RichChar> getRichChars(Editor editor) {
		List<RichChar> chars = new LinkedList<RichChar>();

		for (Block block : editor.getBlocks()) {
			String text = block.getText();

			for (int i = 0; i < text.length(); i++) {
				RichChar rch = new RichChar(text.charAt(i));

				for (Range range : block.getInlineStyleRanges()) {
					if (range.getOffset() > i || range.getOffset() + range.getLength() <= i)
						continue;

					char[] style = range.getStyle().toCharArray();

					if (!range.getStyle().matches("\\d+"))
						switch (range.getStyle()) {
						case "Red":
						case "Yellow":
						case "Green":
						case "Blue":
							rch.color = style;
							break;

						case "Italic":
						case "Underline":
							rch.style = style;
							break;

						case "Arial":
						case "Courier":
						case "Times":
							rch.typeface = style;
							break;

						case "Bold":
							rch.weight = style;
							break;
						}
					else
						rch.size = style;
				}

				chars.add(rch);
			}
		}

		return chars;
	}

	public static String getString(Editor editor) {
		List<Block> blocks = editor.getBlocks();
		return blocks == null ? "" : blocks.stream().map(i -> i.getText()).collect(Collectors.joining());
	}

}
