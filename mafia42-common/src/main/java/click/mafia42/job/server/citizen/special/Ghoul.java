package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.Team;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.PassiveJob;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillResult;
import click.mafia42.job.server.citizen.Citizen;
import click.mafia42.job.server.mafia.Villan;

import java.util.Set;

public class Ghoul extends PassiveJob {
    public Ghoul(GameRoomUser owner) {
        super(owner);
    }

    protected Ghoul(Ghoul ghoul) {
        super(ghoul);
    }

    @Override
    protected Job copyInternal() {
        return new Ghoul(this);
    }

    @Override
    public SkillResult passiveAction() {
        SkillResult skillResult = new SkillResult();

        GameRoomUser mafiaTarget = owner.getGameRoom().findSharedActiveTarget(SharedActiveType.MAFIA);
        if (mafiaTarget != null && mafiaTarget.getStatus() == GameUserStatus.DIE && owner.getGameRoom().getDay() == 1) {
            owner.updateJob(mafiaTarget.getJob());
            owner.addVisibleToUserId(mafiaTarget.getUser().getId());
            skillResult.concat(new SkillResult(
                    new MessageResult(
                            mafiaTarget.getJob().getJobType().getAlias() + "직업을 획득하였습니다.",
                            Set.of(owner))));


            if (mafiaTarget.getTeam() == Team.CITIZEN) {
                mafiaTarget.updateJob(new Citizen(mafiaTarget));
            } else {
                mafiaTarget.updateJob(new Villan(mafiaTarget));
            }
            mafiaTarget.addVisibleToUserId(owner.getUser().getId());
            skillResult.concat(new SkillResult(
                    new MessageResult(
                            String.format("도굴꾼에게 도굴당해 %s이 되었습니다.", mafiaTarget.getJob().getJobType().getAlias()),
                            Set.of(mafiaTarget))));
        }

        return skillResult;
    }

    @Override
    public JobType getJobType() {
        return JobType.GHOUL;
    }
}