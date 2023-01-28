package io.github.shroman.logging.log4j.fluency.appender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Needs `docker-compose up` for Fluentd until automated.
class FluencyAppenderTest {
    private Logger logger = (Logger) LogManager.getLogger();

    private static final String LOG_FILE = "/tmp/fluentd/log/fluentd.test_0.log";

    @AfterEach
    public void cleanup() {
        ThreadContext.clearMap();
        new File(LOG_FILE).delete();
    }

    @Test
    public void appendFluentd() throws IOException, InterruptedException {
        FluencyAppender fluentdAppender = FluencyAppender.createAppender("fluency", null, null, "false", null, "test");
        testAppend(fluentdAppender);
        checkLogged();
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
        ThreadContext.put("ip_address", "127.0.0.1");
        logger.info("test message");
        logger.removeAppender(fluentdAppender);
        fluentdAppender.stop();
    }

    private void checkLogged() throws IOException, InterruptedException {
        Thread.sleep(1000);
        ObjectMapper mapper = new ObjectMapper();
        String contents = Files.readString(Paths.get(LOG_FILE));
        Map<?, ?> map = mapper.readValue(contents.split("\t")[2], Map.class);
        assertEquals("test message", map.get("message"));
        assertEquals("127.0.0.1", map.get("ip_address"));
    }
}
