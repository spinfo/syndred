package syndred.logic;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

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
					Threading.connect(instance);
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

}
