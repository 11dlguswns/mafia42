package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.PassiveJob;
import click.mafia42.job.server.SkillResult;

import java.util.Set;

public class Lover extends PassiveJob {
    public Lover(GameRoomUser owner) {
        super(owner);
    }

    protected Lover(Lover lover) {
        super(lover);
    }

    @Override
    protected Job copyInternal() {
        return new Lover(this);
    }

    @Override
    public SkillResult passiveAction() {
        SkillResult skillResult = new SkillResult();

        Set<GameRoomUser> lovers = owner.getGameRoom().findUsersByJobType(JobType.LOVER);
        GameRoomUser partner = lovers.stream()
                .filter(gUser -> !gUser.equals(owner))
                .findFirst()
                .orElse(null);

        if (partner == null) {
            return skillResult;
        }

        if (owner.getStatus() == GameUserStatus.ALIVE && partner.getStatus() == GameUserStatus.ALIVE) {
            partner.die();
            owner.addVisibleAllUser();
            partner.addVisibleAllUser();
            skillResult.concat(new SkillResult(
                    new MessageResult(String.format("%s님이 %s을(를) 살리고 대신 마피아에게 살해당했습니다!",
                            partner.getUser().getNickname(),
                            owner.getUser().getNickname()
                    ), owner.getGameRoom().getPlayers())
            ));
            return skillResult;
        }

        return skillResult;
    }

    @Override
    public JobType getJobType() {
        return JobType.LOVER;
    }
}
