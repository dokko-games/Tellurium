package dev.dokko.tellurium.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private static final Path PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("tellurium.json");

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static Config load() {
        if (!Files.exists(PATH)) {
            Config config = new Config();
            save(config);
            return config;
        }

        try (Reader reader = Files.newBufferedReader(PATH)) {
            Config config = GSON.fromJson(reader, Config.class);

            // ensures new fields get written if you update config later
            save(config);

            return config != null ? config : new Config();

        } catch (IOException e) {
            e.printStackTrace();
            return new Config();
        }
    }

    public static void save(Config config) {
        try (Writer writer = Files.newBufferedWriter(PATH)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}