package click.mafia42.job.server.mafia;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.server.PassiveJob;
import click.mafia42.job.server.SkillResult;

public class MadScientist extends PassiveJob {
    public MadScientist(GameRoomUser owner) {
        super(owner);
    }

    protected MadScientist(MadScientist madScientist) {
        super(madScientist);
    }

    @Override
    protected Job copyInternal() {
        return new MadScientist(this);
    }

    @Override
    public SkillResult passiveAction() {
        // TODO skill 구현
        return null;
    }

    @Override
    public JobType getJobType() {
        return JobType.MAD_SCIENTIST;
    }
}
