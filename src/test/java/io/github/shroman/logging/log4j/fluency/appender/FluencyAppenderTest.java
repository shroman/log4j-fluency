package io.github.shroman.logging.log4j.fluency.appender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Needs `docker-compose up` for Fluentd until automated.
class FluencyAppenderTest {
    private Logger logger = (Logger) LogManager.getLogger();

    private static final String LOG_FILE = "/tmp/fluentd/log/fluentd.test_0.log";

    @AfterEach
    public void cleanup() {
        new File(LOG_FILE).delete();
    }

    @Test
    public void appendFluentd() {
        FluencyAppender fluentdAppender = FluencyAppender.createAppender("fluency", null, null, "false", null, "test");
        testAppend(fluentdAppender);
        try {
            checkLogged();
        } catch (IOException | InterruptedException e) {
            cleanup();
        }
    }

    @Test
    public void appendTD() {
        FluencyAppender fluentdAppender = FluencyAppender.createAppender("fluency", null, null, "false", "abc-key", "test");
        testAppend(fluentdAppender);
    }

    private void testAppend(FluencyAppender fluentdAppender) {
        assertEquals("fluency", fluentdAppender.getName());
        fluentdAppender.start();
        logger.addAppender(fluentdAppender);
        logger.setLevel(Level.ALL);
        logger.info("test message");
        logger.removeAppender(fluentdAppender);
        fluentdAppender.stop();
    }

    private void checkLogged() throws IOException, InterruptedException {
        Thread.sleep(1000);
        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> map = mapper.readValue(Paths.get(LOG_FILE).toFile(), Map.class);
        assertEquals("test message", map.get("message"));
    }
}
