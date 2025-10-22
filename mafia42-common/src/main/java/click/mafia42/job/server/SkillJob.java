package click.mafia42.job.server;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;

public abstract class SkillJob extends Job {
    protected GameRoomUser target;
    protected JobType skillJobType;

    protected SkillJob(GameRoomUser owner) {
        super(owner);
    }

    protected SkillResult setSkillTarget(GameRoomUser target, JobType skillJobType) {
        if (getOwner().getStatus() == GameUserStatus.DIE) {
            throw new GlobalException(GlobalExceptionCode.SKILL_USE_NOT_ALLOWED);
        }
        if (!isSkillSetApproved(target.getGameRoom().getStatus())) {
            throw new GlobalException(GlobalExceptionCode.SKILL_USE_NOT_ALLOWED);
        }
        if (!isValidTarget(target.getStatus())) {
            throw new GlobalException(GlobalExceptionCode.INVALID_SKILL_TARGET);
        }

        this.target = target;
        this.skillJobType = skillJobType;

        if (isSkillTriggerTime(SkillTriggerTime.IMMEDIATE)) {
            return useSkill();
        }

        return null;
    }

    public SkillResult useSkill() {
        if (getOwner().getStatus() == GameUserStatus.DIE) {
            return null;
        }

        SkillResult actionResult = skillAction();
        clearSkillAction();

        return actionResult;
    }

    public GameRoomUser getTarget() {
        return target;
    }

    abstract public SkillResult setSkill(GameRoomUser target, JobType skillJobType);

    public void clearSkill() {
        target = null;
        skillJobType = null;
    }

    abstract public SkillResult skillAction();

    abstract public void clearSkillAction();

    abstract public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime);

    abstract public boolean isSkillSetApproved(GameStatus gameStatus);

    abstract public boolean isValidTarget(GameUserStatus gameUserStatus);
}
