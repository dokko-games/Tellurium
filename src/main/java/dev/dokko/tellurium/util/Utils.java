package dev.dokko.util;

import net.minecraft.client.MinecraftClient;

import java.io.File;
public class Utils {
    public static File getScreenshotsPath(MinecraftClient client){
        return new File(client.getResourcePackDir().toFile().getParentFile().getPath()+"/screenshots");
    }

    public static boolean delete(File f) {
        if(f.isDirectory()){
            for(File f2 : f.listFiles()){
                delete(f2);
            }
        }
        return f.delete();
    }

    public static float closest(float a, float... options) {
        if (options.length == 0) {
            throw new IllegalArgumentException("No options provided");
        }
        float res = Float.MAX_VALUE;
        float dist = Float.MAX_VALUE;
        for (float f : options) {
            float currentDist = Math.abs(a - f);
            if (currentDist < dist) {
                dist = currentDist;
                res = f;
            }
        }

        return res;
    }
}
