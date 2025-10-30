package click.mafia42.job.server.citizen.special;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;

import java.util.Optional;

public class Martyr extends SkillJob {
    public Martyr(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, true);
    }

    @Override
    public JobType getJobType() {
        return JobType.MARTYR;
    }

    @Override
    public SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        GameStatus gameStatus = owner.getGameRoom().getStatus();
        Optional<GameRoomUser> mostVotedUser = owner.getGameRoom().getMostVotedUser();
        boolean isMostVoted = mostVotedUser.isPresent() && mostVotedUser.get().equals(owner);

        if (gameStatus == GameStatus.MORNING) {
            GameRoomUser mafiaTarget = owner.getGameRoom().findSharedActiveTarget(SharedActiveType.MAFIA);

            boolean mafiaTargetIsOwner = mafiaTarget != null && mafiaTarget.equals(owner);
            boolean targetIsMafia = target != null && target.getJob().getJobType().equals(JobType.MAFIA);
            if (mafiaTargetIsOwner && targetIsMafia) {
                owner.die();
                target.die();
                target.addVisibleAllUser();
                owner.addVisibleAllUser();
                skillResult.concat(new SkillResult(
                        new MessageResult(
                                String.format("테러리스트 %s님이 %s님을 습격하였습니다.",
                                        owner.getUser().getNickname(), target.getUser().getNickname()),
                                owner.getGameRoom().getPlayers()
                        )
                ));
            }
        } else if (gameStatus == GameStatus.NIGHT && isMostVoted && owner.getGameRoom().isVotePassed()) {
            owner.die();
            target.die();
            owner.addVisibleAllUser();
            skillResult.concat(new SkillResult(
                    new MessageResult(
                            String.format("테러리스트 %s님이 %s님을 습격하였습니다.",
                                    owner.getUser().getNickname(), target.getUser().getNickname()),
                            owner.getGameRoom().getPlayers()
                    )
            ));
        }

        return skillResult;
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.SPECIAL;
    }

    @Override
    public boolean isSkillSetApproved(GameStatus gameStatus) {
        Optional<GameRoomUser> mostVotedUser = getOwner().getGameRoom().getMostVotedUser();
        boolean isOwnerMostVoted = mostVotedUser.isPresent() && mostVotedUser.get().equals(getOwner());
        return gameStatus == GameStatus.NIGHT ||
                gameStatus == GameStatus.CONTRADICT && (gameStatus.isAfterVoting() && isOwnerMostVoted);
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}