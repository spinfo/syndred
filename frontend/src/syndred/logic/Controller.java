package syndred.logic;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

@org.springframework.stereotype.Controller
public class Controller {

	@SubscribeMapping("/{instance}/editor/pull")
	public RawDraftContentState editorPull(@DestinationVariable String instance) {
		return Threading.pull(instance);
	}

	@MessageMapping("/{instance}/editor/push")
	public void editorPush(@DestinationVariable String instance, RawDraftContentState state)
			throws InterruptedException {
		Threading.push(instance, state);
	}

	@SubscribeMapping("/{instance}/parser/pull")
	public Parser parserInit(@DestinationVariable String instance) {
		return Threading.parser(instance);
	}

	@MessageMapping("/{instance}/parser/push")
	@SendTo("/syndred/{instance}/parser/pull")
	public Parser parserPush(@DestinationVariable String instance, Parser parser) {
		return Threading.run(instance, parser);
	}

}
