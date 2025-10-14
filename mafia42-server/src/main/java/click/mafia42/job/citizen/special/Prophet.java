package click.mafia42.job.citizen.special;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

public class Prophet implements Job {
    @Override
    public JobType getJobType() {
        return JobType.PROPHET;
    }
}