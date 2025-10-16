package click.mafia42.dto.client;

import java.awt.*;

public enum MessageType {
    MAFIA(Color.RED), CULT(Color.ORANGE), PSYCHIC(Color.BLACK), ALL(Color.BLACK), LOVER(Color.PINK), DIE(Color.GRAY),
    SYSTEM(Color.DARK_GRAY)
    ;

    private final Color color;

    MessageType(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
