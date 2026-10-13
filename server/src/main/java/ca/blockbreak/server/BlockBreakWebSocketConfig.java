package ca.blockbreak.server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * BlockBreakWebSocketConfig implementation
 * Registers WebSocket handlers.
 */
@Configuration
@EnableWebSocket
public class BlockBreakWebSocketConfig implements WebSocketConfigurer {

    private final BlockBreakWebSocketHandler webSocketHandler;

    /**
     * Constructor
     *
     * @param webSocketHandler each url uses the same handler
     */
    public BlockBreakWebSocketConfig(BlockBreakWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Registers WebSocket handlers.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/mariadb")
                .setAllowedOrigins("*"); // Required for cross-origin requests
        registry.addHandler(webSocketHandler, "/mongodb")
                .setAllowedOrigins("*");
        registry.addHandler(webSocketHandler, "/postgresql")
                .setAllowedOrigins("*");
    }
}
