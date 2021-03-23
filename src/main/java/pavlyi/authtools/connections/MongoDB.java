package pavlyi.authtools.connections;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.bson.Document;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import pavlyi.authtools.AuthTools;

public class MongoDB {
	private static AuthTools instance = AuthTools.getInstance();

	public MongoClient mongoClient;
	public MongoDatabase mongoDatabase;
	public MongoCollection collection;
	
	public void connect() {
		try {
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
			rootLogger.setLevel(Level.OFF);

			String uri = "mongodb+srv://" + instance.getConfigHandler().CONNECTION_MONGODB_USERNAME + ":"
					+ instance.getConfigHandler().CONNECTION_MONGODB_PASSWORD + "@"
					+ instance.getConfigHandler().CONNECTION_MONGODB_CLUSTER + "/"
					+ instance.getConfigHandler().CONNECTION_MONGODB_DATABASE + "?retryWrites=true&w=majority";
			MongoClientURI clientURI = new MongoClientURI(uri);
			mongoClient = new MongoClient(clientURI);
			
			mongoDatabase = mongoClient.getDatabase(instance.getConfigHandler().CONNECTION_MONGODB_DATABASE);

			if (!collectionExists()) {
				mongoDatabase.createCollection("authtools");
			}

			collection = mongoDatabase.getCollection("authtools");

			instance.log("&r  &aSuccess: &cMongoDB &fsucessfully connected!");
		} catch (Exception ex) {
			instance.log("&r  &cError: &cMongoDB &fcouldn't connect");
			ex.printStackTrace();
			
			instance.getPluginManager().disablePlugin(instance);
		}
	}

	public void disconnect() {
		try {
			mongoClient.close();

	        instance.log("&r  &aSuccess: &cMongoDB &fsucessfully disconnected!");
		} catch (Exception ex) {
			instance.log("&r  &cError: &cMongoDB &fcouldn't disconnect");
			ex.printStackTrace();

			instance.getPluginManager().disablePlugin(instance);
		}
	}

	public boolean collectionExists() {
	    MongoDatabase database = mongoClient.getDatabase(instance.getConfigHandler().CONNECTION_MONGODB_DATABASE);

	    if (database == null) {
	            return false;
	    }

	    MongoIterable<String> iterable = database.listCollectionNames();

	    try (final MongoCursor<String> it = iterable.iterator()) {
	        while (it.hasNext()) {
	            if (it.next().equalsIgnoreCase("authtools")) {
	                return true;
	            }
	        }
	    }

	    return false;
	}

	public MongoClient getClient() {
		return mongoClient;
	}
	
	public MongoDatabase getDatabase() {
		return mongoDatabase;
	}

	public MongoCollection getCollection() {
		return collection;
	}
}
