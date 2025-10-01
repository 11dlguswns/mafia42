package click.mafia42.entity.room;

import click.mafia42.entity.user.User;

public class GameRoomUser implements Comparable<GameRoomUser> {
    private final int number;
    private final User user;

    public GameRoomUser(GameRoom gameRoom, User user) {
        this.number = gameRoom.getUserNumber();
        this.user = user;
    }

    public int getNumber() {
        return number;
    }

    public User getUser() {
        return user;
    }

    @Override
    public int compareTo(GameRoomUser o) {
        return Integer.compare(this.number, o.number);
    }
}
