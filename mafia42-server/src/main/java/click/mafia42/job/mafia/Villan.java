package click.mafia42.job.mafia;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

public class Villan implements Job {
    @Override
    public JobType getJobType() {
        return JobType.VILLAN;
    }
}
