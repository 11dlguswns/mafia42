package click.mafia42.util;

import click.mafia42.entity.room.GameType;
import click.mafia42.job.Job;
import click.mafia42.job.Role;
import click.mafia42.job.citizen.*;
import click.mafia42.job.citizen.special.*;
import click.mafia42.job.cult.CultLeader;
import click.mafia42.job.cult.Fanatic;
import click.mafia42.job.mafia.*;

import java.util.*;

public class GameUtil {
    public static ArrayDeque<Job> getJob(int userCount, GameType gameType) {
        List<Role> roles = getRoles(userCount, gameType);
        ArrayDeque<Job> mafiaAssistanceJobs = getMafiaAssistanceJobs(gameType);
        ArrayDeque<Job> policeJobs = getPoliceJobs(gameType);
        long spatialCitizenCount = roles.stream().filter(role -> role == Role.SPECIAL_CITIZEN).count();
        ArrayDeque<Job> spatialCitizenJobs = getSpatialCitizenJobs((int) spatialCitizenCount, gameType);

        return new ArrayDeque<>(roles.stream()
                .map(role ->
                        switch (role) {
                            case MAFIA -> new Mafia();
                            case MAFIA_ASSISTANCE -> mafiaAssistanceJobs.poll();
                            case CULT_LEADER -> new CultLeader();
                            case CULT_ASSISTANCE -> new Fanatic();
                            case POLICE -> policeJobs.poll();
                            case DOCTOR -> new Doctor();
                            case CITIZEN -> new Citizen();
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

    private static ArrayDeque<Job> getMafiaAssistanceJobs(GameType gameType) {
        ArrayList<Job> mafiaAssistanceList = new ArrayList<>(
                switch (gameType) {
                    case CLASSIC -> getMafiaAssistanceJobsByClassic();
                    case DUAL, CULT -> getDefaultMafiaAssistanceJobs();
                });

        Collections.shuffle(mafiaAssistanceList);
        return new ArrayDeque<>(mafiaAssistanceList);
    }

    private static ArrayDeque<Job> getPoliceJobs(GameType gameType) {
        ArrayList<Job> policeJobs = new ArrayList<>(
                switch (gameType) {
                    case CLASSIC -> Set.of(new Cop());
                    case DUAL, CULT -> getDefaultPoliceJobs();
                });

        Collections.shuffle(policeJobs);
        return new ArrayDeque<>(policeJobs);
    }

    private static ArrayDeque<Job> getSpatialCitizenJobs(int spatialCitizenCount, GameType gameType) {
        List<Job> allSpatialCitizenJobs = new ArrayList<>(
                switch (gameType) {
                    case CLASSIC -> getSpatialCitizenJobsByClassic();
                    case DUAL, CULT -> getDefaultSpatialCitizenJobs();
                });

        Collections.shuffle(allSpatialCitizenJobs);
        List<Job> selectedJobs = selectJobsWithCompanion(spatialCitizenCount, allSpatialCitizenJobs);

        Collections.shuffle(selectedJobs);
        return new ArrayDeque<>(selectedJobs);
    }

    private static List<Job> selectJobsWithCompanion(int count, List<Job> allJobs) {
        ArrayDeque<Job> allJobsDeque = new ArrayDeque<>(allJobs);

        List<Job> selectedJobs = new ArrayList<>(count);

        while (selectedJobs.size() < count && !allJobsDeque.isEmpty()) {
            Job job = allJobsDeque.poll();

            if (job.requiredCompanion()) {
                if (selectedJobs.size() + 1 < count) {
                    selectedJobs.add(job);
                    selectedJobs.add(job);
                }
            } else {
                selectedJobs.add(job);
            }
        }
        return selectedJobs;
    }

    private static Set<Job> getDefaultPoliceJobs() {
        return Set.of(
                new Cop(),
                new Vigilante(),
                new Agent()
        );
    }

    private static Set<Job> getDefaultMafiaAssistanceJobs() {
        return Set.of(
                new BeastMan(),
                new HitMan(),
                new Hostess(),
                new MadScientist(),
                new Spy(),
                new Swindler(),
                new Thief(),
                new Villan(),
                new Witch()
        );
    }

    private static Set<Job> getMafiaAssistanceJobsByClassic() {
        return Set.of(
                new BeastMan(),
                new Hostess(),
                new Spy(),
                new Thief()
        );
    }

    private static Set<Job> getSpatialCitizenJobsByClassic() {
        return Set.of(
                new Soldier(),
                new Politician(),
                new Psychic(),
                new Lover(),
                new Detective(),
                new Ghoul(),
                new Martyr(),
                new Priest()
        );
    }

    private static Set<Job> getDefaultSpatialCitizenJobs() {
        return Set.of(
                new Administrator(),
                new Cabal(),
                new Detective(),
                new FortuneTeller(),
                new Gangster(),
                new Ghoul(),
                new Hacker(),
                new Judge(),
                new Lover(),
                new Magician(),
                new Martyr(),
                new Mentalist(),
                new Mercenary(),
                new Nurse(),
                new Paparazzi(),
                new Politician(),
                new Priest(),
                new Prophet(),
                new Psychic(),
                new Reporter(),
                new Soldier()
        );
    }
}