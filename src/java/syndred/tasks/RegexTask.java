package syndred.tasks;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import syndred.entities.Editor;
import syndred.entities.Parser;
import syndred.logic.DraftState;
import syndred.logic.Instance;

public class RegexTask extends Task {

	Pattern pattern;

	public RegexTask(Instance instance) throws ExecutionException {
		super(instance);

		try {
			pattern = Pattern.compile(instance.value(Parser.class).getGrammar());
		} catch (Throwable thrown) {
			throw new ExecutionException(thrown);
		}

		initialized = true;
	}

	@Override
	protected Editor parse(Editor editor) throws Exception {
		Matcher matcher = pattern.matcher(DraftState.getString(editor));

		while (matcher.find())
			DraftState.addRange(editor, "Success", matcher.start(), matcher.end() - matcher.start());

		return editor;
	}

}
