package click.mafia42.job.citizen;

import click.mafia42.job.Job;
import click.mafia42.job.JobType;

public class Agent implements Job {
    @Override
    public JobType getJobType() {
        return JobType.AGENT;
    }
}