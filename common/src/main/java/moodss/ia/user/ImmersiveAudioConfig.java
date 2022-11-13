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

public class ImmersiveAudioConfig {

    public int resolution = 16;

    public final World world = new World();
    public final Raytracing raytracing = new Raytracing();

    private Path configPath;

    public static ImmersiveAudioConfig defaults(Path path) {
        var config = new ImmersiveAudioConfig();
        config.configPath = path;
        config.sanitize();

        return config;
    }

    public static class Raytracing {
        public int maxRayCount = 50;
        public int maxRayBounceCount = 25;

        public int additionalRayCount = 3;
        public int additionalRayBounceCount = 3;

        public float maxRayDistance = 256F;

        public boolean showDebug = false;

        public float maxRayDistance(float audioSimulationDistance) {
            return Math.min(audioSimulationDistance, this.maxRayDistance);
        }
    }

    public static class World {
        public float maxAudioSimulationDistance = 1F;
        public float minAudioSimulationDistance = Float.MIN_VALUE;

        public float maxAudioSimulationDistance(int simulationDistance) {
            float maxAudioSimulationDistance = this.maxAudioSimulationDistance;
            float simulationDistanceSqr = simulationDistance * simulationDistance;
            if (simulationDistanceSqr != 0) {
                float simulationDistanceSqrNorm = 1F / simulationDistanceSqr;

                return Math.min(maxAudioSimulationDistance * simulationDistanceSqrNorm, 1F);
            }

            return maxAudioSimulationDistance;
        }
    }

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();

    public static ImmersiveAudioConfig load(Path path) {
        ImmersiveAudioConfig config;

        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                config = GSON.fromJson(reader, ImmersiveAudioConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Could not parse config", e);
            }

            config.configPath = path;
        } else {
            config = ImmersiveAudioConfig.defaults(path);
        }

        try {
            config.writeChanges();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update config file", e);
        }

        return config;
    }

    private void sanitize() {
        //NO-OP
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
