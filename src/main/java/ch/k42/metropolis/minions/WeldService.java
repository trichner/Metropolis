package ch.k42.metropolis.minions;

/**
 * Created on 20.11.2014.
 *
 * @author Thomas
 */
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.inject.Singleton;

/**
 * Created on 03.06.14.
 *
 * @author Thomas Richner
 */
@Singleton
public class WeldService {

    class ShutdownHook extends Thread {
        public void run() {
            weld.shutdown();
        }
    }

    private Weld weld;
    private WeldContainer container;

    public void init() {
        weld = new Weld();
        container = weld.initialize();

        // register a hook to shut down weld properly
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    public <T> T get(Class<T> clazz) {
        return container.instance().select(clazz).get();
    }

}