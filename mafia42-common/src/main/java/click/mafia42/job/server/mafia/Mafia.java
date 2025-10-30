package click.mafia42.job.server.mafia;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;
import click.mafia42.job.SkillTriggerTime;

public class Mafia extends SkillJob {
    public Mafia(GameRoomUser owner) {
        super(owner, SharedActiveType.MAFIA, true);
    }

    @Override
    public JobType getJobType() {
        return JobType.MAFIA;
    }

    @Override
    protected SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        if (target == null) {
            return skillResult;
        }

        skillResult.concat(target.getGameRoom().dieUser(target));

        isUseSkill = true;
        return skillResult;
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.SPECIAL;
    }

    @Override
    protected boolean isSkillSetApproved(GameStatus gameStatus) {
        return gameStatus == GameStatus.NIGHT;
    }

    @Override
    protected boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}
