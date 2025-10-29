package click.mafia42.util;

import click.mafia42.entity.room.GameRoomUser;
import click.mafia42.entity.room.GameType;
import click.mafia42.job.Job;
import click.mafia42.job.JobType;
import click.mafia42.job.Role;
import click.mafia42.job.server.citizen.*;
import click.mafia42.job.server.citizen.special.*;
import click.mafia42.job.server.cult.CultLeader;
import click.mafia42.job.server.cult.Fanatic;
import click.mafia42.job.server.mafia.*;

import java.util.*;

public class GameUtil {
    public static ArrayDeque<JobType> getJob(int userCount, GameType gameType) {
        List<Role> roles = getRoles(userCount, gameType);
        ArrayDeque<JobType> mafiaAssistanceJobs = getMafiaAssistanceJobs(gameType);
        ArrayDeque<JobType> policeJobs = getPoliceJobs(gameType);
        long spatialCitizenCount = roles.stream().filter(role -> role == Role.SPECIAL_CITIZEN).count();
        ArrayDeque<JobType> spatialCitizenJobs = getSpatialCitizenJobs((int) spatialCitizenCount, gameType);

        return new ArrayDeque<>(roles.stream()
                .map(role ->
                        switch (role) {
                            case MAFIA -> JobType.MAFIA;
                            case MAFIA_ASSISTANCE -> mafiaAssistanceJobs.poll();
                            case CULT_LEADER -> JobType.CULT_LEADER;
                            case CULT_ASSISTANCE -> JobType.FANATIC;
                            case POLICE -> policeJobs.poll();
                            case DOCTOR -> JobType.DOCTOR;
                            case CITIZEN -> JobType.CITIZEN;
                            case SPECIAL_CITIZEN -> spatialCitizenJobs.poll();
                        }
                ).toList());
    }

    public static List<Role> getRoles(int userCount, GameType gameType) {
        List<Role> roles = new ArrayList<>(userCount);

        fillRoleWithMafia(userCount, roles);
        fillRoleWithCult(userCount, gameType, roles);
        fillWithCitizen(userCount, roles);
        Collections.shuffle(roles);

        return roles;
    }

    private static void fillRoleWithMafia(int userCount, List<Role> roles) {
        if (4 <= userCount && userCount <= 5) {
            roles.add(Role.MAFIA);
        } else if (6 <= userCount && userCount <= 7) {
            roles.add(Role.MAFIA);
            roles.add(Role.MAFIA_ASSISTANCE);
        } else if (8 <= userCount && userCount <= 10) {
            roles.add(Role.MAFIA);
            roles.add(Role.MAFIA);
            roles.add(Role.MAFIA_ASSISTANCE);
        } else if (11 <= userCount && userCount <= 12) {
            roles.add(Role.MAFIA);
            roles.add(Role.MAFIA);
            roles.add(Role.MAFIA);
            roles.add(Role.MAFIA_ASSISTANCE);
        }
    }

    private static void fillRoleWithCult(int userCount, GameType gameType, List<Role> roles) {
        if (gameType == GameType.CULT) {
            if (9 <= userCount) {
                roles.add(Role.CULT_LEADER);
            }
            if (12 <= userCount) {
                roles.add(Role.CULT_ASSISTANCE);
            }
        } else {
            if (12 <= userCount) {
                roles.add(Role.CITIZEN);
            }
        }
    }

    private static void fillWithCitizen(int userCount, List<Role> roles) {
        roles.add(Role.POLICE);
        roles.add(Role.DOCTOR);

        for (int jobCount = roles.size(); jobCount < userCount; jobCount++) {
            roles.add(Role.SPECIAL_CITIZEN);
        }
    }

    private static ArrayDeque<JobType> getMafiaAssistanceJobs(GameType gameType) {
        ArrayList<JobType> mafiaAssistanceList = new ArrayList<>(
                switch (gameType) {
                    case CLASSIC -> getMafiaAssistanceJobsByClassic();
                    case DUAL, CULT -> getDefaultMafiaAssistanceJobs();
                });

        Collections.shuffle(mafiaAssistanceList);
        return new ArrayDeque<>(mafiaAssistanceList);
    }

    private static ArrayDeque<JobType> getPoliceJobs(GameType gameType) {
        ArrayList<JobType> policeJobs = new ArrayList<>(
                switch (gameType) {
                    case CLASSIC -> Set.of(JobType.COP);
                    case DUAL, CULT -> getDefaultPoliceJobs();
                });

        Collections.shuffle(policeJobs);
        return new ArrayDeque<>(policeJobs);
    }

