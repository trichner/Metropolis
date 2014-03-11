package ch.k42.metropolis.grid.common;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.grid.urbanGrid.clipboard.*;
import ch.k42.metropolis.grid.urbanGrid.context.ContextConfig;
import ch.k42.metropolis.grid.urbanGrid.context.ContextProvider;
import ch.k42.metropolis.grid.urbanGrid.context.ContextProviderSimplex;

/**
 * Created by Thomas on 10.03.14.
 */
public class Factory {
    public static ClipboardLoader getDefaultLoader(ClipboardDAO dao){
        return new ClipboardLoaderWECache(dao);
    }

    public static ContextProvider getDefaultContextProvider(MetropolisGenerator generator, ContextConfig contextConfig){
        return new ContextProviderSimplex(generator,contextConfig);
    }

    public static ClipboardProvider getDefaultClipboardProvider() throws ClipboardProviderDB.PluginNotFoundException {
        return new ClipboardProviderDB();
    }
}
