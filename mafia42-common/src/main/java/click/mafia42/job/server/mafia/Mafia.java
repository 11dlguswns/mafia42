package click.mafia42.job.server.mafia;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;
import click.mafia42.job.server.SharedActiveJob;
import click.mafia42.job.server.SkillResult;
import click.mafia42.job.SkillTriggerTime;

public class Mafia extends SharedActiveJob {
    public Mafia(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public JobType getJobType() {
        return JobType.MAFIA;
    }

    @Override
    public SkillResult skillAction() {
        if (target == null) {
            return null;
        }

        return target.getGameRoom().dieUser(target);
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.SPECIAL;
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
