package ch.k42.metropolis.plugin;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.generator.populators.OreVein;
import ch.k42.metropolis.minions.Nimmersatt;
import ch.k42.metropolis.model.enums.ContextType;
import ch.k42.metropolis.model.zones.ContextZone;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Created by spaceribs on 2/4/14.
 */
public class PopulatorConfig {

    public class PopulatorSchema {

        private OreVein[] ores = {
            new OreVein("GRAVEL", 32, 10, 1, 0),
            new OreVein("COAL_ORE", 16, 21, 1, 0),
            new OreVein("IRON_ORE", 8, 21, 0.8, 0),
            new OreVein("GOLD_ORE", 8, 8, 0.75, 0),
            new OreVein("REDSTONE_ORE", 7, 10, 0.7, 0),
            new OreVein("DIAMOND_ORE", 7, 1, 0.4, 0),
            new OreVein("LAPIS_ORE", 6, 3, 0.5, 0)
        };

        private ContextZone[] contexts = {
            new ContextZone("RESIDENTIAL", 1),
            new ContextZone(new ContextZone[] {
               new ContextZone("LOWRISE", 3),
               new ContextZone("INDUSTRIAL", 2)
            }, 1),
            new ContextZone("MIDRISE", 1),
            new ContextZone("HIGHRISE", 1)
        };

        private double contextScale = 1.0D;
        private double contextRecursiveScale = 1.0D;

        public PopulatorSchema(){}

        public ContextZone[] getContexts() {
            return contexts;
        }

        public OreVein[] getOres() {
            return ores;
        }
    }

    private PopulatorSchema config = new PopulatorSchema();

    public PopulatorConfig(){};

    public PopulatorConfig(MetropolisGenerator generator, File file){
        Gson gson = new Gson();
        try {
            String json = new String(Files.readAllBytes(file.toPath()));
            json = Nimmersatt.friss(json);
            this.config = gson.fromJson(json, PopulatorSchema.class);
        } catch (Exception e) {
            generator.reportException("[PopulatorConfig] " + file.getName() + " could NOT be loaded", e);
        }
    }

    public ContextZone[] getContexts() {
        return config.getContexts();
    }

    public OreVein[] getOres() {
        return config.getOres();
    }

}
