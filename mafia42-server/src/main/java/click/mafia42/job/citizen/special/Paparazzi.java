package click.mafia42.job.citizen.special;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

public class Paparazzi implements Job {
    @Override
    public JobType getJobType() {
        return JobType.PAPARAZZI;
    }
}
