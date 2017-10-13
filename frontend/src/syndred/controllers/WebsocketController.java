package syndred.controllers;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

@Controller
public class WebsocketController {

	@MessageMapping("/editor/{instance}")
	// @SendTo("/editor/{instance}")
	public RawDraftContentState editor(@DestinationVariable String instance, RawDraftContentState contentState) {

		System.out.println(contentState);

		return contentState;
	}

	@MessageMapping("/parser/{instance}")
	// @SendTo("/parser/{instance}")
	public Boolean parser(@DestinationVariable String instance, Parser parser) {

		System.out.println(instance);

		switch (parser.getName()) {
		case "regex":
			return true;

		case "ebnf":
			return true;

		case "abnf":
			return true;

		default:
			return false;
		}
	}

}
