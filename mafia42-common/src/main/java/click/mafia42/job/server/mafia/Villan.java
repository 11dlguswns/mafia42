package click.mafia42.job.server.mafia;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;

public class Villan extends Job {
    public Villan(GameRoomUser owner) {
        super(owner);
    }

    public Villan(Villan villan) {
        super(villan);
    }

    @Override
    protected Job copyInternal() {
        return new Villan(this);
    }

    @Override
    public JobType getJobType() {
        return JobType.VILLAN;
    }
}
