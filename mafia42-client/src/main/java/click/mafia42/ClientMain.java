package click.mafia42;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        Class.forName("org.hibernate.validator.internal.util.Version");
        Class.forName("org.hibernate.validator.internal.engine.ValidatorImpl");
        Mafia42Client mafia42Client = new Mafia42Client();

        new Thread(new ClientWriter()).start();
        mafia42Client.start();
    }
}
