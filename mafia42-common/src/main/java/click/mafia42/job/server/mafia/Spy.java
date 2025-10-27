package click.mafia42.job.server.mafia;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.ActiveJob;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.SkillResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Spy extends ActiveJob {
    public Spy(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public JobType getJobType() {
        return JobType.SPY;
    }

    @Override
    public SkillResult skillAction() {
        List<MessageResult> messageResults = new ArrayList<>();
        target.addVisibleToUserId(owner.getUser().getId());

        if (target.getJob().getJobType() == JobType.MAFIA) {
            owner.connectWithMafia();
            messageResults.add(new MessageResult("접선했습니다.", owner.getGameRoom().findUsersByMafiaTeam()));
            return new SkillResult(messageResults);
        }

        if (target.getJob().getJobType() == JobType.SOLDIER) {
            messageResults.add(new MessageResult(
                    String.format("스파이 %s님이 당신을 조사했습니다.", owner.getUser().getNickname()),
                    Set.of(target)));
            owner.addVisibleToUserId(target.getUser().getId());
        }

        target.addVisibleToUserId(owner.getUser().getId());
        messageResults.add(new MessageResult("그 사람의 직업은 " + target.getJob().getJobType().getAlias(),
                Set.of(owner)));
        return new SkillResult(messageResults);
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.IMMEDIATE;
    }

    @Override
    public boolean isSkillSetApproved(GameStatus gameStatus) {
        return gameStatus == GameStatus.NIGHT && target == null;
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}
