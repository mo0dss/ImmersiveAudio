package moodss.ia.user;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

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

    /**
     * Whether reverb effects shall be enabled or not.
     * <p>
     * Requires {@link ImmersiveAudioConfig#bidirectionalAudio}
     */
    public boolean eaxReverb = true;

    /**
     * Whether audio sources will be positioned based off a raycast
     */
    public boolean bidirectionalAudio = true;

    public final World world = new World();
    public final Raytracing raytracing = new Raytracing();
    public final Occlusion occlusion = new Occlusion();
    public final Exclusion exclusion = new Exclusion();
    public final Reflectivity reflectivity = new Reflectivity();

    private Path configPath;

    public static ImmersiveAudioConfig defaults(Path path, Sanitizer sanitizer) {
        var config = new ImmersiveAudioConfig();
        config.configPath = path;
        config.sanitize(sanitizer);

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
         * The max ray distance with an audio simulation distance being used as minimum
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
        public float maxAudioSimulationDistance = 32F;

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

    public static class Occlusion {
        public Map<String, Float> blockOcclusion = new Object2FloatArrayMap<>();
        public Map<SupportedSoundType, Float> soundTypeOcclusion = new Object2FloatArrayMap<>();

        /**
         * The default value for block sound occlusion
         */
        public float defaultOcclusion = 1F;

        private void sanitize(Sanitizer sanitizer) {
            sanitizer.sanitizeOcclusion(this.soundTypeOcclusion);

            this.soundTypeOcclusion.put(SupportedSoundTypes.WOOL, 1F / 1.5F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.MOSS, 1F / 0.75F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.HONEY, 1F / 0.5F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.GLASS, 1F / 0.1F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.SNOW, 1F / 0.1F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.POWDERED_SNOW, 1F / 0.1F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.BAMBOO, 1F / 0.1F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.BAMBOO_SAPLING, 1F / 0.1F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.WET_GRASS, 1F / 0.1F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.MOSS_CARPET, 1F / 0.1F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.WEEPING_VINES, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.CAVE_VINES, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.VINE, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.SWEET_BERRY_BUSH, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.SPORE_BLOSSOM, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.SMALL_DRIPLEAF, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.ROOTS, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.POINTED_DRIPSTONE, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.SCAFFOLDING, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.GLOW_LICHEN, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.CROP, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.FUNGUS, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.LILY_PAD, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.LARGE_AMETHYST_BUD, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.MEDIUM_AMETHYST_BUD, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.SMALL_AMETHYST_BUD, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.LADDER, 0F);
            this.soundTypeOcclusion.put(SupportedSoundTypes.CHAIN, 0F);
        }
    }

    public static class Exclusion {
        public Map<String, Float> blockExclusion = new Object2FloatArrayMap<>();
        public Map<SupportedSoundType, Float> soundTypeExclusion = new Object2FloatArrayMap<>();

        /**
         * The default value for block sound exclusion
         */
        public float defaultExclusion = 0F;

        private void sanitize(Sanitizer sanitizer) {
            sanitizer.sanitizeExclusion(this.soundTypeExclusion);

            this.soundTypeExclusion.put(SupportedSoundTypes.WOOL, 1.5F);
            this.soundTypeExclusion.put(SupportedSoundTypes.MOSS, 0.75F);
            this.soundTypeExclusion.put(SupportedSoundTypes.HONEY, 0.5F);
            this.soundTypeExclusion.put(SupportedSoundTypes.GLASS, 0.1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.SNOW, 0.1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.POWDERED_SNOW, 0.1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.BAMBOO, 0.1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.BAMBOO_SAPLING, 0.1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.WET_GRASS, 0.1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.MOSS_CARPET, 0.1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.WEEPING_VINES, 0F);
            this.soundTypeExclusion.put(SupportedSoundTypes.CAVE_VINES, 0F);
            this.soundTypeExclusion.put(SupportedSoundTypes.VINE, 0F);
            this.soundTypeExclusion.put(SupportedSoundTypes.SWEET_BERRY_BUSH, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.SPORE_BLOSSOM, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.SMALL_DRIPLEAF, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.ROOTS, 0F);
            this.soundTypeExclusion.put(SupportedSoundTypes.POINTED_DRIPSTONE, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.SCAFFOLDING, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.GLOW_LICHEN, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.CROP, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.FUNGUS, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.LILY_PAD, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.LARGE_AMETHYST_BUD, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.MEDIUM_AMETHYST_BUD, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.SMALL_AMETHYST_BUD, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.LADDER, 1F);
            this.soundTypeExclusion.put(SupportedSoundTypes.CHAIN, 1F);
        }
    }

    public static class Reflectivity {
        public Map<String, Float> blockReflectivity = new Object2FloatArrayMap<>();
        public Map<SupportedSoundType, Float> soundTypeReflectivity = new Object2FloatArrayMap<>();

        /**
         * The default value for block sound reflectivity
         */
        public float defaultReflectivity = 1F;

        private void sanitize(Sanitizer sanitizer) {
            sanitizer.sanitizeReflectivity(this.soundTypeReflectivity);

            this.soundTypeReflectivity.put(SupportedSoundTypes.STONE, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.NETHERITE, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.TUFF, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.AMETHYST, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.BASALT, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.CALCITE, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.BONE, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.COPPER, 1F / 1.25F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.DEEPSLATE, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.DEEPSLATE_BRICKS, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.DEEPSLATE_TILES, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.POLISHED_DEEPSLATE, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.NETHER_BRICKS, 1F / 1.5F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.NETHERRACK, 1F / 1.1F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.NETHER_GOLD_ORE, 1F / 1.1F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.NETHER_ORE, 1F / 1.1F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.STEM, 1F / 0.4F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.WOOL, 1F / 0.1F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.HONEY, 1F / 0.1F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.MOSS, 1F / 0.1F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.SOUL_SAND, 1F / 0.2F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.SOUL_SOIL, 1F / 0.2F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.CORAL, 1F / 0.2F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.METAL, 1F / 1.25F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.WOOD, 1F / 0.4F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.GRAVEL, 1F / 0.3F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.GRASS, 1F / 0.3F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.GLASS, 1F / 0.75F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.SAND, 1F / 0.2F);
            this.soundTypeReflectivity.put(SupportedSoundTypes.SNOW, 1F / 0.15F);
        }
    }

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE, Modifier.INTERFACE)
            .registerTypeHierarchyAdapter(SupportedSoundType.class, SupportedSoundType.jsonDeserializer())
            .create();

    public static ImmersiveAudioConfig load(Path path, Sanitizer sanitizer) {
        ImmersiveAudioConfig config;

        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                config = GSON.fromJson(reader, ImmersiveAudioConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Could not parse config", e);
            }

            config.configPath = path;
        } else {
            config = ImmersiveAudioConfig.defaults(path, sanitizer);
        }

        try {
            config.writeChanges();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update config file", e);
        }

        return config;
    }

    private void sanitize(Sanitizer sanitizer) {
        this.occlusion.sanitize(sanitizer);
        this.exclusion.sanitize(sanitizer);
        this.reflectivity.sanitize(sanitizer);
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

    public static interface Sanitizer {

        default void sanitizeOcclusion(Map<SupportedSoundType, Float> map) {

        }

        default void sanitizeExclusion(Map<SupportedSoundType, Float> map) {

        }

        default void sanitizeReflectivity(Map<SupportedSoundType, Float> map) {

        }
    }

}
