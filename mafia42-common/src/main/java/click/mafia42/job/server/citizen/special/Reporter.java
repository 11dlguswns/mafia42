package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;

public class Reporter extends SkillJob {
    public Reporter(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE);
    }

    @Override
    public JobType getJobType() {
        return JobType.REPORTER;
    }

    @Override
    public SkillResult skillAction() {
        // TODO skill 구현
        return null;
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