package click.mafia42.job.server.citizen;

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

public class Doctor extends SkillJob {
    public Doctor(GameRoomUser owner) {
        super(owner, SharedActiveType.NONE, true);
    }

    protected Doctor(Doctor doctor) {
        super(doctor);
    }

    @Override
    protected Job copyInternal() {
        return new Doctor(this);
    }

    @Override
    public JobType getJobType() {
        return JobType.DOCTOR;
    }

    @Override
    protected SkillResult skillAction() {
        SkillResult skillResult = new SkillResult();

        if (target == null) {
            return skillResult;
        }

        isUseSkill = true;
        GameRoomUser mafiaTarget = owner.getGameRoom().findSharedActiveTarget(SharedActiveType.MAFIA);
        if (target.equals(mafiaTarget)) {
            skillResult.concat(
                    new SkillResult(new MessageResult(target.getUser().getNickname() + "님이 의사의 치료를 받았습니다.",
                            owner.getGameRoom().getPlayers())));
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