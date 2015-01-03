package ch.k42.metropolis.services;

import ch.k42.metropolis.plugin.MetropolisPlugin;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created on 03.01.2015.
 *
 * @author Thomas
 */
@Singleton
public class ConfigProvider {
    @Inject
    private MetropolisPlugin plugin;

    private Config config = ConfigFactory.load();

    public Config get(String named){
        return config.getConfig(named);
    }

    public Config get(){
        return config;
    }
}
