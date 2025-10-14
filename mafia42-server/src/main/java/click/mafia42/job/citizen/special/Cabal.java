package click.mafia42.job.citizen.special;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

import java.util.UUID;

public class Cabal implements Job {
    @Override
    public boolean requiredCompanion() {
        return true;
    }

    @Override
    public JobType getJobType() {
        return JobType.CABAL;
    }

    @Override
    public void skill(UUID userId, JobType jobType) {
        // TODO skill 구현
        Job.super.skill(userId, jobType);
    }
}