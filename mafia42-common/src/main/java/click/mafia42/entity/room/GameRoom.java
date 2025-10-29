package click.mafia42.entity.room;

import click.mafia42.dto.client.SaveGameMessageReq;
import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.job.JobType;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;
import click.mafia42.job.server.citizen.special.Lover;
import click.mafia42.job.server.citizen.special.Politician;
import click.mafia42.job.server.citizen.special.Soldier;
import click.mafia42.util.TimeUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private volatile GameStatus status;
    private long endTimeSecond;
    private final List<GameMessageDto> gameMessages = new ArrayList<>();
    private int day;
    private final Lock lock = new ReentrantLock();

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
        if (status != null) {
            throw new GlobalException(GlobalExceptionCode.GAME_ALREADY_STARTED);
        }

        status = GameStatus.NIGHT;
        endTimeSecond = Instant.now().plus(getGameTime()).getEpochSecond();
    }

    public boolean updateStatus() {
        if (!TimeUtil.isTimeOver(endTimeSecond)) {
            return false;
        }

        status = switch (status) {
            case NIGHT -> GameStatus.MORNING;
            case MORNING -> GameStatus.VOTING;
            case VOTING -> {
                Optional<GameRoomUser> mostVotedUser = getMostVotedUser();
                if (mostVotedUser.isEmpty()) {
                    yield GameStatus.NIGHT;
                }

                yield GameStatus.CONTRADICT;
            }
            case CONTRADICT -> GameStatus.JUDGEMENT;
            case JUDGEMENT -> GameStatus.NIGHT;
        };

        return true;
    }

    public void updateEndTime() {
        endTimeSecond = Instant.now().plus(getGameTime()).getEpochSecond();
    }

    public void endMorningEvent() {
        clearVotes();
        clearAgreeUser();
        clearBlackMailed();
    }

    public void startMorningEvent() {
        clearTimeControlUserIds();
        day++;
    }


    public void clearTimeControlUserIds() {
        players.forEach(GameRoomUser::resetTimeControl);
    }

    private void clearBlackMailed() {
        players.forEach(GameRoomUser::clearBlackmailed);
    }

    private void clearAgreeUser() {
        players.forEach(GameRoomUser::clearAgree);
    }

    public void clearVotes() {
        players.forEach(GameRoomUser::clearVote);
    }

    public void clearSeduced() {
        players.forEach(GameRoomUser::resetSeduced);
    }

    public boolean isVotePassed() {
        return getAgreeUserCount() > getVoteAllowedPlayerCount() / 2;
    }

    private long getAgreeUserCount() {
        return players.stream()
                .filter(GameRoomUser::isVoteAgree)
                .mapToLong(gUser -> {
                    if (gUser.getJob() instanceof Politician && !gUser.isSeduced()) {
                        return 2L;
                    } else {
                        return 1L;
                    }
                })
                .sum();
    }

    private long getVoteAllowedPlayerCount() {
        return players.stream()
                .filter(gameRoomUser -> {
                    boolean isAlive = gameRoomUser.getStatus() == GameUserStatus.ALIVE;
                    boolean isNotBlackmailed = !gameRoomUser.isBlackmailed();
                    return isAlive && isNotBlackmailed;
                }).mapToLong(gUser -> {
                    if (!gUser.isVoteAgree() && gUser.getJob() instanceof Politician && !gUser.isSeduced()) {
                        return 2L;
                    } else {
                        return 1L;
                    }
                })
                .sum();
    }

    public Optional<GameRoomUser> getMostVotedUser() {
        Map<GameRoomUser, Long> voteCountMap = players.stream()
                .filter(gUser -> gUser.getVoteUser() != null)
                .collect(Collectors.groupingBy(GameRoomUser::getVoteUser, Collectors.counting()));

        voteCountMap.forEach((gUser, voteCount) -> {
            if (gUser.getJob() instanceof Politician && !gUser.isSeduced()) {
                voteCountMap.put(gUser, voteCount + 1);
            }
        });

        long maxVotes = voteCountMap.values().stream()
                .max(Long::compare)
                .orElse(0L);

        List<GameRoomUser> topUsers = voteCountMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxVotes)
                .map(Map.Entry::getKey)
                .toList();

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

    public void increaseGameTime(GameRoomUser gameRoomUser) {
        if (gameRoomUser.timeControlUsed()) {
            throw new GlobalException(GlobalExceptionCode.TIME_ALREADY_MODIFIED);
        }

        if (gameRoomUser.getStatus() == GameUserStatus.DIE) {
            throw new GlobalException(GlobalExceptionCode.TIME_MODIFICATION_NOT_ALLOWED);
        }

        if (!isStarted) {
            throw new GlobalException(GlobalExceptionCode.GAME_NOT_STARTED);
        }

        if (status != GameStatus.MORNING) {
            throw new GlobalException(GlobalExceptionCode.TIME_MODIFICATION_NOT_ALLOWED);
        }

        endTimeSecond += 15;
        gameRoomUser.timeControl();
    }

    public void decreaseGameTime(GameRoomUser gameRoomUser) {
        if (gameRoomUser.timeControlUsed()) {
            throw new GlobalException(GlobalExceptionCode.TIME_ALREADY_MODIFIED);
        }

        if (gameRoomUser.getStatus() == GameUserStatus.DIE) {
            throw new GlobalException(GlobalExceptionCode.TIME_MODIFICATION_NOT_ALLOWED);
        }

        if (!isStarted) {
            throw new GlobalException(GlobalExceptionCode.GAME_NOT_STARTED);
        }

        if (status != GameStatus.MORNING) {
            throw new GlobalException(GlobalExceptionCode.TIME_MODIFICATION_NOT_ALLOWED);
        }

        endTimeSecond -= 15;
        gameRoomUser.timeControl();
    }

    public void addGameMessage(SaveGameMessageReq message, Set<GameRoomUser> visibleChatToUsers) {
        gameMessages.add(new GameMessageDto(message, visibleChatToUsers));
    }

    public List<GameMessageDto> getChatMessages() {
        return gameMessages;
    }

    public int getDay() {
        return day;
    }

    public void voteUser(GameRoomUser requestUser, GameRoomUser voteUser) {
        if (status != GameStatus.MORNING && status != GameStatus.VOTING) {
            throw new GlobalException(GlobalExceptionCode.VOTE_NOT_ALLOWED);
        }

        if (voteUser.getStatus() == GameUserStatus.DIE) {
            throw new GlobalException(GlobalExceptionCode.VOTE_TARGET_DEAD_NOT_ALLOWED);
        }

        if (!isStarted) {
            throw new GlobalException(GlobalExceptionCode.GAME_NOT_STARTED);
        }

        requestUser.setVoteUser(voteUser);
    }

    public long getUserVoteCount(GameRoomUser gameRoomUser) {
        if (gameRoomUser == null) {
            return 0;
        }

        return players.stream()
                .filter(gUser -> gameRoomUser.equals(gUser.getVoteUser()))
                .count();
    }

    public void voteAgree(GameRoomUser gameRoomUser) {
        if (status != GameStatus.JUDGEMENT) {
            throw new GlobalException(GlobalExceptionCode.VOTE_AGREE_OR_DISAGREE_NOT_ALLOWED);
        }

        if (!isStarted) {
            throw new GlobalException(GlobalExceptionCode.GAME_NOT_STARTED);
        }

        gameRoomUser.voteAgree();
    }

    public void voteDisagree(GameRoomUser gameRoomUser) {
        if (status != GameStatus.JUDGEMENT) {
            throw new GlobalException(GlobalExceptionCode.VOTE_AGREE_OR_DISAGREE_NOT_ALLOWED);
        }

        if (!isStarted) {
            throw new GlobalException(GlobalExceptionCode.GAME_NOT_STARTED);
        }

        gameRoomUser.voteDisagree();
    }

    public SkillResult dieUser(GameRoomUser target) {
        SkillResult skillResult = new SkillResult();

        skillResult.concat(useSkillByJobType(JobType.BEAST_MAN));
        if (!skillResult.isEmpty()) return skillResult;

        skillResult.concat(useSkillByJobType(JobType.DOCTOR));
        if (!skillResult.isEmpty()) return skillResult;

        if (target.getJob() instanceof Soldier soldier) {
            skillResult.concat(soldier.usePassive());
            if (!skillResult.isEmpty()) return skillResult;
        }

        if (target.getJob() instanceof Lover lover) {
            skillResult.concat(lover.usePassive());
            if (!skillResult.isEmpty()) return skillResult;
        }

        target.die();
        skillResult.concat(new SkillResult(
                List.of(new MessageResult(
                        String.format("%s이(가) 살해당했습니다.", target.getUser().getNickname()),
                        players))));
        return skillResult;
    }

    private SkillResult useSkillByJobType(JobType jobType) {
        SkillResult skillResult = new SkillResult();

        List<GameRoomUser> usersByJobType = findUsersByJobType(jobType);
        for (GameRoomUser gUser : usersByJobType) {
            if (gUser.getJob() instanceof SkillJob skillJob) {
                skillResult.concat(skillJob.useSkill());
            }
        }

        return skillResult;
    }

    public Set<GameRoomUser> findUsersByMafiaTeam() {
        return Stream.concat(
                players.stream().filter(gUser -> gUser.getJob().getJobType() == JobType.MAFIA),
                players.stream().filter(GameRoomUser::isContacted)
        ).collect(Collectors.toSet());
    }

    public List<GameRoomUser> findUsersByJobType(JobType jobType) {
        return players.stream()
                .filter(gUser -> gUser.getJob().getJobType() == jobType)
                .toList();
    }

    public GameRoomUser findSharedActiveTarget(SharedActiveType sharedActiveType) {
        Optional<GameRoomUser> sharedActiveUser = findUserBySharedActive(sharedActiveType);

        if (sharedActiveUser.isPresent()) {
            if (sharedActiveUser.get().getJob() instanceof SkillJob skillJob) {
                return skillJob.getTarget();
            }
        }

        return null;
    }

    public Optional<GameRoomUser> findUserBySharedActive(SharedActiveType sharedActiveType) {
        return players.stream()
                .filter(gUser -> {
                    if (gUser.getStatus() == GameUserStatus.ALIVE && gUser.getJob() instanceof SkillJob skillJob) {
                        return skillJob.getSharedActiveType() == sharedActiveType;
                    }

                    return false;
                }).findFirst();
    }

    public <T> T doWithLock(Supplier<T> action) {
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }
}
