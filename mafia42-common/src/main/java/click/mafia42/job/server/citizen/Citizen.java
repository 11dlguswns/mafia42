package click.mafia42.job.server.citizen;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.server.PassiveJob;

public class Citizen extends Job {
    public Citizen(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public JobType getJobType() {
        return JobType.CITIZEN;
    }
}