package ch.k42.metropolis.minions.cdi;

import ch.k42.metropolis.minions.BlockAdapter;
import ch.k42.metropolis.minions.Minions;
import ch.k42.metropolis.minions.SpigotBlockAdapter;
import ch.k42.metropolis.plugin.MetropolisPlugin;
import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * Created on 04.01.2015.
 *
 * @author Thomas
 */
public class GuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bindListener(Matchers.any(), new LoggerTypeListener());
        try {
            bind(BlockAdapter.class).toInstance(new SpigotBlockAdapter());
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        bind(MetropolisPlugin.class).toInstance(MetropolisPlugin.getInstance());
    }

    static class LoggerTypeListener implements TypeListener {
        @Override
        public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
            Class<?> clazz = typeLiteral.getRawType();
            while (clazz != null) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.getType() == Logger.class &&
                            field.isAnnotationPresent(InjectLogger.class)) {
                        typeEncounter.register(new LoggerInjector<>(field));
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
    }

    static class LoggerInjector<T> implements MembersInjector<T> {
        private final Field field;
        private final Logger logger;

        LoggerInjector(Field field) {
            this.field = field;
            this.logger = Logger.getLogger(field.getDeclaringClass().getName());
            field.setAccessible(true);
        }

        @Override
        public void injectMembers(T t) {
            Minions.i("injecting logger!");
            try {
                field.set(t, logger);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
