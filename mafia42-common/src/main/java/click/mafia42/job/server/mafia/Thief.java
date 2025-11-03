package click.mafia42.job.server.mafia;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;

import java.util.Set;

public class Thief extends SkillJob {
    private Job stealJob;
    private boolean hasUsedPoliticianAbility;

    public Thief(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, true);
    }

    protected Thief(Thief thief) {
        super(thief);
        this.stealJob = thief.stealJob;
    }

    @Override
    protected Job copyInternal() {
        return new Thief(this);
    }

    public Job getStealJob() {
        return stealJob;
    }

    @Override
    public JobType getJobType() {
        return JobType.THIEF;
    }

    @Override
    protected SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        if (stealJob != null) {
            if (stealJob instanceof SkillJob skillJob) {
                return skillJob.useSkill();
            } else {
                return skillResult;
            }
        }

        if (target == null || target.getJob().getJobType() == JobType.THIEF) {
            return skillResult;
        }

        target.addVisibleToUserId(owner.getUser().getId());

        if (target.getJob().getJobType() == JobType.SOLDIER) {
            owner.addVisibleToUserId(target.getUser().getId());
            skillResult.concat(new SkillResult(new MessageResult("훔치는 데 실패했습니다.", Set.of(owner))));
            skillResult.concat(new SkillResult(new MessageResult(
                    owner.getUser().getNickname() + "님이 직업을 훔치려고 시도했습니다.", Set.of(target))));
            return skillResult;
        }

        stealJob = target.getJob().copy(owner);

        if (!owner.isContacted() && target.getJob().getJobType() == JobType.MAFIA) {
            owner.connectWithMafia();
            skillResult.concat(new SkillResult(
                    new MessageResult("접선했습니다.", owner.getGameRoom().findUsersByMafiaTeam())));
            return skillResult;
        }

        skillResult.concat(new SkillResult(
                new MessageResult(
                        String.format("%s님의 직업 %s을 훔쳤습니다.",
                                target.getUser().getNickname(), target.getJob().getJobType().getAlias()),
                        Set.of(owner))
        ));

        return skillResult;
    }

    @Override
    public GameRoomUser getTarget() {
        if (stealJob != null) {
            if (stealJob instanceof SkillJob skillJob) {
                return skillJob.getTarget();
            } else {
                return null;
            }
        }

        return super.getTarget();
    }

    @Override
    public SharedActiveType getSharedActiveType() {
        if (stealJob != null) {
            if (stealJob instanceof SkillJob skillJob) {
                return skillJob.getSharedActiveType();
            } else {
                return SharedActiveType.NONE;
            }
        }

        return super.getSharedActiveType();
    }

    @Override
    public SkillResult setSkill(GameRoomUser target, JobType skillJobType) {
        if (stealJob != null) {
            if (stealJob instanceof SkillJob skillJob) {
                return skillJob.setSkill(target, skillJobType);
            } else {
                return new SkillResult();
            }
        }

        return super.setSkill(target, skillJobType);
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        if (stealJob != null && stealJob instanceof SkillJob skillJob) {
            return skillJob.isSkillTriggerTime(skillTriggerTime);
        }

        return skillTriggerTime == SkillTriggerTime.IMMEDIATE;
    }

    @Override
    public boolean isSkillSetApproved(GameStatus gameStatus) {
        if (stealJob != null && stealJob instanceof SkillJob skillJob) {
            return skillJob.isSkillSetApproved(gameStatus);
        }

        return gameStatus == GameStatus.VOTING && target == null;
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        if (stealJob != null && stealJob instanceof SkillJob skillJob) {
            return skillJob.isValidTarget(gameUserStatus);
        }

        return gameUserStatus == GameUserStatus.ALIVE;
    }

    @Override
    protected void clearSkillAction() {
        super.clearSkillAction();
        if (owner.getGameRoom().getStatus() == GameStatus.MORNING) {
            this.stealJob = null;
        }
    }

    public boolean hasUsedPoliticianAbility() {
        return hasUsedPoliticianAbility;
    }

    public void usedPoliticianAbility() {
        this.hasUsedPoliticianAbility = true;
    }
}

