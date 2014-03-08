package ch.k42.metropolis.minions;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Thomas on 06.03.14.
 */
public class Minions {
    /**
     * Limits value x between [0,max)
     * @param max
     * @param x
     * @return "Math.min(max,Math.max (0,x))";
     */
    public static final int limit(int max, int x){
        if(x<0) return 0;
        else if(x>=max) return max-1;
        return x;
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
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        return complete.digest();
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

}
