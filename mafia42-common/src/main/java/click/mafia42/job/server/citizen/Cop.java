package click.mafia42.job.server.citizen;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;

import java.util.Set;

public class Cop extends SkillJob {
    public Cop(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, true);
    }

    @Override
    public JobType getJobType() {
        return JobType.COP;
    }

    @Override
    public SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        if (target.getJob().getJobType() == JobType.MAFIA) {
            target.addVisibleToUserId(owner.getUser().getId());
            skillResult.concat(new SkillResult(
                    new MessageResult(
                            target.getUser().getNickname() + "님은 마피아입니다.",
                            Set.of(owner))));
            return skillResult;
        }

        skillResult.concat(new SkillResult(
                new MessageResult(
                        target.getUser().getNickname() + "님은 마피아가 아닙니다.",
                        Set.of(owner))));
        return skillResult;
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.IMMEDIATE;
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