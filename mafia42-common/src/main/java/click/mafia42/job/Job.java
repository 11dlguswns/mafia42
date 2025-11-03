package click.mafia42.job;

import click.mafia42.entity.room.GameRoomUser;

public abstract class Job {
    protected GameRoomUser owner;

    protected Job(GameRoomUser owner) {
        this.owner = owner;
    }

    protected Job(Job job) {
        this.owner = job.owner;
    }

    public GameRoomUser getOwner() {
        return owner;
    }

    public void updateOwner(GameRoomUser owner) {
        this.owner = owner;
    }

    public Job copy(GameRoomUser owner) {
        Job cloned = copyInternal();
        cloned.updateOwner(owner);
        return cloned;
    }

    protected abstract Job copyInternal();

    abstract public JobType getJobType();
}
