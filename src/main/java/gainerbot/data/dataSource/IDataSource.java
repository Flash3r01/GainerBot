package gainerbot.data.dataSource;

public interface IDataSource {
    boolean connect();
    boolean disconnect();

    String read(String location);
    boolean write(String location, String data);
}
