package click.mafia42.entity.room;

import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameRoom {
    private final long id;
    private String name;
    private final int maxPlayers;
    private final List<User> players;
    private User manager;
    private final GameType gameType;
    private final String password;

    public GameRoom(long id, String name, int maxPlayers, User manager, GameType gameType, String password) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.players = Collections.synchronizedList(new ArrayList<>(Collections.singleton(manager)));
        this.manager = manager;
        this.gameType = gameType;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addPlayer(User user, String password) {
        if (containsPlayer(user)) {
            throw new GlobalException(GlobalExceptionCode.ALREADY_JOINED_ROOM);
        }

        if (maxPlayers <= players.size()) {
            throw new GlobalException(GlobalExceptionCode.ROOM_MEMBER_FULL);
        }

        if (!BCrypt.checkpw(password, this.password)) {
            throw new GlobalException(GlobalExceptionCode.PASSWORD_MISMATCH);
        }

        players.add(user);
    }

    public void removePlayer(User user) {
        if (manager.equals(user)) {
            manager = players.getFirst();
        }

        players.remove(user);
    }

    public boolean containsPlayer(User user) {
        return players.contains(user);
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayersCount() {
        return players.size();
    }

    public List<User> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public User getManager() {
        return manager;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setName(String name) {
        this.name = name;
    }
}
