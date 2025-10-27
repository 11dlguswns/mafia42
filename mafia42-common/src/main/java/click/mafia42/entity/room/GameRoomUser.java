package click.mafia42.entity.room;

import click.mafia42.entity.user.User;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.Team;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GameRoomUser implements Comparable<GameRoomUser> {
    private final int number;
    private final User user;
    private Job job;
    private Team team;
    private GameUserStatus status = GameUserStatus.ALIVE;
    private final Set<UUID> visibleToUserIds;
    private boolean isProselytized = false;
    private boolean isContacted = false;
    private GameRoomUser voteUser;
    private Boolean voteAgree;
    private boolean isBlackmailed = false;
    private boolean timeControlUsed = false;
    private GameRoom gameRoom;

    public GameRoomUser(GameRoom gameRoom, User user) {
        this.number = gameRoom.getUserNumber();
        this.user = user;
        this.gameRoom = gameRoom;
        visibleToUserIds = new HashSet<>(Set.of(user.getId()));
    }

    public int getNumber() {
        return number;
    }

    public User getUser() {
        return user;
    }

    public Job getJob() {
        return job;
    }

    public Team getTeam() {
        return team;
    }

    public void updateStatus(GameUserStatus status) {
        this.status = status;
    }

    public void updateJob(Job job) {
        this.job = job;
        this.team = job.getJobType().getRole().getTeam();
    }

    public void updateTeam(Team team) {
        this.team = team;
    }

    public GameUserStatus getStatus() {
        return status;
    }

    @Override
    public int compareTo(GameRoomUser o) {
        return Integer.compare(this.number, o.number);
    }

    public boolean isVisibleToUser(UUID userId) {
        return visibleToUserIds.contains(userId);
    }

    public void addVisibleToUserId(UUID userId) {
        visibleToUserIds.add(userId);
    }

    public void addVisibleToUserIds(List<UUID> userIds) {
        userIds.forEach(this::addVisibleToUserId);
    }

    public void addVisibleAllUser() {
        gameRoom.getPlayers().forEach(gUser -> addVisibleToUserId(gUser.getUser().getId()));
    }

    public void connectWithMafia() {
        isContacted = true;
        gameRoom.findUsersByJobType(JobType.MAFIA)
                .forEach(gUser -> {
                    addVisibleToUserId(gUser.getUser().getId());
                    gUser.addVisibleToUserId(user.getId());
                });
    }

    public void clearVote() {
        voteUser = null;
    }

    public void die() {
        status = GameUserStatus.DIE;
    }

    public void resurrection() {
        status = GameUserStatus.ALIVE;
    }

    public boolean isProselytized() {
        return isProselytized;
    }

    public boolean isContacted() {
        return isContacted;
    }

    public void setVoteUser(GameRoomUser voteUser) {
        if (status == GameUserStatus.DIE) {
            throw new GlobalException(GlobalExceptionCode.VOTE_NOT_ALLOWED);
        }

        this.voteUser = voteUser;
    }

    public GameRoomUser getVoteUser() {
        return voteUser;
    }

    public void voteAgree() {
        if (voteAgree != null) {
            throw new GlobalException(GlobalExceptionCode.VOTE_AGREE_OR_DISAGREE_NOT_ALLOWED);
        }

        if (status == GameUserStatus.DIE) {
            throw new GlobalException(GlobalExceptionCode.VOTE_AGREE_OR_DISAGREE_NOT_ALLOWED);
        }

        voteAgree = true;
    }

    public void voteDisagree() {
        if (voteAgree != null) {
            throw new GlobalException(GlobalExceptionCode.VOTE_AGREE_OR_DISAGREE_NOT_ALLOWED);
        }

        if (status == GameUserStatus.DIE) {
            throw new GlobalException(GlobalExceptionCode.VOTE_AGREE_OR_DISAGREE_NOT_ALLOWED);
        }

        voteAgree = false;
    }

    public boolean isVoteAgree() {
        if (voteAgree == null) {
            return false;
        }

        return voteAgree;
    }

    public void clearAgree() {
        voteAgree = null;
    }

    public void blackmailed() {
        isBlackmailed = true;
    }

    public void clearBlackmailed() {
        isBlackmailed = false;
    }

    public boolean isBlackmailed() {
        return isBlackmailed;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }

    public boolean timeControlUsed() {
        return timeControlUsed;
    }

    public void timeControl() {
        this.timeControlUsed = true;
    }

    public void resetTimeControl() {
        this.timeControlUsed = false;
    }
}
