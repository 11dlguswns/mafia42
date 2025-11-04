package click.mafia42.initializer.service;

import click.mafia42.dto.client.ConsoleOutputReq;
import click.mafia42.dto.client.DisplayNotificationReq;
import click.mafia42.ui.ClientUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class OutputService {
    private static final Logger log = LoggerFactory.getLogger(OutputService.class);
    private final ClientUI clientUI = ClientUI.getInstance();

    public void output(ConsoleOutputReq request) {
        switch (request.consoleType()) {
            case INFO -> log.info(request.message());
            case WARN -> log.warn(request.message());
            case ERROR -> log.error(request.message());
        }
    }

    public void displayNotification(DisplayNotificationReq request) {
        JOptionPane.showMessageDialog(null, request.message());
    }
}
