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

import java.util.Set;

public class Detective extends SkillJob {
    public Detective(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, true);
    }

    protected Detective(Detective detective) {
        super(detective);
    }

    @Override
    protected Job copyInternal() {
        return new Detective(this);
    }

    @Override
    public JobType getJobType() {
        return JobType.DETECTIVE;
    }

    @Override
    protected SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        if (target == null) {
            return skillResult;
        }

        isUseSkill = true;
        skillResult.concat(new SkillResult(
                new MessageResult(target.getUser().getNickname() + "님을 상대로 수사를 진행합니다.", Set.of(owner))));

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