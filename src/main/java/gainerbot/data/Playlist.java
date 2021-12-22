package gainerbot.data;

import org.bson.Document;

import java.util.ArrayList;

public class Playlist {
    /**
     * The assigned name of the playlist.
     */
    private String name;

    /**
     * The entries this playlist contains.
     */
    private ArrayList<PlaylistEntry> playlistEntries;

    // --------Metadata--------
    /**
     * The unique id of the playlist.
     */
    private String uId;

    /**
     * TODO Username or ID of the user that created this.
     */
    private String createdBy;

    /**
     * Permission for everyone to view this playlist.
     */
    private boolean allowPublicRead;

    /**
     * If public read is off, only these users can view the playlist.
     */
    private ArrayList<String> readUsers;

    /**
     * Permission for everyone to add, modify and delete tracks in this playlist.
     */
    private boolean allowPublicWrite;

    /**
     * If public write is off, only these users can add, modify and delete tracks in this playlist.
     */
    private ArrayList<String> writeUsers;

    /**
     * The time this playlist was created.
     */
    private long timestampCreated = 0;

    /**
     * The schema version used.
     */
    private int schema = 0;

    // State booleans, to check if booleans are valid.
    private boolean publicReadAssigned = false;
    private boolean publicWriteAssigned = false;

    public Document toDocument(){
        Document ret = new Document();

        if (name != null){
            ret.append("name", name);
        }
        if (playlistEntries != null){
            ret.append("playlistEntries", playlistEntries);
        }
        if (createdBy != null){
            ret.append("createdBy", createdBy);
        }
        if (publicReadAssigned){
            ret.append("allowPublicRead", allowPublicRead);
        }
        if (readUsers != null){
            ret.append("readUsers", readUsers);
        }
        if (publicWriteAssigned){
            ret.append("allowPublicWrite", allowPublicWrite);
        }
        if (writeUsers != null){
            ret.append("writeUsers", writeUsers);
        }
        if (timestampCreated != 0){
            ret.append("timestampCreated", timestampCreated);
        }
        if (schema != 0){
            ret.append("schema", schema);
        }

        return ret;
    }

    // TODO Implement

    // region Getter
    public String getName() {
        return name;
    }

    public ArrayList<PlaylistEntry> getPlaylistEntries() {
        return playlistEntries;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public boolean isAllowPublicRead() {
        return allowPublicRead;
    }

    public ArrayList<String> getReadUsers() {
        return readUsers;
    }

    public boolean isAllowPublicWrite() {
        return allowPublicWrite;
    }

    public ArrayList<String> getWriteUsers() {
        return writeUsers;
    }

    public long getTimestampCreated() {
        return timestampCreated;
    }

    public int getSchema() {
        return schema;
    }
    // endregion
}
