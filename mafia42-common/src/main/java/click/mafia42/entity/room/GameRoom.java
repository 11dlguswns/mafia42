package click.mafia42.entity.room;

import click.mafia42.dto.client.SaveGameMessageReq;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.util.TimeUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private GameStatus status;
    private long endTimeSecond;
    private final Set<UUID> agreeUserIds = new HashSet<>();
    private final Set<UUID> timeControlUserIds = new HashSet<>();
    private final List<SaveGameMessageReq> chatMessages = new ArrayList<>();
    private int day;

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

    public GameStatus getStatus() {
        return status;
    }

    public Long getEndTimeSecond() {
        return endTimeSecond;
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

    public void initStatus() {
        status = GameStatus.NIGHT;
        endTimeSecond = Instant.now().plus(getGameTime()).getEpochSecond();
    }

    public synchronized void updateStatus() {
        if (status == null) {
            return;
        }

        if (!TimeUtil.isTimeOver(endTimeSecond)) {
            return;
        }

        status = switch (status) {
            case NIGHT -> {
                timeControlUserIds.clear();
                yield GameStatus.MORNING;
            }
            case MORNING -> GameStatus.VOTING;
            case VOTING -> {
                Optional<GameRoomUser> mostVotedUser = getMostVotedUser();
                if (mostVotedUser.isEmpty()) {
                    yield GameStatus.NIGHT;
                }

                yield GameStatus.CONTRADICT;
            }
            case CONTRADICT -> GameStatus.JUDGEMENT;
            case JUDGEMENT -> {
                Optional<GameRoomUser> mostVotedUser = getMostVotedUser();
                if (mostVotedUser.isPresent() && isVotePassed()) {
                    mostVotedUser.get().die();
                }

                clearVotes();
                agreeUserIds.clear();
                day++;
                yield GameStatus.NIGHT;
            }
        };

        endTimeSecond = Instant.now().plus(getGameTime()).getEpochSecond();
    }

    private void clearVotes() {
        players.forEach(GameRoomUser::clearVotes);
    }

    private boolean isVotePassed() {
        return agreeUserIds.size() > getAlivePlayerCount() / 2;
    }

    public Optional<GameRoomUser> getMostVotedUser() {
        Map<Integer, List<GameRoomUser>> groupedByVotes = players.stream()
                .collect(Collectors.groupingBy(user -> user.getVotedByUserIds().size()));

        int maxVotes = groupedByVotes.keySet().stream()
                .max(Integer::compare)
                .orElse(0);

        List<GameRoomUser> topUsers = groupedByVotes.getOrDefault(maxVotes, List.of());

        if (topUsers.size() != 1) {
            return Optional.empty();
        }

        return Optional.of(topUsers.getFirst());
    }

    private Duration getGameTime() {
        Duration gameTime = status.getDefaultTime();

        if (status == GameStatus.MORNING) {
            long alivePlayerCount = getAlivePlayerCount();

            return Duration.ofSeconds(gameTime.getSeconds() * alivePlayerCount);
        }

        return gameTime;
    }

    private long getAlivePlayerCount() {
        return players.stream()
                .filter(gameRoomUser -> gameRoomUser.getStatus() == GameUserStatus.ALIVE).count();
    }

    public void increaseGameTime(User user) {
        if (timeControlUserIds.contains(user.getId())) {
            throw new GlobalException(GlobalExceptionCode.TIME_ALREADY_MODIFIED);
        }

        if (status != GameStatus.MORNING) {
            throw new GlobalException(GlobalExceptionCode.TIME_MODIFICATION_NOT_ALLOWED);
        }

        endTimeSecond += 15;
        timeControlUserIds.add(user.getId());
    }

    public void decreaseGameTime(User user) {
        if (timeControlUserIds.contains(user.getId())) {
            throw new GlobalException(GlobalExceptionCode.TIME_ALREADY_MODIFIED);
        }

        if (status != GameStatus.MORNING) {
            throw new GlobalException(GlobalExceptionCode.TIME_MODIFICATION_NOT_ALLOWED);
        }

        endTimeSecond -= 15;
        timeControlUserIds.add(user.getId());
    }

    public void addChatMessage(SaveGameMessageReq message) {
        chatMessages.add(message);
    }

    public List<SaveGameMessageReq> getChatMessages() {
        return chatMessages;
    }

    public int getDay() {
        return day;
    }
}
