package ch.k42.metropolis.minions;

import ch.k42.metropolis.generator.MetropolisGenerator;
import ch.k42.metropolis.plugin.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Thomas on 06.03.14.
 */
public class Minions {


    private static final String TAG = "[Metropolis] ";

    public static final void w(String msg){
        Bukkit.getLogger().warning(TAG+msg);
    }

    public static final void w(String msg,Object... args){
        Bukkit.getLogger().warning(TAG+String.format(msg,args));
    }


    public static void e(Exception e) {
        Bukkit.getLogger().warning(TAG+"Exception: " + e.getMessage());
        if(PluginConfig.isDebugEnabled()){
            e.printStackTrace();
        }
    }


    public static final void i(String msg){
        Bukkit.getLogger().info(TAG+msg);
    }

    public static final void i(String msg,Object... args){
        Bukkit.getLogger().info(TAG+String.format(msg,args));
    }

    private static final String DTAG = "[Debug] ";
    public static final void d(String msg){
        if(PluginConfig.isDebugEnabled())
            Bukkit.getLogger().info(TAG + DTAG +msg);
    }

    public static final void d(String msg,Object... args){
        if(PluginConfig.isDebugEnabled())
            Bukkit.getLogger().info(TAG+ DTAG +String.format(msg,args));
    }

    /**
     * Limits value x between [0,max)
     * @param max
     * @param x
     * @return 0 if max==0 or "Math.min(max,Math.max (0,x))";
     */
    public static final int limit(int max, int x){
        if(x<0) return 0;
        if(max==0) return 0;
        if(x>=max) return max-1;
        return x;
    }

    public static final MetropolisGenerator getGeneratorForPlayer(Player player){
        World world = player.getWorld();
        ChunkGenerator cgenerator = world.getGenerator();
        if(cgenerator instanceof MetropolisGenerator){
            MetropolisGenerator generator = (MetropolisGenerator) cgenerator;
            return generator;
        }
        return null;
    }

    public static final File findFolder(File parent, String name){
        File result = new File(parent, name);
        if (!result.isDirectory())
            if (!result.mkdir())
                throw new UnsupportedOperationException("Could not create/find the folder: " + parent.getAbsolutePath() + File.separator + name);
        return result;
    }

    public static List<File> findAllFilesRecursively(File path, List<File> files, FilenameFilter filter) {
        File[] configFiles = path.listFiles(filter);
        files.addAll(Arrays.asList(configFiles));
        File[] subfolders = path.listFiles(isDirectory());
        for (File folder : subfolders) {              // recursively search in all subfolders
            findAllFilesRecursively(folder, files, filter); // this could lead to a endless loop, maybe a max_depth would be clever...
        }
        return files;
    }

    public static List<File> findAllFilesRecursively(String path, List<File> files, FilenameFilter filter) {
        return findAllFilesRecursively(new File(path),files,filter);
    }

    public static FileFilter isDirectory() {
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    public static byte[] createChecksum(File file) throws NoSuchAlgorithmException, IOException {
        InputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        MessageDigest hash = MessageDigest.getInstance("MD5");
        hash.update(file.getName().getBytes());
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                hash.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        return hash.digest();
    }

    public static String getMD5Checksum(File file) throws IOException, NoSuchAlgorithmException {
        byte[] b = createChecksum(file);
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    private static final int sigma_factor = PluginConfig.getSigmaCut();

    public static final int makeCut(GridRandom random,int x){
        double  mean = x / 2.0;
        double sigma = mean / sigma_factor;
        int      cut = getNormalCut(random,mean, sigma);
        return Minions.limit(x - 2, cut) + 1;
    }

    public static final int getNormalCut(GridRandom random,double mean, double sigma) {
        return (int) Math.round(mean + random.getRandomGaussian() * sigma);
    }

    public static final int square(int x){
        return x*x;
    }

    /**
     * Selects a random entry from a List
     *
     * @param rand a number between [0,SUM]
     * @return
     */
    public static int getRandomWeighted(List<Integer> odds,GridRandom rand) {
        int[] thresholds = new int[odds.size()];
        thresholds[0] = odds.get(0);
        for (int i = 1; i < odds.size(); i++) {
            thresholds[i] = thresholds[i-1] + odds.get(i);
        }
        int random = rand.getRandomInt(thresholds[thresholds.length-1]);
        for (int i = 0; i < thresholds.length; i++) {
            if (random < thresholds[i])
                return i;
        }
        return 0; // something went wrong
    }

}
