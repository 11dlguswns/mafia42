package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.server.PassiveJob;
import click.mafia42.job.server.SkillResult;

public class Prophet extends PassiveJob {
    public Prophet(GameRoomUser owner) {
        super(owner);
    }

    protected Prophet(Prophet prophet) {
        super(prophet);
    }

    @Override
    protected Job copyInternal() {
        return new Prophet(this);
    }

    @Override
    public SkillResult passiveAction() {
        // TODO skill 구현
        return null;
    }

    @Override
    public JobType getJobType() {
        return JobType.PROPHET;
    }
}