package click.mafia42.job.server.mafia;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.SkillTriggerTime;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.SharedActiveType;
import click.mafia42.job.server.SkillJob;
import click.mafia42.job.server.SkillResult;

import java.util.Set;

public class Hostess extends SkillJob {
    public Hostess(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, true);
    }

    protected Hostess(Hostess hostess) {
        super(hostess);
    }

    @Override
    protected Job copyInternal() {
        return new Hostess(this);
    }

    @Override
    public JobType getJobType() {
        return JobType.HOSTESS;
    }

    @Override
    protected SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        if (owner.getVoteUser() != null) {
            isUseSkill = true;
            target = owner.getVoteUser();

            if (target.getJob().getJobType() == JobType.MAFIA) {
                owner.connectWithMafia();
                skillResult.concat(new SkillResult(
                        new MessageResult("접선했습니다.", owner.getGameRoom().findUsersByMafiaTeam())));
                return skillResult;
            }

            target.seduced();
            skillResult.concat(new SkillResult(new MessageResult(target.getUser().getNickname() + "님을 유혹하였습니다!", Set.of(owner))));
            skillResult.concat(new SkillResult(new MessageResult("다른 플레이어에게 유혹당했습니다!", Set.of(target))));
        }

        return skillResult;
    }

    @Override
    public boolean isSkillTriggerTime(SkillTriggerTime skillTriggerTime) {
        return skillTriggerTime == SkillTriggerTime.END_OF_VOTING;
    }

    @Override
    public boolean isSkillSetApproved(GameStatus gameStatus) {
        return false;
    }

    @Override
    public boolean isValidTarget(GameUserStatus gameUserStatus) {
        return gameUserStatus == GameUserStatus.ALIVE;
    }
}
