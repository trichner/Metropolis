package ch.k42.metropolis.minions;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.nolagg.lighting.LightingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaronbrewer on 3/29/14.
 */
public class NoLaggAPI {

    /**
     * Queues chunks to be relit in the near future
     *
     * @param chunk bukkit chunk to be relit
     */
    public static final void relightChunk(Chunk chunk) {
        if(!Bukkit.getServer().getPluginManager().isPluginEnabled("NoLagg")){
            Minions.w("Unable to fix light, NoLagg plugin not found.");
        }else {
            List<IntVector2> lightchunk = new ArrayList<>();
            lightchunk.add(new IntVector2(chunk));
            LightingService.schedule(chunk.getWorld(), lightchunk);
        }
    }

}
