package click.mafia42.initializer.service;

import click.mafia42.dto.ConsoleOutputReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputService {
    private static final Logger log = LoggerFactory.getLogger(OutputService.class);

    public void output(ConsoleOutputReq request) {
        log.info(request.message());
    }
}
