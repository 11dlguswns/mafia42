package click.mafia42.job.server.mafia;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.*;
import click.mafia42.job.server.ActiveJob;
import click.mafia42.job.server.SkillResult;

public class Thief extends ActiveJob {
    private Job stealJob;

    public Thief(GameRoomUser owner) {
        super(owner);
    }

    public Job getStealJob() {
        return stealJob;
    }

    @Override
    public JobType getJobType() {
        return JobType.THIEF;
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

