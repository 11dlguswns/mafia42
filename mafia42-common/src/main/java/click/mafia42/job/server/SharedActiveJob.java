package click.mafia42.job.server;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.JobType;

import java.util.List;

public abstract class SharedActiveJob extends SkillJob {
    public SharedActiveJob(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public SkillResult setSkill(GameRoomUser target, JobType skillJobType) {
        SkillResult skillResult = null;
        List<GameRoomUser> gameRoomUserStream = target.getGameRoom().getPlayers().stream()
                .filter(gUser -> getJobType() == gUser.getJob().getJobType())
                .toList();

        for (GameRoomUser gUser : gameRoomUserStream) {
            if (gUser.getJob() instanceof SkillJob skillJob) {
                skillResult = skillJob.setSkillTarget(target, skillJobType);
            }
        }

        return skillResult;
    }

    @Override
    public void clearSkillAction() {
        List<GameRoomUser> gameRoomUserStream = target.getGameRoom().getPlayers().stream()
                .filter(gUser -> getJobType() == gUser.getJob().getJobType())
                .toList();

        for (GameRoomUser gUser : gameRoomUserStream) {
            if (gUser.getJob() instanceof SkillJob skillJob) {
                skillJob.clearSkill();
            }
        }
    }
}
