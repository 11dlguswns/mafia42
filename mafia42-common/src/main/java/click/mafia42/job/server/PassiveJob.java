package click.mafia42.job.server;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameUserStatus;
import click.mafia42.job.Job;

public abstract class PassiveJob extends Job {
    protected PassiveJob(GameRoomUser owner) {
        super(owner);
    }

    public SkillResult usePassive() {
        if (getOwner().getStatus() == GameUserStatus.DIE) {
            return null;
        }

        return passiveAction();
    }

    abstract public SkillResult passiveAction();
}
