package click.mafia42.job.mafia;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

public class MadScientist implements Job {
    @Override
    public JobType getJobType() {
        return JobType.MAD_SCIENTIST;
    }
}
