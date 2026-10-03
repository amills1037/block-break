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

@Component
public class BlockBreakWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final DatabaseService databaseService;

    public BlockBreakWebSocketHandler(DatabaseService databaseService) {
        super();

        this.databaseService = databaseService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);

//         session.sendMessage(new TextMessage("Connection established successfully!"));
//
//         System.out.println("Connection established");
    }

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

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);

        System.out.println("Connection closed");
    }

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
