package click.mafia42.job;

public enum JobType {
    // MAFIA
    MAFIA(Role.MAFIA, "마피아"),

    VILLAN(Role.MAFIA_ASSISTANCE, "악인"),
    BEAST_MAN(Role.MAFIA_ASSISTANCE, "짐승인간"),
    SPY(Role.MAFIA_ASSISTANCE, "스파이"),
    HOSTESS(Role.MAFIA_ASSISTANCE, "마담"),
    WITCH(Role.MAFIA_ASSISTANCE, "마녀"),
    MAD_SCIENTIST(Role.MAFIA_ASSISTANCE, "과학자"),
    THIEF(Role.MAFIA_ASSISTANCE, "도둑"),
    SWINDLER(Role.MAFIA_ASSISTANCE, "사기꾼"),
    HIT_MAN(Role.MAFIA_ASSISTANCE, "청부업자"),

    // CULT
    CULT_LEADER(Role.CULT_LEADER, "교주"),

    FANATIC(Role.CULT_ASSISTANCE, "광신도"),

    // CITIZEN
    CITIZEN(Role.CITIZEN, "시민"),

    COP(Role.POLICE, "경찰"),
    VIGILANTE(Role.POLICE, "자경단원"),
    AGENT(Role.POLICE, "요원"),

    DOCTOR(Role.DOCTOR, "의사"),

    SOLDIER(Role.SPECIAL_CITIZEN, "군인"),
    POLITICIAN(Role.SPECIAL_CITIZEN, "정치인"),
    PSYCHIC(Role.SPECIAL_CITIZEN, "영매"),
    LOVER(Role.SPECIAL_CITIZEN, "연인"),
    REPORTER(Role.SPECIAL_CITIZEN, "기자"),
    GANGSTER(Role.SPECIAL_CITIZEN, "건달"),
    DETECTIVE(Role.SPECIAL_CITIZEN, "사립탐정"),
    GHOUL(Role.SPECIAL_CITIZEN, "도굴꾼"),
    MARTYR(Role.SPECIAL_CITIZEN, "테러리스트"),
    PRIEST(Role.SPECIAL_CITIZEN, "성직자"),
    PROPHET(Role.SPECIAL_CITIZEN, "예언자"),
    JUDGE(Role.SPECIAL_CITIZEN, "판사"),
    NURSE(Role.SPECIAL_CITIZEN, "간호사"),
    MAGICIAN(Role.SPECIAL_CITIZEN, "마술사"),
    HACKER(Role.SPECIAL_CITIZEN, "해커"),
    MENTALIST(Role.SPECIAL_CITIZEN, "심리학자"),
    MERCENARY(Role.SPECIAL_CITIZEN, "용병"),
    ADMINISTRATOR(Role.SPECIAL_CITIZEN, "공무원"),
    CABAL(Role.SPECIAL_CITIZEN, "비밀결사"),
    PAPARAZZI(Role.SPECIAL_CITIZEN, "파파라치"),
    HYPNOTIST(Role.SPECIAL_CITIZEN, "최면술사"),
    FORTUNE_TELLER(Role.SPECIAL_CITIZEN, "점쟁이");

    private final Role role;
    private final String alias;

    JobType(Role role, String alias) {
        this.role = role;
        this.alias = alias;
    }

    public Role getRole() {
        return role;
    }

    public String getAlias() {
        return alias;
    }
}
