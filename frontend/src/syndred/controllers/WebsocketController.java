package syndred.controllers;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

@Controller
public class WebsocketController {

	@MessageMapping("/editor/{instance}")
	@SendTo("/editor/{instance}")
	public RawDraftContentState editor(@DestinationVariable String instance, RawDraftContentState contentState) {

		System.out.println(contentState);

		return contentState;
	}

	@MessageMapping("/parser/{instance}")
	@SendTo("/parser/{instance}")
	public Parser parser(@DestinationVariable String instance, Parser parser) {

		System.out.println(instance);
		System.out.println(parser.getName());

		parser.setError(true);
		
		switch (parser.getName()) {
		case "regex":
			parser.setError(true);

		case "ebnf":
			parser.setError(false);

		case "abnf":
			parser.setError(true);

		default:
			parser.setError(true);
		}

		return parser;
	}

}
