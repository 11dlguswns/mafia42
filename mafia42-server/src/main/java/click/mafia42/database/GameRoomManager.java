package click.mafia42.database;

import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.room.GameType;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GameRoomManager {
    private final ConcurrentHashMap<Long, GameRoom> gameRooms = new ConcurrentHashMap<>();
    private final PriorityQueue<Long> freeIds = new PriorityQueue<>();
    private final AtomicLong nextId = new AtomicLong(0);

    public long createGameRoom(String name, int maxPlayers, User user, GameType gameType, String password) {
        if (gameRooms.values().stream().anyMatch(gameRoom -> gameRoom.containsPlayer(user))) {
            throw new GlobalException(GlobalExceptionCode.ALREADY_JOINED_ROOM);
        }

        if (password != null) {
            password = BCrypt.hashpw(password, BCrypt.gensalt());
        }

        GameRoom gameRoom = new GameRoom(getRoomId(), name, maxPlayers, user, gameType, password);
        gameRooms.put(gameRoom.getId(), gameRoom);
        return gameRoom.getId();
    }

    public void exitGameRoom(GameRoom gameRoom, User user) {
        gameRoom.removePlayer(user);

        if (gameRoom.getPlayers().isEmpty()) {
            gameRooms.remove(gameRoom.getId());
            freeIds.add(gameRoom.getId());
        }
    }

    public void removeGameRoom(GameRoom gameRoom, User user) {
        if (isRemovalAllowedForManagerOnly(gameRoom, user)) {
            throw new GlobalException(GlobalExceptionCode.ROOM_REMOVE_NOT_ALLOWED);
        }

        gameRooms.remove(gameRoom.getId());
        freeIds.add(gameRoom.getId());
    }

    public Optional<GameRoom> findById(long id) {
        return Optional.ofNullable(gameRooms.get(id));
    }

    public List<GameRoom> findAll() {
        return gameRooms.values().stream().toList();
    }

    public Optional<GameRoom> findGameRoomByUser(User user) {
        return gameRooms.values().stream()
                .filter(gr -> gr.getPlayers().contains(user))
                .findFirst();
    }

    private long getRoomId() {
        if (freeIds.isEmpty()) {
            return nextId.getAndIncrement();
        } else {
            return freeIds.poll();
        }
    }

    private boolean isRemovalAllowedForManagerOnly(GameRoom gameRoom, User user) {
        return user != gameRoom.getManager() || gameRoom.getPlayersCount() > 1;
    }
}
