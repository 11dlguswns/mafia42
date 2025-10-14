package click.mafia42.job.citizen.special;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

import java.util.UUID;

public class Magician implements Job {
    @Override
    public JobType getJobType() {
        return JobType.MAGICIAN;
    }

    @Override
    public void skill(UUID userId, JobType jobType) {
        // TODO skill 구현
        Job.super.skill(userId, jobType);
    }
}