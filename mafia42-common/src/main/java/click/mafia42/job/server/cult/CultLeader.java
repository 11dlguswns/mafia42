package click.mafia42.job.server.cult;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;

public class CultLeader extends SkillJob {
    public CultLeader(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, true);
    }

    protected CultLeader(CultLeader cultLeader) {
        super(cultLeader);
    }

    @Override
    protected Job copyInternal() {
        return new CultLeader(this);
    }

    @Override
    public JobType getJobType() {
        return JobType.CULT_LEADER;
    }

    @Override
    protected SkillResult skillAction() {
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
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}