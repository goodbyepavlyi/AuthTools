package pavlyi.authtools.spigot.connections;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.slf4j.LoggerFactory;
import pavlyi.authtools.spigot.AuthTools;

public class MongoDB {
    private static final AuthTools instance = AuthTools.getInstance();

    public MongoClient mongoClient;
    public MongoDatabase mongoDatabase;
    public MongoCollection collection;

    public boolean connect(boolean silent) {
        try {
            ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.OFF);

            String uri = "mongodb+srv://" + instance.getConfigHandler().CONNECTION_MONGODB_USERNAME + ":"
                    + instance.getConfigHandler().CONNECTION_MONGODB_PASSWORD + "@"
                    + instance.getConfigHandler().CONNECTION_MONGODB_CLUSTER + "/"
                    + instance.getConfigHandler().CONNECTION_MONGODB_DATABASE + "?retryWrites=true&w=majority";

            MongoClientURI clientURI = new MongoClientURI(uri);
            mongoClient = new MongoClient(clientURI);

            mongoDatabase = mongoClient.getDatabase(instance.getConfigHandler().CONNECTION_MONGODB_DATABASE);

            if (!collectionExists())
                mongoDatabase.createCollection("authtools");

            collection = mongoDatabase.getCollection("authtools");

            if (!silent)
                instance.log("&r  &aSuccess: &cMongoDB &fsucessfully connected!");

            return true;
        } catch (Exception exception) {
            instance.log("&r  &cError: &cMongoDB &fcouldn't connect");
            exception.printStackTrace();

            return false;
        }
    }

    public boolean disconnect(boolean silent) {
        try {
            mongoClient.close();

            if (!silent)
                instance.log("&r  &aSuccess: &cMongoDB &fsucessfully disconnected!");

            return true;
        } catch (Exception exception) {
            instance.log("&r  &cError: &cMongoDB &fcouldn't disconnect");
            exception.printStackTrace();

            return false;
        }
    }

    public boolean collectionExists() {
        MongoDatabase database = mongoClient.getDatabase(instance.getConfigHandler().CONNECTION_MONGODB_DATABASE);

        if (database == null)
            return false;

        MongoIterable<String> iterable = database.listCollectionNames();

        try (final MongoCursor<String> it = iterable.iterator()) {
            while (it.hasNext())
                if (it.next().equalsIgnoreCase("authtools"))
                    return true;
        }

        return false;
    }

    public MongoCollection getCollection() {
        return collection;
    }
}
