package ca.blockbreak.server;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;
import org.json.JSONObject;

import ca.blockbreak.server.service.DatabaseService;
import ca.blockbreak.server.service.DatabaseService.Database;

/**
 * BlockBreakWebSocketHandler implmentation.
 * Implements the WebSocket handler used by BlockBreakWebSocketConfig.
 */
@Component
public class BlockBreakWebSocketHandler extends TextWebSocketHandler {

    /**
     * Stores a list WebSocketSesssions
     */
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final DatabaseService databaseService;

    /**
     * Constructor
     *
     * @param databaseService database service used to access the databases
     */
    public BlockBreakWebSocketHandler(DatabaseService databaseService) {
        super();

        this.databaseService = databaseService;
    }

    /**
     * After connection is established, add the session to the sessions list.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    /**
     * Handles the text message sent from the WebSocket client.  The message should be a JSON object.
     * Should have a "path".  If the "path" is "connect", will send the current global count.
     * If "path" is "breakblock", increment the global count and send the new value to all the sesssions in the list.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        JSONObject json = new JSONObject(payload);

        String path = session.getUri().getPath();

        System.out.println("handleTextMessage: " + path + ", " + json.getString("action"));

        if ("connect".equals(json.getString("action"))) {
            System.out.println("Action connect");

            String action = getGlobalCount(path);

            session.sendMessage(new TextMessage(action));
        } else if ("breakblock".equals(json.getString("action"))) {
            System.out.println("Action breakblock");

            String action = incrementGlobalCount(path);

            for (WebSocketSession webSocketSession : sessions) {
                String wssPath = webSocketSession.getUri().getPath();

                if (path.equals(wssPath) && webSocketSession.isOpen()) {
                    //&& !webSocketSession.getId().equals(session.getId())) {
                    webSocketSession.sendMessage(new TextMessage(action));
                }
            }
        }
    }

    /**
     * After the connection is closed remove the session from the list.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);

        System.out.println("Connection closed");
    }

    /**
     * Get the global count for the database sepcified by the url path.
     *
     * @param path path portion of the url
     * @return returns a json string.
     */
    private String getGlobalCount(String path) {
        int count = -1;

        if ("/mariadb".equals(path)) {
            count = databaseService.getGlobalCount(Database.MARIADB);
        } else if ("/mongodb".equals(path)) {
            count = databaseService.getGlobalCount(Database.MONGODB);
        } else if ("/postgresql".equals(path)) {
            count = databaseService.getGlobalCount(Database.POSTGRESQL);
        }

        String action =
            "{\"action\": \"global\", \"data\": { \"count\": " +
            count +
            " }}";

        return action;
    }

    /**
     * Increment the global count for the databases sepcified by the url path and return the new value.
     *
     * @param path path portion of the url
     * @return returns a json string.
     */
    private String incrementGlobalCount(String path) {
        int count = -1;

        if ("/mariadb".equals(path)) {
            count = databaseService.incrementGlobalCount(Database.MARIADB);
        } else if ("/mongodb".equals(path)) {
            count = databaseService.incrementGlobalCount(Database.MONGODB);
        } else if ("/postgresql".equals(path)) {
            count = databaseService.incrementGlobalCount(Database.POSTGRESQL);
        }

        String action =
            "{\"action\": \"global\", \"data\": { \"count\": " +
            count +
            " }}";

        return action;
    }
}
