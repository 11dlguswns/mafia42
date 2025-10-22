package click.mafia42.job.server;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.JobType;

public abstract class ActiveJob extends SkillJob {
    public ActiveJob(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public SkillResult setSkill(GameRoomUser target, JobType skillJobType) {
        return setSkillTarget(target, skillJobType);
    }

    @Override
    public void clearSkillAction() {
        clearSkill();
    }
}
