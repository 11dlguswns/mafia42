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
    protected SharedActiveType sharedActiveType;

    protected SkillJob(GameRoomUser owner, SharedActiveType sharedActiveType) {
        super(owner);
        this.sharedActiveType = sharedActiveType;
    }

    protected SkillResult setSkillTarget(GameRoomUser target, JobType skillJobType) {
        if (getOwner().getStatus() == GameUserStatus.DIE) {
            throw new GlobalException(GlobalExceptionCode.SKILL_USE_NOT_ALLOWED);
        }

        this.target = target;
        this.skillJobType = skillJobType;

        if (isSkillTriggerTime(SkillTriggerTime.IMMEDIATE)) {
            return useSkill();
        }

        return new SkillResult();
    }

    public SkillResult useSkill() {
        if (getOwner().getStatus() == GameUserStatus.DIE) {
            return new SkillResult();
        }
        if (owner.isSeduced()) {
            return new SkillResult();
        }

        SkillResult actionResult = skillAction();
        clearSkillAction();

        return actionResult;
    }

    public GameRoomUser getTarget() {
        return target;
    }

    public SharedActiveType getSharedActiveType() {
        return sharedActiveType;
    }

    public SkillResult setSkill(GameRoomUser target, JobType skillJobType) {
        if (!isSkillSetApproved(target.getGameRoom().getStatus())) {
            throw new GlobalException(GlobalExceptionCode.SKILL_USE_NOT_ALLOWED);
        }
        if (!isValidTarget(target.getStatus())) {
            throw new GlobalException(GlobalExceptionCode.INVALID_SKILL_TARGET);
        }
        if (owner.isSeduced()) {
            throw new GlobalException(GlobalExceptionCode.SEDUCED_CANNOT_USE_SKILL);
        }

        if (sharedActiveType == SharedActiveType.NONE) {
            return setSkillTarget(target, skillJobType);
        }

        for (GameRoomUser gUser : owner.getGameRoom().getPlayers()) {
            if (gUser.getJob() instanceof SkillJob skillJob) {
                if (skillJob.sharedActiveType != sharedActiveType) {
                    continue;
                }
                if (skillJob.getOwner().getStatus() == GameUserStatus.DIE) {
                    continue;
                }

                return skillJob.setSkillTarget(target, skillJobType);
            }
        }

        return new SkillResult();
    }

    public void clearSkill() {
        target = null;
        skillJobType = null;
    }

    abstract public SkillResult skillAction();

    public void clearSkillAction() {
        if (sharedActiveType == SharedActiveType.NONE) {
            clearSkill();
            return;
        }

        for (GameRoomUser gUser : owner.getGameRoom().getPlayers()) {
            if (gUser.getJob() instanceof SkillJob skillJob) {
                if (skillJob.sharedActiveType != sharedActiveType) {
                    continue;
                }

                skillJob.clearSkill();
            }
        }
    }

    abstract public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime);

    abstract public boolean isSkillSetApproved(GameStatus gameStatus);

    abstract public boolean isValidTarget(GameUserStatus gameUserStatus);
}
