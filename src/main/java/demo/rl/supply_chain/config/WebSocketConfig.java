package demo.rl.supply_chain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


/**
 * Configuration class for web socket
 * 
 * (this application uses web socket for displaying live simulation process)
 * 
 * @author gamita
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {

		config.enableSimpleBroker("/topic");

	}



	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

		registry.addEndpoint("/ws").withSockJS();

	}

}
