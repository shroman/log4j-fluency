package io.github.shroman.logging.log4j.fluency.appender;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Booleans;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Plugin(name = "Fluency", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public final class FluencyAppender extends AbstractAppender {

    private final FluencyManager manager;
    private final String tag;

    private FluencyAppender(String name, Filter filter, Layout<? extends Serializable> layout,
                            final boolean ignoreExceptions, final FluencyManager manager,
                            final String tag) {
        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
        this.manager = manager;
        this.tag = tag;
    }

    @PluginFactory
    public static FluencyAppender createAppender(@PluginAttribute("name") final String name,
                                                 @PluginElement("Filter") final Filter filter,
                                                 @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                 @PluginAttribute("ignoreExceptions") final String ignore,
                                                 @PluginAttribute("apikey") final String apiKey,
                                                 @PluginAttribute("tag") final String tag) {
        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);

        if (name == null) {
            LOGGER.error("No name provided for the appender");
            return null;
        }

        FluencyManager manager = FluencyManager.getManager(name, apiKey);

        return new FluencyAppender(name, filter, layout, ignoreExceptions, manager, tag);
    }

    @Override
    public void append(LogEvent event) {
        try {
            manager.send(this.tag, event);
        } catch (IOException e) {
            LOGGER.warn("Can't send event");
        }
    }

    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        setStopping();
        boolean stopped = super.stop(timeout, timeUnit, false);
        stopped &= manager.stop(timeout, timeUnit);
        setStopped();
        return stopped;
    }
}
