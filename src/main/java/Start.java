import logic.GenericServer;

public class Start {
    public static void main(String[] args) {
        GenericServer genericServer = new GenericServer(8090);
        genericServer.start();

    }
}
