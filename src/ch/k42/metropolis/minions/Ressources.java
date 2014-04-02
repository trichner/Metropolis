package ch.k42.metropolis.minions;

import ch.k42.metropolis.grid.urbanGrid.clipboard.ClipboardDAO;
import ch.k42.metropolis.grid.urbanGrid.clipboard.ClipboardLoader;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * Created by Thomas on 02.04.14.
 */
public class Ressources implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ClipboardDAO.class);
    }
}
