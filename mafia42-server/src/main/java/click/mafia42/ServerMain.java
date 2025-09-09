package click.mafia42;

import click.mafia42.database.ChannelManager;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        Class.forName("click.mafia42.database.properties.DBProperties");
        Class.forName("click.mafia42.security.properties.JwtProperties");
        Class.forName("click.mafia42.database.transaction.TransactionManager");
        Class.forName("org.hibernate.validator.internal.util.Version");
        Class.forName("org.hibernate.validator.internal.engine.ValidatorImpl");
        ChannelManager channelManager = new ChannelManager();
        Mafia42Server mafia42Server = new Mafia42Server(channelManager);
        mafia42Server.start();
    }
}
