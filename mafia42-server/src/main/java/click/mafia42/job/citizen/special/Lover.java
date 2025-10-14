package click.mafia42.job.citizen.special;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

public class Lover implements Job {
    @Override
    public boolean requiredCompanion() {
        return true;
    }

    @Override
    public JobType getJobType() {


        return JobType.LOVER;
    }
}
