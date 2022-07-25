package io.github.shroman.logging.log4j.fluency.appender;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.komamitsu.fluency.Fluency;
import org.komamitsu.fluency.fluentd.FluencyBuilderForFluentd;
import org.komamitsu.fluency.treasuredata.FluencyBuilderForTreasureData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FluencyManager extends AbstractManager {
    private static FluencyManagerFactory factory = new FluencyManagerFactory();

    private final Fluency fluency;

    protected FluencyManager(String name, String apiKey) {
        super(null, name);

        if (apiKey != null) {
            fluency = new FluencyBuilderForTreasureData().build(apiKey);
        } else {
            fluency = new FluencyBuilderForFluentd().build();
        }
    }

    public static FluencyManager getManager(final String name, final String apiKey) {
        return getManager(name, factory, new FactoryData(name, apiKey));
    }

    public void send(final String tag, final LogEvent logEvent) throws IOException {
        Map<String, Object> event = new HashMap<>();
        event.put("epochSeconds", logEvent.getInstant().getEpochSecond());
        event.put("date", new java.util.Date(logEvent.getTimeMillis()));
        event.put("level", logEvent.getLevel());
        event.put("message", logEvent.getMessage() == null ? null : logEvent.getMessage().getFormattedMessage());

        if (logEvent.getContextData() != null) {
            event.putAll(logEvent.getContextData().toMap());
        }

        if (logEvent.getContextStack() != null) {
            event.put("contextStack", logEvent.getContextStack().asList().toArray());
        }

        fluency.emit(tag, event);
    }

    @Override
    protected boolean releaseSub(final long timeout, final TimeUnit timeUnit) {
        boolean closed = true;
        try {
            fluency.flush();
            fluency.close();
        } catch (Exception ex) {
            LOGGER.error("Attempt to close fluency failed", ex);
            closed = false;
        }
        return closed;
    }

    private static class FactoryData {
        private final String name;
        private final String apiKey;

        public FactoryData(final String name, final String apiKey) {
            this.name = name;
            this.apiKey = apiKey;
        }
    }

    private static class FluencyManagerFactory implements ManagerFactory<FluencyManager, FactoryData> {

        /**
         * Creates the FluencyManager.
         *
         * @param name The name of the entity to manage.
         * @param data The data required to create the entity.
         * @return The FluencyManager.
         */
        @Override
        public FluencyManager createManager(final String name, final FactoryData data) {
            try {
                return new FluencyManager(name, data.apiKey);
            } catch (final Exception ex) {
                LOGGER.error("Could not create FluencyManager", ex);
            }
            return null;
        }
    }
}
