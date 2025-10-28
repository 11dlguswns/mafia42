package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.JobType;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.PassiveJob;
import click.mafia42.job.server.SkillResult;

public class Politician extends PassiveJob {
    public Politician(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public SkillResult passiveAction() {
        SkillResult skillResult = new SkillResult();

        owner.getGameRoom().clearVotes();
        owner.addVisibleAllUser();
        skillResult.concat(new SkillResult(
                new MessageResult("정치인은 투표로 죽지 않습니다.", owner.getGameRoom().getPlayers())));

        return skillResult;
    }

    @Override
    public JobType getJobType() {
        return JobType.POLITICIAN;
    }
}
