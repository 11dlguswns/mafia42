package click.mafia42.job.citizen;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

import java.util.UUID;

public class Doctor implements Job {
    @Override
    public JobType getJobType() {
        return JobType.DOCTOR;
    }

    @Override
    public void skill(UUID userId, JobType jobType) {
        // TODO skill 구현
        Job.super.skill(userId, jobType);
    }
}