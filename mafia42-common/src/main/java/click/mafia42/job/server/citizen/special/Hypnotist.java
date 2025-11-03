package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;

public class Hypnotist extends SkillJob {
    public Hypnotist(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, true);
    }

    protected Hypnotist(Hypnotist hypnotist) {
        super(hypnotist);
    }

    @Override
    protected Job copyInternal() {
        return new Hypnotist(this);
    }

    @Override
    public JobType getJobType() {
        return JobType.HYPNOTIST;
    }

    @Override
    protected SkillResult skillAction() {
        // TODO skill 구현
        return null;
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.IMMEDIATE;
    }

    @Override
    public boolean isSkillSetApproved(GameStatus gameStatus) {
        return gameStatus == GameStatus.NIGHT || gameStatus == GameStatus.MORNING;
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}
