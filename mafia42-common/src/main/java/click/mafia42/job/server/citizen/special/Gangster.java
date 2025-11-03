package click.mafia42.job.server.citizen.special;

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

public class Gangster extends SkillJob {
    public Gangster(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, true);
    }

    protected Gangster(Gangster gangster) {
        super(gangster);
    }

    @Override
    protected Job copyInternal() {
        return new Gangster(this);
    }

    @Override
    public JobType getJobType() {
        return JobType.GANGSTER;
    }

    @Override
    protected SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        if (target == null) {
            return skillResult;
        }

        isUseSkill = true;
        target.blackmailed();
        skillResult.concat(new SkillResult(
                new MessageResult(target.getUser().getNickname() + "님에게 위협을 가하였습니다.", Set.of(owner))));
        skillResult.concat(new SkillResult(
                new MessageResult("의문의 괴한으로부터 협박을 당했습니다.", Set.of(target))));

        return skillResult;
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