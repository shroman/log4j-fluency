package io.github.shroman.logging.log4j.fluency.appender;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FluencyAppenderTest {
    private Logger logger = (Logger) LogManager.getLogger();

    @Test
    public void appendFluentd() {
        FluencyAppender fluentdAppender = FluencyAppender.createAppender("fluency", null, null, "false", null, "testTag");
        testAppend(fluentdAppender);
    }

    @Test
    public void appendTD() {
        FluencyAppender fluentdAppender = FluencyAppender.createAppender("fluency", null, null, "false", "abc-key", "testTag");
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
}
