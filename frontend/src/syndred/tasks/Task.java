package syndred.tasks;

import java.util.ArrayList;
import java.util.List;

import syndred.entities.InlineStyleRange;
import texts.RichChar;

public abstract class Task implements Runnable {

	public List<RichChar> characters = new ArrayList<RichChar>();

	public List<InlineStyleRange> errors = new ArrayList<InlineStyleRange>();

	public int position = 0;

	protected final String gramma;

	public Task(String gramma) {
		this.gramma = gramma;
	}

	public final boolean completed() {
		return position == characters.size();
	}

	@Override
	public abstract void run();

}
