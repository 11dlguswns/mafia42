package click.mafia42.job.mafia;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

import java.util.UUID;

public class Thief implements Job {
    private Job stealJob;

    public Job getStealJob() {
        return stealJob;
    }

    @Override
    public JobType getJobType() {
        return JobType.THIEF;
    }

    @Override
    public void skill(UUID userId, JobType jobType) {
        // TODO skill 구현
        Job.super.skill(userId, jobType);
    }
}

