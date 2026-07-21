package ca.blockbreak.server.database;

import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;

public final class MongoDBDAO implements StatsDAO {

    private static final String COLLECTION_NAME = "statistics";
    private static final String PLAYER_ID_FIELD = "player_id";
    private static final String STAT_NAME_FIELD = "stat_name";
    private static final String GLOBAL_PLAYER_ID = "global";
    private static final String BLOCKS_BROKEN_STAT = "blocksBroken";
    private static final String STAT_FIELD = "stat";

    private final Object lock = new Object();
    private static MongoClient mongoClient;

    public MongoDatabase mongoDatabase;

    public MongoDBDAO(SecretsManager sm) {
        if (mongoClient == null) {
            synchronized(lock) {
                if (mongoClient == null) {
                    String host = sm.getMongoDBHost();
                    String username = sm.getMongoDBUsername();
                    String password = sm.getMongoDBPassword();

                    String uri = "mongodb://" + username + ":" + password + "@" + host + ":27017/";
                    // Construct a ServerApi instance using the ServerApi.builder() method
                    ServerApi serverApi = ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build();
                    MongoClientSettings settings = MongoClientSettings.builder()
                            .applyConnectionString(new ConnectionString(uri))
                            .serverApi(serverApi)
                            .build();

                    // Create a new client and connect to the server
                    mongoClient = MongoClients.create(settings);
                }
            }
        }

        mongoDatabase = mongoClient.getDatabase(sm.getMongoDBDatabase());
    }

    public int incrementGlobalCount() {
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
                .returnDocument(ReturnDocument.AFTER);
        Document updatedDoc = collection.findOneAndUpdate(
            and(eq(PLAYER_ID_FIELD, GLOBAL_PLAYER_ID),
                eq(STAT_NAME_FIELD, BLOCKS_BROKEN_STAT)),
            inc(STAT_FIELD, 1),
            options);

        int count = updatedDoc.getInteger(STAT_FIELD);

        return count;
    }

    public int getGlobalCount() {
        MongoCollection<Document> collection = mongoDatabase.getCollection(COLLECTION_NAME);

        Document updatedDoc = collection.find(
            and(eq(PLAYER_ID_FIELD, GLOBAL_PLAYER_ID),
                eq(STAT_NAME_FIELD, BLOCKS_BROKEN_STAT))
        ).first();

        int count = updatedDoc.getInteger(STAT_FIELD);

        return count;
    }

    public void close() {
    }

    public static void closePool() {
        //Should we syncronize?
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }

}
