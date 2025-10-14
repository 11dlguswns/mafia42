package click.mafia42.entity.room;

import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameRoom {
    private final long id;
    private String name;
    private final int maxPlayers;
    private final SortedSet<GameRoomUser> players;
    private GameRoomUser manager;
    private final GameType gameType;
    private final String password;
    private boolean isStarted = false;
    private final PriorityQueue<Integer> freeNumbers = new PriorityQueue<>(12);
    private final AtomicInteger nextNumber = new AtomicInteger(1);

    public GameRoom(long id, String name, int maxPlayers, User manager, GameType gameType, String password) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
        GameRoomUser gameRoomManager = new GameRoomUser(this, manager);
        this.players = Collections.synchronizedSortedSet(new TreeSet<>(Collections.singleton(gameRoomManager)));
        this.manager = gameRoomManager;
        this.gameType = gameType;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getUserNumber() {
        if (freeNumbers.isEmpty()) {
            return nextNumber.getAndIncrement();
        } else {
            return freeNumbers.poll();
        }
    }

    public void addPlayer(User user, String password) {
        if (isStarted) {
            throw new GlobalException(GlobalExceptionCode.GAME_ALREADY_STARTED);
        }

        if (containsPlayer(user)) {
            throw new GlobalException(GlobalExceptionCode.ALREADY_JOINED_ROOM);
        }

        if (maxPlayers <= players.size()) {
            throw new GlobalException(GlobalExceptionCode.ROOM_MEMBER_FULL);
        }

        if (this.password != null && !BCrypt.checkpw(password, this.password)) {
            throw new GlobalException(GlobalExceptionCode.PASSWORD_MISMATCH);
        }

        players.add(new GameRoomUser(this, user));
    }

    public void removePlayer(GameRoomUser gameRoomuser) {
        players.remove(gameRoomuser);
        freeNumbers.add(gameRoomuser.getNumber());

        if (!players.isEmpty() && isManager(gameRoomuser)) {
            manager = players.getFirst();
        }
    }

    public boolean isManager(GameRoomUser gameRoomUser) {
        return manager.equals(gameRoomUser);
    }

    public boolean containsPlayer(User user) {
        return players.stream()
                .anyMatch(gameRoomUser -> gameRoomUser.getUser().equals(user));
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayersCount() {
        return players.size();
    }

    public Set<GameRoomUser> getPlayers() {
        return Collections.unmodifiableSortedSet(players);
    }

    public Optional<GameRoomUser> getPlayer(UUID userId) {
        return players.stream()
                .filter(player -> player.getUser().getId().equals(userId))
                .findFirst();
    }

    public GameRoomUser getManager() {
        return manager;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public boolean existPassword() {
        return password != null;
    }
}
