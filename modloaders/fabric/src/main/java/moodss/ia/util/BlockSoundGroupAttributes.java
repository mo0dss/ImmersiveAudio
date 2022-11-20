package moodss.ia.util;

import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import moodss.ia.user.SupportedSoundType;
import moodss.ia.user.SupportedSoundTypes;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

//TODO: Remake elsewhere
public class BlockSoundGroupAttributes {

    protected static final Queue<Runnable> pendingTasks = Queues.newArrayDeque();

    protected static final Set<BlockModifier> blockOcclusionModifiers = new ObjectArraySet<>();
    protected static final Set<BlockModifier> blockExclusionModifiers = new ObjectArraySet<>();
    protected static final Set<BlockModifier> blockReflectionModifiers = new ObjectArraySet<>();

    public static void registerBlockOcclusionModifier(BlockModifier modifier) {
        pendingTasks.add(() -> blockOcclusionModifiers.add(modifier));
    }

    public static void registerBlockExclusionModifier(BlockModifier modifier) {
        pendingTasks.add(() -> blockExclusionModifiers.add(modifier));
    }

    public static void registerBlockReflectionModifier(BlockModifier modifier) {
        pendingTasks.add(() -> blockReflectionModifiers.add(modifier));
    }

    public static void init() {
        pendingTasks.forEach(Runnable::run);
    }

    public static void applyOcclusion(Map<SupportedSoundType, Float> map) {
        map.put(SupportedSoundTypes.WOOL, 1F / 1.5F);
        map.put(SupportedSoundTypes.MOSS, 1F / 0.75F);
        map.put(SupportedSoundTypes.HONEY, 1F / 0.5F);
        map.put(SupportedSoundTypes.GLASS, 1F / 0.1F);
        map.put(SupportedSoundTypes.SNOW, 1F / 0.1F);
        map.put(SupportedSoundTypes.POWDERED_SNOW, 1F / 0.1F);
        map.put(SupportedSoundTypes.BAMBOO, 1F / 0.1F);
        map.put(SupportedSoundTypes.BAMBOO_SAPLING, 1F / 0.1F);
        map.put(SupportedSoundTypes.WET_GRASS, 1F / 0.1F);
        map.put(SupportedSoundTypes.MOSS_CARPET, 1F / 0.1F);
        map.put(SupportedSoundTypes.WEEPING_VINES, 0F);
        map.put(SupportedSoundTypes.CAVE_VINES, 0F);
        map.put(SupportedSoundTypes.VINE, 0F);
        map.put(SupportedSoundTypes.SWEET_BERRY_BUSH, 0F);
        map.put(SupportedSoundTypes.SPORE_BLOSSOM, 0F);
        map.put(SupportedSoundTypes.SMALL_DRIPLEAF, 0F);
        map.put(SupportedSoundTypes.ROOTS, 0F);
        map.put(SupportedSoundTypes.POINTED_DRIPSTONE, 0F);
        map.put(SupportedSoundTypes.SCAFFOLDING, 0F);
        map.put(SupportedSoundTypes.GLOW_LICHEN, 0F);
        map.put(SupportedSoundTypes.CROP, 0F);
        map.put(SupportedSoundTypes.FUNGUS, 0F);
        map.put(SupportedSoundTypes.LILY_PAD, 0F);
        map.put(SupportedSoundTypes.LARGE_AMETHYST_BUD, 0F);
        map.put(SupportedSoundTypes.MEDIUM_AMETHYST_BUD, 0F);
        map.put(SupportedSoundTypes.SMALL_AMETHYST_BUD, 0F);
        map.put(SupportedSoundTypes.LADDER, 0F);
        map.put(SupportedSoundTypes.CHAIN, 0F);

        blockOcclusionModifiers.forEach((modifier) -> modifier.apply(map));
    }

