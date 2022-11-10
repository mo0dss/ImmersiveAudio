package moodss.ia.user;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class EchoConfig {
    public Configuration[] configurations = new Configuration[4];

    private Path configPath;

    public static EchoConfig defaults(Path path) {
        var config = new EchoConfig();
        config.configPath = path;
        config.sanitize();

        return config;
    }

    private void sanitize() {

    }

    public static class Configuration {

    }

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();

    public static EchoConfig load(Path path) {
        EchoConfig config;

        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                config = GSON.fromJson(reader, EchoConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Could not parse config", e);
            }

            config.configPath = path;
        } else {
            config = defaults(path);
        }

        try {
            config.writeChanges();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update config file", e);
        }

        return config;
    }

    public void writeChanges() throws IOException {
        Path dir = this.configPath.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        // Use a temporary location next to the config's final destination
        Path tempPath = this.configPath.resolveSibling(this.configPath.getFileName() + ".tmp");

        // Write the file to our temporary location
        Files.writeString(tempPath, GSON.toJson(this));

        // Atomically replace the old config file (if it exists) with the temporary file
        Files.move(tempPath, this.configPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }
}
