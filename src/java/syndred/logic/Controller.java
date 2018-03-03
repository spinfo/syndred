package syndred.logic;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;

import syndred.entities.Editor;
import syndred.entities.Parser;

@org.springframework.stereotype.Controller
public class Controller {

	@SubscribeMapping("/{instance}/editor/get")
	public Editor getEditor(@DestinationVariable String instance) {
		return Threading.getEditor(instance);
	}

	@MessageMapping("/{instance}/editor/set")
	public void setEditor(@DestinationVariable String instance, Editor editor) {
		Threading.setEditor(instance, editor);
	}

	@SubscribeMapping("/{instance}/parser/get")
	public Parser getParser(@DestinationVariable String instance) {
		return Threading.getParser(instance);
	}

	@MessageMapping("/{instance}/parser/set")
	public void setParser(@DestinationVariable String instance, Parser parser) {
		Threading.setParser(instance, parser);
	}

}
