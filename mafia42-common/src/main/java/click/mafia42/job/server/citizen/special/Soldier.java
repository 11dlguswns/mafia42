package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.job.JobType;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.PassiveJob;
import click.mafia42.job.server.SkillResult;

import java.util.ArrayList;
import java.util.List;

public class Soldier extends PassiveJob {
    private boolean isBulletBlocked = false;

    public Soldier(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public SkillResult passiveAction() {
        List<MessageResult> messageResults = new ArrayList<>();

        if (!isBulletBlocked) {
            owner.addVisibleAllUser();
            isBulletBlocked = true;

            messageResults.add(new MessageResult(
                    String.format("군인 %s님이 공격을 버텨냈습니다.", owner.getUser().getNickname()),
                    owner.getGameRoom().getPlayers()));
            return new SkillResult(messageResults);
        }

        return null;
    }

    @Override
    public JobType getJobType() {
        return JobType.SOLDIER;
    }
}
