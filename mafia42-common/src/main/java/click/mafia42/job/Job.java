package click.mafia42.job;

import click.mafia42.entity.room.GameRoomUser;

public abstract class Job {
    protected GameRoomUser owner;

    public GameRoomUser getOwner() {
        return owner;
    }

    public void updateOwner(GameRoomUser owner) {
        this.owner = owner;
    }

    protected Job(GameRoomUser owner) {
        this.owner = owner;
    }

    abstract public JobType getJobType();
}
