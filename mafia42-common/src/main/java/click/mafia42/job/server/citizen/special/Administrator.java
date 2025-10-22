package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.server.ActiveJob;
import click.mafia42.job.JobType;
import click.mafia42.job.server.SkillResult;
import click.mafia42.job.SkillTriggerTime;

public class Administrator extends ActiveJob {
    public Administrator(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public JobType getJobType() {
        return JobType.ADMINISTRATOR;
    }

    @Override
    public SkillResult skillAction() {
        // TODO skill 구현
        return null;
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.IMMEDIATE;
    }

    @Override
    public boolean isSkillSetApproved(GameStatus gameStatus) {
        return gameStatus == GameStatus.NIGHT;
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        return true;
    }
}