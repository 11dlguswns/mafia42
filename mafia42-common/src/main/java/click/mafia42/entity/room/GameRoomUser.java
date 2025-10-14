package click.mafia42.entity.room;

import click.mafia42.entity.user.User;
import click.mafia42.job.Job;
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
    private final Set<UUID> visibleToUserIds;

    public GameRoomUser(GameRoom gameRoom, User user) {
        this.number = gameRoom.getUserNumber();
        this.user = user;
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

    public void updateJob(Job job) {
        this.job = job;
        this.team = job.getJobType().getRole().getTeam();
    }

    public void updateTeam(Team team) {
        this.team = team;
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
}
