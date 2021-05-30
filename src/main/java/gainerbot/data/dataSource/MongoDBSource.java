package gainerbot.data.dataSource;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import gainerbot.GainerBotConfiguration;

public class MongoDBSource implements IDataSource {
    protected static MongoClient mongoClient;
    protected static MongoDatabase database;

    protected MongoDBSource(){
        if(mongoClient == null && database == null) {
            mongoClient = MongoClients.create();
            database = mongoClient.getDatabase(GainerBotConfiguration.databaseName);
        }
    }

    @Override
    public void disconnect() {
        // Mongo connections don't have to be closed?
    }
}
