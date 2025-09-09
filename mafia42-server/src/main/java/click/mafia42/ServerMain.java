package click.mafia42;

import click.mafia42.database.ChannelManager;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        ChannelManager channelManager = new ChannelManager();
        Mafia42Server mafia42Server = new Mafia42Server(channelManager);
        mafia42Server.start();
    }
}
