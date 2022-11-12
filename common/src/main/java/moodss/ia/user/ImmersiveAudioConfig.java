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
import java.util.HashMap;
import java.util.Map;

public class ImmersiveAudioConfig {

    public int resolution = 16;

    public final World world = new World();
    public final Reflectivity reflectivity = new Reflectivity();
    public final Raytracing raytracing = new Raytracing();

    private Path configPath;

    public static ImmersiveAudioConfig defaults(Path path) {
        var config = new ImmersiveAudioConfig();
        config.configPath = path;
        config.sanitize();

        return config;
    }

    public static class Reflectivity {
        public final Map<String, Float> surfaceReflectivity = new HashMap<>();

        private void sanitize() {
            Map<String, Float> surfaceReflectivity = this.surfaceReflectivity;

            surfaceReflectivity.put("all", 0.5F);

            surfaceReflectivity.put("stone", 1.5F);
            surfaceReflectivity.put("netherite_block", 1.5F);
            surfaceReflectivity.put("tuff", 1.5F);
            surfaceReflectivity.put("amethyst", 1.5F);
            surfaceReflectivity.put("basalt", 1.5F);
            surfaceReflectivity.put("calcite", 1.5F);
            surfaceReflectivity.put("bone_block", 1.5F);
            surfaceReflectivity.put("copper", 1.25F);
            surfaceReflectivity.put("deepslate", 1.5F);
            surfaceReflectivity.put("deepslate_bricks", 1.5F);
            surfaceReflectivity.put("deepslate_tiles", 1.5F);
            surfaceReflectivity.put("polished_deepslate", 1.5F);
            surfaceReflectivity.put("nether_bricks", 1.5F);
            surfaceReflectivity.put("netherrack", 1.1F);
            surfaceReflectivity.put("nether_gold_ore", 1.1F);
            surfaceReflectivity.put("nether_ore", 1.1F);
            surfaceReflectivity.put("stem", 0.4F);
            surfaceReflectivity.put("wool", 0.1F);
            surfaceReflectivity.put("honey_block", 0.1F);
            surfaceReflectivity.put("moss", 0.1F);
            surfaceReflectivity.put("soul_sand", 0.2F);
            surfaceReflectivity.put("soul_soil", 0.2F);
            surfaceReflectivity.put("coral_block", 0.2F);
            surfaceReflectivity.put("metal", 1.25F);
            surfaceReflectivity.put("wood", 0.4F);
            surfaceReflectivity.put("gravel", 0.3F);
            surfaceReflectivity.put("grass", 0.3F);
            surfaceReflectivity.put("glass", 0.75F);
            surfaceReflectivity.put("sand", 0.2F);
            surfaceReflectivity.put("snow", 0.15F);
        }
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
        this.reflectivity.sanitize();
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
