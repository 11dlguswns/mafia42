package click.mafia42.job.server.mafia;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;

public class BeastMan extends SkillJob {
    public BeastMan(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE);
    }

    @Override
    public JobType getJobType() {
        return JobType.BEAST_MAN;
    }

    @Override
    public SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        if (owner.equals(owner.getGameRoom().findSharedActiveTarget(SharedActiveType.MAFIA))) {
            if (!owner.isContacted()) {
                owner.connectWithMafia();
                sharedActiveType = SharedActiveType.MAFIA;
                skillResult.concat(new SkillResult(
                        new MessageResult("길들여졌습니다.", owner.getGameRoom().findUsersByMafiaTeam())));
            }

            return skillResult;
        }

        if (target != null && target.equals(owner.getGameRoom().findSharedActiveTarget(SharedActiveType.MAFIA))) {
            target.die();
            skillResult.concat(new SkillResult(new MessageResult(
                    target.getUser().getNickname() + "님이 짐승에게 습격당하였습니다.",
                    owner.getGameRoom().getPlayers())));

            if (!owner.isContacted()) {
                owner.connectWithMafia();
                sharedActiveType = SharedActiveType.MAFIA;
                skillResult.concat(new SkillResult(
                        new MessageResult("길들여졌습니다.", owner.getGameRoom().findUsersByMafiaTeam())));
            }
            return skillResult;
        }

        return skillResult;
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.SPECIAL;
    }

    @Override
    public boolean isSkillSetApproved(GameStatus gameStatus) {
        return gameStatus == GameStatus.NIGHT;
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}
