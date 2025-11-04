package click.mafia42.initializer.service;

import click.mafia42.dto.client.ConsoleOutputReq;
import click.mafia42.ui.ClientUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class OutputService {
    private static final Logger log = LoggerFactory.getLogger(OutputService.class);
    private final ClientUI clientUI = ClientUI.getInstance();

    public void output(ConsoleOutputReq request) {
        switch (request.consoleType()) {
            case INFO -> JOptionPane.showMessageDialog(null, request.message(), "정보", JOptionPane.INFORMATION_MESSAGE);
            case WARN -> JOptionPane.showMessageDialog(null, request.message(), "경고", JOptionPane.WARNING_MESSAGE);
            case ERROR -> JOptionPane.showMessageDialog(null, request.message(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