    private static ArrayDeque<JobType> getSpatialCitizenJobs(int spatialCitizenCount, GameType gameType) {
        List<JobType> allSpatialCitizenJobs = new ArrayList<>(
                switch (gameType) {
                    case CLASSIC -> getSpatialCitizenJobsByClassic();
                    case DUAL, CULT -> getDefaultSpatialCitizenJobs();
                });

        Collections.shuffle(allSpatialCitizenJobs);
        List<JobType> selectedJobs = selectJobsWithCompanion(spatialCitizenCount, allSpatialCitizenJobs);

        Collections.shuffle(selectedJobs);
        return new ArrayDeque<>(selectedJobs);
    }

    private static List<JobType> selectJobsWithCompanion(int count, List<JobType> allJobs) {
        ArrayDeque<JobType> allJobsDeque = new ArrayDeque<>(allJobs);

        List<JobType> selectedJobs = new ArrayList<>(count);

        while (selectedJobs.size() < count && !allJobsDeque.isEmpty()) {
            JobType jobType = allJobsDeque.poll();

            if (jobType.isRequiredCompanion()) {
                if (selectedJobs.size() + 1 < count) {
                    selectedJobs.add(jobType);
                    selectedJobs.add(jobType);
                }
            } else {
                selectedJobs.add(jobType);
            }
        }
        return selectedJobs;
    }

    private static Set<JobType> getDefaultPoliceJobs() {
        return Set.of(
                JobType.COP,
                JobType.VIGILANTE,
                JobType.AGENT
        );
    }

    private static Set<JobType> getDefaultMafiaAssistanceJobs() {
        return Set.of(
                JobType.BEAST_MAN,
                JobType.HIT_MAN,
                JobType.HOSTESS,
                JobType.MAD_SCIENTIST,
                JobType.SPY,
                JobType.SWINDLER,
                JobType.THIEF,
                JobType.VILLAN,
                JobType.WITCH
        );
    }

    private static Set<JobType> getMafiaAssistanceJobsByClassic() {
        return Set.of(
                JobType.BEAST_MAN,
                JobType.HOSTESS,
                JobType.SPY,
                JobType.THIEF
        );
    }

    private static Set<JobType> getSpatialCitizenJobsByClassic() {
        return Set.of(
                JobType.REPORTER
        );
    }

    private static Set<JobType> getDefaultSpatialCitizenJobs() {
        return Set.of(
                JobType.ADMINISTRATOR,
                JobType.CABAL,
                JobType.DETECTIVE,
                JobType.FORTUNE_TELLER,
                JobType.GANGSTER,
                JobType.GHOUL,
                JobType.HACKER,
                JobType.JUDGE,
                JobType.LOVER,
                JobType.MAGICIAN,
                JobType.MARTYR,
                JobType.MENTALIST,
                JobType.MERCENARY,
                JobType.NURSE,
                JobType.PAPARAZZI,
                JobType.POLITICIAN,
                JobType.PRIEST,
                JobType.PROPHET,
                JobType.PSYCHIC,
                JobType.REPORTER,
                JobType.SOLDIER
        );
    }

    public static Job convertToJob(JobType jobType, GameRoomUser owner) {
        return switch (jobType) {
            case MAFIA -> new Mafia(owner);
            case VILLAN -> new Villan(owner);
            case BEAST_MAN -> new BeastMan(owner);
            case SPY -> new Spy(owner);
            case HOSTESS -> new Hostess(owner);
            case WITCH -> new Witch(owner);
            case MAD_SCIENTIST -> new MadScientist(owner);
            case THIEF -> new Thief(owner);
            case SWINDLER -> new Swindler(owner);
            case HIT_MAN -> new HitMan(owner);
            case CULT_LEADER -> new CultLeader(owner);
            case FANATIC -> new Fanatic(owner);
            case CITIZEN -> new Citizen(owner);
            case COP -> new Cop(owner);
            case VIGILANTE -> new Vigilante(owner);
            case AGENT -> new Agent(owner);
            case DOCTOR -> new Doctor(owner);
            case SOLDIER -> new Soldier(owner);
            case POLITICIAN -> new Politician(owner);
            case PSYCHIC -> new Psychic(owner);
            case LOVER -> new Lover(owner);
            case REPORTER -> new Reporter(owner);
            case GANGSTER -> new Gangster(owner);
            case DETECTIVE -> new Detective(owner);
            case GHOUL -> new Ghoul(owner);
            case MARTYR -> new Martyr(owner);
            case PRIEST -> new Priest(owner);
            case PROPHET -> new Prophet(owner);
            case JUDGE -> new Judge(owner);
            case NURSE -> new Nurse(owner);
            case MAGICIAN -> new Magician(owner);
            case HACKER -> new Hacker(owner);
            case MENTALIST -> new Mentalist(owner);
            case MERCENARY -> new Mercenary(owner);
            case ADMINISTRATOR -> new Administrator(owner);
            case CABAL -> new Cabal(owner);
            case PAPARAZZI -> new Paparazzi(owner);
            case HYPNOTIST -> new Hypnotist(owner);
            case FORTUNE_TELLER -> new FortuneTeller(owner);
        };
    }
}