package click.mafia42.job;

public enum JobType {
    // MAFIA
    MAFIA(Role.MAFIA, "마피아", false),

    VILLAN(Role.MAFIA_ASSISTANCE, "악인", false),
    BEAST_MAN(Role.MAFIA_ASSISTANCE, "짐승인간", false),
    SPY(Role.MAFIA_ASSISTANCE, "스파이", false),
    HOSTESS(Role.MAFIA_ASSISTANCE, "마담", false),
    WITCH(Role.MAFIA_ASSISTANCE, "마녀", false),
    MAD_SCIENTIST(Role.MAFIA_ASSISTANCE, "과학자", false),
    THIEF(Role.MAFIA_ASSISTANCE, "도둑", false),
    SWINDLER(Role.MAFIA_ASSISTANCE, "사기꾼", false),
    HIT_MAN(Role.MAFIA_ASSISTANCE, "청부업자", false),

    // CULT
    CULT_LEADER(Role.CULT_LEADER, "교주", false),

    FANATIC(Role.CULT_ASSISTANCE, "광신도", false),

    // CITIZEN
    CITIZEN(Role.CITIZEN, "시민", false),

    COP(Role.POLICE, "경찰", false),
    VIGILANTE(Role.POLICE, "자경단원", false),
    AGENT(Role.POLICE, "요원", false),

    DOCTOR(Role.DOCTOR, "의사", false),

    SOLDIER(Role.SPECIAL_CITIZEN, "군인", false),
    POLITICIAN(Role.SPECIAL_CITIZEN, "정치인", false),
    PSYCHIC(Role.SPECIAL_CITIZEN, "영매", false),
    LOVER(Role.SPECIAL_CITIZEN, "연인", true),
    REPORTER(Role.SPECIAL_CITIZEN, "기자", false),
    GANGSTER(Role.SPECIAL_CITIZEN, "건달", false),
    DETECTIVE(Role.SPECIAL_CITIZEN, "사립탐정", false),
    GHOUL(Role.SPECIAL_CITIZEN, "도굴꾼", false),
    MARTYR(Role.SPECIAL_CITIZEN, "테러리스트", false),
    PRIEST(Role.SPECIAL_CITIZEN, "성직자", false),
    PROPHET(Role.SPECIAL_CITIZEN, "예언자", false),
    JUDGE(Role.SPECIAL_CITIZEN, "판사", false),
    NURSE(Role.SPECIAL_CITIZEN, "간호사", false),
    MAGICIAN(Role.SPECIAL_CITIZEN, "마술사", false),
    HACKER(Role.SPECIAL_CITIZEN, "해커", false),
    MENTALIST(Role.SPECIAL_CITIZEN, "심리학자", false),
    MERCENARY(Role.SPECIAL_CITIZEN, "용병", false),
    ADMINISTRATOR(Role.SPECIAL_CITIZEN, "공무원", false),
    CABAL(Role.SPECIAL_CITIZEN, "비밀결사", true),
    PAPARAZZI(Role.SPECIAL_CITIZEN, "파파라치", false),
    HYPNOTIST(Role.SPECIAL_CITIZEN, "최면술사", false),
    FORTUNE_TELLER(Role.SPECIAL_CITIZEN, "점쟁이", false);

    private final Role role;
    private final String alias;
    private final boolean requiredCompanion;

    JobType(Role role, String alias, boolean requiredCompanion) {
        this.role = role;
        this.alias = alias;
        this.requiredCompanion = requiredCompanion;
    }

    public Role getRole() {
        return role;
    }

    public String getAlias() {
        return alias;
    }

    public boolean isRequiredCompanion() {
        return requiredCompanion;
    }
}
