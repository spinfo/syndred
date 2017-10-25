package syndred.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

import syndred.entities.RawDraftContentState;

public class Interceptor extends ChannelInterceptorAdapter {

	@Autowired
	private SimpMessagingTemplate clientOut;

	private Map<String, String> mapping = new HashMap<String, String>();

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);

		if (sha.getCommand() != null) {
			String instance;
			String session = sha.getSessionId();

			switch (sha.getCommand()) {
			case CONNECT:
				instance = sha.getFirstNativeHeader("instance");
				Threading.callback(instance, callback(instance));

				if (!mapping.containsValue(instance)) {
					try {
						Threading.connect(instance);
					} catch (InterruptedException e) {
						break;
					}
				}

				mapping.put(session, instance);
				break;
			case DISCONNECT:
				instance = mapping.get(session);
				mapping.remove(session);

				if (!mapping.containsValue(instance)) {
					Threading.disconnect(instance);
				}

				break;
			default:
				break;
			}
		}
	}

	private Function<RawDraftContentState, Exception> callback(String instance) {
		return new Function<RawDraftContentState, Exception>() {
			@Override
			public Exception apply(RawDraftContentState state) {
				try {
					clientOut.convertAndSend("/" + instance + "/editor/pull", state);
				} catch (Exception exception) {
					return exception;
				}
				return null;
			}
		};
	}

}
