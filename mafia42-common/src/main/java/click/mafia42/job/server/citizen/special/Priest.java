package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;

public class Priest extends SkillJob {
    public Priest(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, false);
    }

    @Override
    public JobType getJobType() {
        return JobType.PRIEST;
    }

    @Override
    protected SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        if (target == null) {
            return skillResult;
        }

        isUseSkill = true;
        target.resurrection();
        skillResult.concat(new SkillResult(
                new MessageResult(
                        target.getUser().getNickname() + "님이 부활하셨습니다.",
                        getOwner().getGameRoom().getPlayers())));

        return skillResult;
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.START_OF_MORNING;
    }

    @Override
    protected boolean isSkillSetApproved(GameStatus gameStatus) {
        return gameStatus == GameStatus.NIGHT;
    }

    @Override
    protected boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.DIE;
    }
}