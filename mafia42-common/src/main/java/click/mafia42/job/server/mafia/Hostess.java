package click.mafia42.job.server.mafia;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.server.ActiveJob;
import click.mafia42.job.JobType;
import click.mafia42.job.server.SkillResult;
import click.mafia42.job.SkillTriggerTime;

public class Hostess extends ActiveJob {
    public Hostess(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public JobType getJobType() {
        return JobType.HOSTESS;
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
        return gameStatus == GameStatus.VOTING;
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}
