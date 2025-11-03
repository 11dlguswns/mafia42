package click.mafia42.job.server.citizen;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;

public class Citizen extends Job {
    public Citizen(GameRoomUser owner) {
        super(owner);
    }

    protected Citizen(Citizen citizen) {
        super(citizen);
    }

    @Override
    protected Job copyInternal() {
        return new Citizen(this);
    }

    @Override
    public JobType getJobType() {
        return JobType.CITIZEN;
    }
}