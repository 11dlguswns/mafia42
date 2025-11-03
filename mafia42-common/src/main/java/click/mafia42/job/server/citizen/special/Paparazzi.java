package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.server.PassiveJob;
import click.mafia42.job.server.SkillResult;

public class Paparazzi extends PassiveJob {
    public Paparazzi(GameRoomUser owner) {
        super(owner);
    }

    protected Paparazzi(Paparazzi paparazzi) {
        super(paparazzi);
    }

    @Override
    protected Job copyInternal() {
        return new Paparazzi(this);
    }

    @Override
    public SkillResult passiveAction() {
        // TODO skill 구현
        return null;
    }

    @Override
    public JobType getJobType() {
        return JobType.PAPARAZZI;
    }
}
