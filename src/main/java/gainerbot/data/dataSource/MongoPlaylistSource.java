package gainerbot.data.dataSource;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteConcernException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import gainerbot.data.Playlist;
import gainerbot.data.PlaylistEntry;
import org.bson.Document;

import java.util.Date;

public class MongoPlaylistSource extends MongoDBSource implements IPlaylistDataSource {
    private static MongoCollection<Document> playlistCollection;

    public MongoPlaylistSource() {
        if(playlistCollection == null){
            playlistCollection = database.getCollection("playlist");
        }
    }

    @Override
    public boolean createPlaylist(String name, String creator) {
        Document playlistDocument = new Document("name", name)
                .append("playlistEntries", new PlaylistEntry[0])
                .append("createdBy", creator)
                .append("allowPublicRead", true)
                .append("readUsers", new String[0])
                .append("allowPublicWrite", false)
                .append("writeUsers", new String[0])
                .append("timestampCreated", new Date().toInstant().getEpochSecond())
                .append("schema", 1);

        try {
            playlistCollection.insertOne(playlistDocument);
        }catch (MongoWriteException e){
            System.out.println("Inserting a new Playlist in the database did not succeed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }catch (MongoException e){
            System.out.println("Encountered an error while inserting a document in the database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deletePlaylist(Playlist playlist) {
        return false;
    }

    @Override
    public boolean addPlaylistEntry(Playlist playlist, PlaylistEntry entry) {
        return false;
    }

    @Override
    public boolean updatePlaylistEntry(Playlist playlist, PlaylistEntry entry) {
        return false;
    }

    @Override
    public boolean deletePlaylistEntry(Playlist playlist, PlaylistEntry entry) {
        return false;
    }

    @Override
    public Playlist getPlaylist(Playlist playlist) {
        return null;
    }

    @Override
    public int numMatches(Playlist playlist) {
        return 0;
    }
}
