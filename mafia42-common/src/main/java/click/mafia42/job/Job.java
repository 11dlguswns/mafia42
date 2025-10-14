package click.mafia42.job;

import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;

import java.util.UUID;

public interface Job {
    JobType getJobType();

    default void skill(UUID userId, JobType jobType) {
        throw new GlobalException(GlobalExceptionCode.SKILL_NOT_AVAILABLE);
    }

    default boolean requiredCompanion() {
        return false;
    }
}
