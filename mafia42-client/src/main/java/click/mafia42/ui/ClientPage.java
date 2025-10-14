package click.mafia42.ui;

public enum ClientPage {
    LOBBY("LOBBY"), GAME_ROOM_LOBBY("GAME_ROOM_LOBBY"), GAME("GAME");

    ClientPage(String name) {
        this.name = name;
    }

    String name;
}
