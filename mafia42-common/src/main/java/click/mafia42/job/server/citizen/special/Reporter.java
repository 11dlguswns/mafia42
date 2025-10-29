package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.*;

public class Reporter extends SkillJob {
    public Reporter(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, false);
    }

    @Override
    public JobType getJobType() {
        return JobType.REPORTER;
    }

    @Override
    public SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

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
        return gameStatus == GameStatus.NIGHT && target == null;
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}