    public static void applyExclusion(Map<SupportedSoundType, Float> map) {
        map.put(SupportedSoundTypes.WOOL, 1.5F);
        map.put(SupportedSoundTypes.MOSS, 0.75F);
        map.put(SupportedSoundTypes.HONEY, 0.5F);
        map.put(SupportedSoundTypes.GLASS, 0.1F);
        map.put(SupportedSoundTypes.SNOW, 0.1F);
        map.put(SupportedSoundTypes.POWDERED_SNOW, 0.1F);
        map.put(SupportedSoundTypes.BAMBOO, 0.1F);
        map.put(SupportedSoundTypes.BAMBOO_SAPLING, 0.1F);
        map.put(SupportedSoundTypes.WET_GRASS, 0.1F);
        map.put(SupportedSoundTypes.MOSS_CARPET, 0.1F);
        map.put(SupportedSoundTypes.WEEPING_VINES, 0F);
        map.put(SupportedSoundTypes.CAVE_VINES, 0F);
        map.put(SupportedSoundTypes.VINE, 0F);
        map.put(SupportedSoundTypes.SWEET_BERRY_BUSH, 1F);
        map.put(SupportedSoundTypes.SPORE_BLOSSOM, 1F);
        map.put(SupportedSoundTypes.SMALL_DRIPLEAF, 1F);
        map.put(SupportedSoundTypes.ROOTS, 0F);
        map.put(SupportedSoundTypes.POINTED_DRIPSTONE, 1F);
        map.put(SupportedSoundTypes.SCAFFOLDING, 1F);
        map.put(SupportedSoundTypes.GLOW_LICHEN, 1F);
        map.put(SupportedSoundTypes.CROP, 1F);
        map.put(SupportedSoundTypes.FUNGUS, 1F);
        map.put(SupportedSoundTypes.LILY_PAD, 1F);
        map.put(SupportedSoundTypes.LARGE_AMETHYST_BUD, 1F);
        map.put(SupportedSoundTypes.MEDIUM_AMETHYST_BUD, 1F);
        map.put(SupportedSoundTypes.SMALL_AMETHYST_BUD, 1F);
        map.put(SupportedSoundTypes.LADDER, 1F);
        map.put(SupportedSoundTypes.CHAIN, 1F);

        blockExclusionModifiers.forEach((modifier) -> modifier.apply(map));
    }

    public static void applyReflectivity(Map<SupportedSoundType, Float> map) {
        map.put(SupportedSoundTypes.STONE, 1F / 1.5F);
        map.put(SupportedSoundTypes.NETHERITE, 1F / 1.5F);
        map.put(SupportedSoundTypes.TUFF, 1F / 1.5F);
        map.put(SupportedSoundTypes.AMETHYST, 1F / 1.5F);
        map.put(SupportedSoundTypes.BASALT, 1F / 1.5F);
        map.put(SupportedSoundTypes.CALCITE, 1F / 1.5F);
        map.put(SupportedSoundTypes.BONE, 1F / 1.5F);
        map.put(SupportedSoundTypes.COPPER, 1F / 1.25F);
        map.put(SupportedSoundTypes.DEEPSLATE, 1F / 1.5F);
        map.put(SupportedSoundTypes.DEEPSLATE_BRICKS, 1F / 1.5F);
        map.put(SupportedSoundTypes.DEEPSLATE_TILES, 1F / 1.5F);
        map.put(SupportedSoundTypes.POLISHED_DEEPSLATE, 1F / 1.5F);
        map.put(SupportedSoundTypes.NETHER_BRICKS, 1F / 1.5F);
        map.put(SupportedSoundTypes.NETHERRACK, 1F / 1.1F);
        map.put(SupportedSoundTypes.NETHER_GOLD_ORE, 1F / 1.1F);
        map.put(SupportedSoundTypes.NETHER_ORE, 1F / 1.1F);
        map.put(SupportedSoundTypes.STEM, 1F / 0.4F);
        map.put(SupportedSoundTypes.WOOL, 1F / 0.1F);
        map.put(SupportedSoundTypes.HONEY, 1F / 0.1F);
        map.put(SupportedSoundTypes.MOSS, 1F / 0.1F);
        map.put(SupportedSoundTypes.SOUL_SAND, 1F / 0.2F);
        map.put(SupportedSoundTypes.SOUL_SOIL, 1F / 0.2F);
        map.put(SupportedSoundTypes.CORAL, 1F / 0.2F);
        map.put(SupportedSoundTypes.METAL, 1F / 1.25F);
        map.put(SupportedSoundTypes.WOOD, 1F / 0.4F);
        map.put(SupportedSoundTypes.GRAVEL, 1F / 0.3F);
        map.put(SupportedSoundTypes.GRASS, 1F / 0.3F);
        map.put(SupportedSoundTypes.GLASS, 1F / 0.75F);
        map.put(SupportedSoundTypes.SAND, 1F / 0.2F);
        map.put(SupportedSoundTypes.SNOW, 1F / 0.15F);

        blockReflectionModifiers.forEach((modifier) -> modifier.apply(map));
    }

    @FunctionalInterface
    public interface BlockModifier {
        void apply(Map<SupportedSoundType, Float> map);
    }
}
