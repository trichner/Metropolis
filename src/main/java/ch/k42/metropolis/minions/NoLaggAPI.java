package ch.k42.metropolis.minions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.nolagg.lighting.LightingService;

/**
 * Created by aaronbrewer on 3/29/14.
 */
public class NoLaggAPI {

    private static final NoLaggAPI _instance = new NoLaggAPI();

    public NoLaggAPI() {
        if(!Bukkit.getServer().getPluginManager().isPluginEnabled("NoLagg")){
            throw new UnsupportedOperationException("NoLagg Plugin not found. Make sure you have NoLagg plugin loaded or disable the option in 'config.yml'");
        }
    }

    private final void fixChunk(Chunk chunk) {
        List<IntVector2> lightchunk = new ArrayList<>();
        lightchunk.add(new IntVector2(chunk));
        LightingService.schedule(chunk.getWorld(), lightchunk);
    }

    /**
     * Queues chunks to be relit in the near future
     *
     *
     * @param chunk bukkit chunk to be relit
     */
    public static final void relightChunk(Chunk chunk) {
        _instance.fixChunk(chunk);
    }

}
