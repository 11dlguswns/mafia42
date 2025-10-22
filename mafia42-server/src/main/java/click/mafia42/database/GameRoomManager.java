package click.mafia42.database;

import click.mafia42.entity.room.GameRoom;
import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameType;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.job.JobType;
import click.mafia42.util.GameUtil;
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

    public void exitGameRoom(GameRoom gameRoom, GameRoomUser gameRoomUser) {
        gameRoom.removePlayer(gameRoomUser);

        if (gameRoom.getPlayers().isEmpty()) {
            gameRooms.remove(gameRoom.getId());
            freeIds.add(gameRoom.getId());
        }
    }

    public void removeGameRoom(GameRoom gameRoom, GameRoomUser gameRoomUser) {
        if (isRemovalAllowedForManagerOnly(gameRoom, gameRoomUser)) {
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

    public Optional<GameRoom> findGameRoomByGameRoomUser(User user) {
        return gameRooms.values().stream()
                .filter(gr -> gr.getPlayers().stream()
                        .anyMatch(gameRoomUser -> gameRoomUser.getUser().equals(user)))
                .findFirst();
    }

    public void startGame(GameRoom gameRoom) {
        if (gameRoom.isStarted()) {
            throw new GlobalException(GlobalExceptionCode.GAME_ALREADY_STARTED);
        }
        if (gameRoom.getPlayersCount() < 4) {
            throw new GlobalException(GlobalExceptionCode.GAME_START_FAIL);
        }

        ArrayDeque<JobType> job = GameUtil.getJob(gameRoom.getPlayersCount(), gameRoom.getGameType());

        if (!(job.size() == gameRoom.getPlayersCount())) {
            throw new GlobalException(GlobalExceptionCode.GAME_START_FAIL);
        }

        gameRoom.getPlayers().forEach(gameRoomUser -> {
            JobType jobType = Objects.requireNonNull(job.poll());
            gameRoomUser.updateJob(GameUtil.convertToJob(jobType, gameRoomUser));
        });

        setMutualVisibilityByJobType(gameRoom, JobType.MAFIA);
        setMutualVisibilityByJobType(gameRoom, JobType.LOVER);

        gameRoom.initStatus();
        gameRoom.setStarted(true);
    }

    public boolean isUserInAnyGameRoom(User user) {
        return gameRooms.values().stream().anyMatch(gameRoom -> gameRoom.containsPlayer(user));
    }

    private void setMutualVisibilityByJobType(GameRoom gameRoom, JobType jobType) {
        List<UUID> mutualIds = gameRoom.getPlayers().stream()
                .filter(gameRoomUser -> gameRoomUser.getJob().getJobType() == jobType)
                .map(gameRoomUser -> gameRoomUser.getUser().getId())
                .toList();
        gameRoom.getPlayers().forEach(gameRoomUser -> {
            if (gameRoomUser.getJob().getJobType() == jobType) {
                gameRoomUser.addVisibleToUserIds(mutualIds);
            }
        });
    }

    private long getRoomId() {
        if (freeIds.isEmpty()) {
            return nextId.getAndIncrement();
        } else {
            return freeIds.poll();
        }
    }

    private boolean isRemovalAllowedForManagerOnly(GameRoom gameRoom, GameRoomUser gameRoomUser) {
        return gameRoomUser != gameRoom.getManager() || gameRoom.getPlayersCount() > 1;
    }
}
