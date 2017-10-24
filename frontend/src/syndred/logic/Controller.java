package syndred.logic;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

@org.springframework.stereotype.Controller
public class Controller {

	@SubscribeMapping("/{instance}/editor")
	public RawDraftContentState editorInit(@DestinationVariable String instance) {
		return Threading.getState(instance);
	}

	@MessageMapping("/{instance}/editor")
	@SendTo("/syndred/{instance}/editor")
	public RawDraftContentState editor(@DestinationVariable String instance, RawDraftContentState state) {
		Threading.setState(instance, state);
		return Threading.getState(instance);
	}

	@SubscribeMapping("/{instance}/parser")
	public Parser parserInit(@DestinationVariable String instance) {
		return Threading.getParser(instance);
	}

	@MessageMapping("/{instance}/parser")
	@SendTo("/syndred/{instance}/parser")
	public Parser parser(@DestinationVariable String instance, Parser parser) {
		switch (parser.getName()) {
		case "regex":
			parser.setError("");
			parser.setRunning(true);
			parser.setName("ebnf");
			break;

		case "ebnf":
			parser.setError("Not implemented");
			parser.setRunning(false);
			break;

		case "abnf":
			parser.setError("Not implemented");
			parser.setRunning(false);
			break;

		default:
			parser.setError("true");
			parser.setRunning(false);
		}

		Threading.setParser(instance, parser);
		return Threading.getParser(instance);
	}

}
