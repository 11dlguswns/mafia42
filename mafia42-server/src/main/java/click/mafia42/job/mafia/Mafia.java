package click.mafia42.job.mafia;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

import java.util.UUID;

public class Mafia implements Job {
    @Override
    public JobType getJobType() {
        return JobType.MAFIA;
    }

    @Override
    public void skill(UUID userId, JobType jobType) {
        // TODO skill 구현
        Job.super.skill(userId, jobType);
    }
}
