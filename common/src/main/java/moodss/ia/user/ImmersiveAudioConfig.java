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

    /**
     * How many pixels should be capable of getting calculated for data
     */
    public int audioResolution = 16;

    /**
     * How many sources should be capable of supporting mono data
     */
    public int monoSources = 32;

    /**
     * How many sources should be capable of supporting stereo data
     */
    public int stereoSources = 32;

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

        /**
         * How many rays that will be used
         */
        public int maxRayCount = 50;

        /**
         * How many rays that can bounced off a surface reflected from a ray
         */
        public int maxRayBounceCount = 25;

        /**
         * How many additional times a ray can be counted
         */
        public int additionalRayCount = 3;

        /**
         * How many additional times a ray can be bounced off
         */
        public int additionalRayBounceCount = 3;

        /**
         * Maximum distance for every ray
         */
        public float maxRayDistance = 256F;

        public boolean showDebug = false;

        /**
         * The max ray distance with a audio simulation distance being used as minimum
         *
         * @param audioSimulationDistance The audio simulation distance
         * @return The overall max ray distance
         */
        public float maxRayDistance(float audioSimulationDistance) {
            return Math.min(audioSimulationDistance, this.maxRayDistance);
        }
    }

    public static class World {

        /**
         * The max audio simulation distances
         */
        public float maxAudioSimulationDistance = 1F;

        /**
         * The min audio simulation distances
         */
        public float minAudioSimulationDistance = Float.MIN_VALUE;

        /**
         * How fast sounds will travel throughout the world
         */
        public float speedOfSound = 343.3F;

        /**
         * The max audio simulation distance with a simulation distance being used as minimum
         *
         * @param simulationDistance The simulation distance
         * @return The max overall audio simulation distance
         */
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
