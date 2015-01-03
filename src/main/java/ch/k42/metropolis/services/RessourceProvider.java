package ch.k42.metropolis.services;

import ch.k42.metropolis.plugin.MetropolisPlugin;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.logging.Logger;

/**
 * Created on 03.01.2015.
 *
 * @author Thomas
 */
public class RessourceProvider {
    @Produces
    public static MetropolisPlugin getPlugin(){

        return MetropolisPlugin.getInstance();
    }

    @Produces
    public static Logger createLogger(InjectionPoint injectionPoint) {
        Logger pluginLogger = MetropolisPlugin.getInstance().getLogger();
        return pluginLogger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
}
