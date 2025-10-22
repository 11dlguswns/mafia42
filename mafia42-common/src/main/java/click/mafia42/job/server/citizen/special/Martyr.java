package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.server.ActiveJob;
import click.mafia42.job.JobType;
import click.mafia42.job.server.SkillResult;
import click.mafia42.job.SkillTriggerTime;

import java.util.Optional;

public class Martyr extends ActiveJob {
    public Martyr(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public JobType getJobType() {
        return JobType.MARTYR;
    }

    @Override
    public SkillResult skillAction() {
        // TODO skill 구현
        return null;
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.ON_DEATH;
    }

    @Override
    public boolean isSkillSetApproved(GameStatus gameStatus) {
        Optional<GameRoomUser> mostVotedUser = getOwner().getGameRoom().getMostVotedUser();
        boolean isOwnerMostVoted = mostVotedUser.isPresent() && mostVotedUser.get().equals(getOwner());
        return gameStatus == GameStatus.NIGHT || (gameStatus.isAfterVoting() && isOwnerMostVoted);
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}