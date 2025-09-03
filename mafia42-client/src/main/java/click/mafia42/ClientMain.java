package click.mafia42;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        Mafia42Client mafia42Client = new Mafia42Client();
        mafia42Client.start();
    }
}
