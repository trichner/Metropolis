package ch.k42.metropolis.grid.urbanGrid.clipboard;

import java.io.File;
import java.util.Map;

/**
 * Created by Thomas on 10.03.14.
 */
public interface ClipboardLoader {
    public Map<String,Clipboard> loadSchematics(File schematicsFolder, File cacheFolder);
}
