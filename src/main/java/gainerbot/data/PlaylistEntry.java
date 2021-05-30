package gainerbot.data;

public class PlaylistEntry {
    /**
     * The position of this track in the playlist. Starting at one.
     */
    private int position;

    /**
     * The retrieved title of this track.
     */
    private String title;

    /**
     * The retrieved artist/channel that created this track.
     */
    private String artist;

    /**
     * The duration of this track in ms.
     */
    private long duration;

    /**
     * The link to the track to be added.
     */
    private String source;

    // --------Metadata--------
    /**
     * The unique id of the PlaylistEntry.
     */
    private String uId;

    /**
     * The amount of times this song has been queued.
     */
    private int queueCount;

    /**
     * The time this track has been added to the playlist.
     */
    private long timestampAdded;

    /**
     * TODO Should this store the Username or the ID of the user?
     */
    private String addedBy;

    /**
     * The schema version used.
     */
    private int schema;

    private PlaylistEntry(int position, String title, String artist, long duration, String source, String uId, int queueCount, long timestampAdded, String addedBy, int schema) {
        this.position = position;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.source = source;
        this.uId = uId;
        this.queueCount = queueCount;
        this.timestampAdded = timestampAdded;
        this.addedBy = addedBy;
        this.schema = schema;
    }

    private PlaylistEntry(String source) {
        this.source = source;
    }

    /**
     * Creates a new PlaylistEntry for a given source.
     * @param sourceString The String to serve as a source.
     * @return The created PlaylistEntry.
     */
    public static PlaylistEntry fromSource(String sourceString){
        return new PlaylistEntry(sourceString);
    }

    // TODO Implement

    // region Getter
    public int getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public String getSource() {
        return source;
    }

    public String getuId() {
        return uId;
    }

    public int getQueueCount() {
        return queueCount;
    }

    public long getTimestampAdded() {
        return timestampAdded;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public int getSchema() {
        return schema;
    }
    // endregion

    // region Setter
    public void setPosition(int position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setQueueCount(int queueCount) {
        this.queueCount = queueCount;
    }

    public void setTimestampAdded(long timestampAdded) {
        this.timestampAdded = timestampAdded;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public void setSchema(int schema) {
        this.schema = schema;
    }
    // endregion
}
