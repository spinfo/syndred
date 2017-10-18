package syndred.controllers;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

@Controller
public class WebsocketController {

	@SubscribeMapping("/{instance}/editor")
	public RawDraftContentState editorInit(@DestinationVariable String instance) {
		return new RawDraftContentState();
	}
	
	@MessageMapping("/{instance}/editor")
	@SendTo("/syndred/{instance}/editor")
	public RawDraftContentState editor(@DestinationVariable String instance, RawDraftContentState contentState) {
		contentState.getBlocks().stream().forEach(i -> System.out.println(i.getText()));
		return contentState;
	}

	@SubscribeMapping("/{instance}/parser")
	public Parser parserInit(@DestinationVariable String instance) {
		return new Parser();
	}

	@MessageMapping("/{instance}/parser")
	@SendTo("/syndred/{instance}/parser")
	public Parser parser(@DestinationVariable String instance, Parser parser) {		
		switch (parser.getName()) {
		case "regex":
			parser.setError(true);
			break;

		case "ebnf":
			parser.setError(true);
			break;

		case "abnf":
			parser.setError(true);
			break;

		default:
			parser.setError(true);
		}

		return parser;
	}

}
