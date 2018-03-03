package syndred.logic;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class Interceptor extends ChannelInterceptorAdapter {

	private Map<String, String> mapping = new HashMap<String, String>();

	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
		String id, session = sha.getSessionId();

		if (sha.getCommand() != null) {
			switch (sha.getCommand()) {
			case CONNECT:
				id = sha.getFirstNativeHeader("id");
				Threading.connect(id);
				mapping.put(session, id);

				break;
			case DISCONNECT:
				id = mapping.get(session);
				mapping.remove(session);
				if (!mapping.containsValue(id))
					Threading.disconnect(id);

				break;
			case SUBSCRIBE:
				id = mapping.get(session);
				callback(id, channel, sha);

				break;
			default:
				break;
			}
		}
	}

	private void callback(String id, MessageChannel channel, StompHeaderAccessor sha) {
		Class<?> key = null;
		SubscribeMapping subscription;

		for (Method method : Controller.class.getDeclaredMethods())
			if ((subscription = method.getAnnotation(SubscribeMapping.class)) != null)
				for (String value : subscription.value())
					if (sha.getDestination().endsWith(value.replace("{instance}", id)))
						key = method.getReturnType();

		if (key == null)
			return;

		SimpMessageHeaderAccessor smha = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		smha.setSessionAttributes(sha.getSessionAttributes());
		smha.setSubscriptionId(sha.getSubscriptionId());
		smha.setSessionId(sha.getSessionId());

		SimpMessagingTemplate smt = new SimpMessagingTemplate(channel);
		smt.setMessageConverter(new MappingJackson2MessageConverter());

		Threading.setCallback(id, key, new Function<Object, MessagingException>() {
			@Override
			public MessagingException apply(Object entity) {
				try {
					smt.convertAndSend(sha.getDestination(), entity, smha.getMessageHeaders());
				} catch (MessagingException exception) {
					return exception;
				}

				return null;
			}
		});
	}

}
