package click.mafia42.job.server.citizen.special;

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

public class Reporter extends SkillJob {
    public Reporter(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, false);
    }

    protected Reporter(Reporter reporter) {
        super(reporter);
    }

    @Override
    protected Job copyInternal() {
        return new Reporter(this);
    }

    @Override
    public JobType getJobType() {
        return JobType.REPORTER;
    }

    @Override
    protected SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        if (target == null) {
            return skillResult;
        }

        isUseSkill = true;
        target.addVisibleAllUser();
        skillResult.concat(new SkillResult(
                new MessageResult(String.format("특종입니다! %s님이 %s(이)라는 소식입니다!",
                        target.getUser().getNickname(),
                        target.getJob().getJobType().getAlias()
                ), owner.getGameRoom().getPlayers())));

        return skillResult;
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.START_OF_MORNING;
    }

    @Override
    public boolean isSkillSetApproved(GameStatus gameStatus) {
        return gameStatus == GameStatus.NIGHT;
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}