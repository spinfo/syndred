package syndred.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

import syndred.entities.RawDraftContentState;

public class Interceptor extends ChannelInterceptorAdapter {

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
			case SUBSCRIBE:
				instance = mapping.get(session);

				if (sha.getDestination().equals("/syndred/" + instance + "/editor/pull")) {
					SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
					headers.setSessionAttributes(sha.getSessionAttributes());
					headers.setSubscriptionId(sha.getSubscriptionId());
					headers.setSessionId(sha.getSessionId());

					SimpMessagingTemplate smt = new SimpMessagingTemplate(channel);
					smt.setMessageConverter(new MappingJackson2MessageConverter());

					Threading.callback(instance, callback(instance, headers.getMessageHeaders(), smt));
				}

				break;
			default:
				break;
			}
		}
	}

	private Function<RawDraftContentState, Exception> callback(String instance, MessageHeaders headers,
			SimpMessagingTemplate smt) {
		return new Function<RawDraftContentState, Exception>() {
			@Override
			public Exception apply(RawDraftContentState state) {
				try {
					smt.convertAndSend("/syndred/" + instance + "/editor/pull", state, headers);
				} catch (Exception exception) {
					return exception;
				}
				return null;
			}
		};
	}

}
