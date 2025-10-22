package click.mafia42.dto.server;

import click.mafia42.job.JobType;

import java.util.UUID;

public record UseJobSkillReq(
        UUID userId,
        JobType jobType
) {
}
