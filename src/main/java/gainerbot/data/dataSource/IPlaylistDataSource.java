package gainerbot.data.dataSource;

import gainerbot.data.Playlist;
import gainerbot.data.PlaylistEntry;

public interface IPlaylistDataSource extends IDataSource {
    boolean createPlaylist(String name, String creator);
    boolean deletePlaylist(Playlist playlist);

    boolean addPlaylistEntry(Playlist playlist, PlaylistEntry entry);
    boolean updatePlaylistEntry(Playlist playlist, PlaylistEntry entry);
    boolean deletePlaylistEntry(Playlist playlist, PlaylistEntry entry);

    Playlist getPlaylist(Playlist playlist);

    int numMatches(Playlist playlist);

    // TODO delete Methods should probably return the deleted elements.
}
