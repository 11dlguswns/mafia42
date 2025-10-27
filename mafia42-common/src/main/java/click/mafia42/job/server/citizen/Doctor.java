package click.mafia42.job.server.citizen;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameStatus;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.server.ActiveJob;
import click.mafia42.job.JobType;
import click.mafia42.job.server.MessageResult;
import click.mafia42.job.server.SkillResult;
import click.mafia42.job.SkillTriggerTime;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends ActiveJob {
    public Doctor(GameRoomUser owner) {
        super(owner);
    }

    @Override
    public JobType getJobType() {
        return JobType.DOCTOR;
    }

    @Override
    public SkillResult skillAction() {
        List<MessageResult> messageResults = new ArrayList<>();

        GameRoomUser mafiaTarget = owner.getGameRoom().findMafiaTarget();
        if (target != null && target.equals(mafiaTarget)) {
            messageResults.add(new MessageResult(target.getUser().getNickname() + "님이 의사의 치료를 받았습니다.",
                    owner.getGameRoom().getPlayers()));
            return new SkillResult(messageResults);
        }

        return null;
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