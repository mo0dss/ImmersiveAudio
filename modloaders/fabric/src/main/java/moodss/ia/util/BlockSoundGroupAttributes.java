package moodss.ia.util;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.sound.BlockSoundGroup;

import java.util.Set;

public class BlockSoundGroupAttributes {

    protected static final Set<BlockModifier> blockOcclusionModifiers = new ObjectArraySet<>();
    protected static final Set<BlockModifier> blockExclusionModifiers = new ObjectArraySet<>();
    protected static final Set<BlockModifier> blockReflectionModifiers = new ObjectArraySet<>();

    public static void registerBlockOcclusionModifier(BlockModifier modifier) {
        blockOcclusionModifiers.add(modifier);
    }

    public static void registerBlockExclusionModifier(BlockModifier modifier) {
        blockExclusionModifiers.add(modifier);
    }

    public static void registerBlockReflectionModifier(BlockModifier modifier) {
        blockReflectionModifiers.add(modifier);
    }

    public static void applyOcclusion(Object2FloatMap<Object> map) {
        map.put(BlockSoundGroup.WOOL, 1F / 1.5F);
        map.put(BlockSoundGroup.MOSS_BLOCK, 1F / 0.75F);
        map.put(BlockSoundGroup.HONEY, 1F / 0.5F);
        map.put(BlockSoundGroup.GLASS, 1F / 0.1F);
        map.put(BlockSoundGroup.SNOW, 1F / 0.1F);
        map.put(BlockSoundGroup.POWDER_SNOW, 1F / 0.1F);
        map.put(BlockSoundGroup.BAMBOO, 1F / 0.1F);
        map.put(BlockSoundGroup.BAMBOO_SAPLING, 1F / 0.1F);
        map.put(BlockSoundGroup.WET_GRASS, 1F / 0.1F);
        map.put(BlockSoundGroup.MOSS_CARPET, 1F / 0.1F);
        map.put(BlockSoundGroup.WEEPING_VINES, 0F);
        map.put(BlockSoundGroup.CAVE_VINES, 0F);
        map.put(BlockSoundGroup.VINE, 0F);
        map.put(BlockSoundGroup.SWEET_BERRY_BUSH, 0F);
        map.put(BlockSoundGroup.SPORE_BLOSSOM, 0F);
        map.put(BlockSoundGroup.SMALL_DRIPLEAF, 0F);
        map.put(BlockSoundGroup.ROOTS, 0F);
        map.put(BlockSoundGroup.POINTED_DRIPSTONE, 0F);
        map.put(BlockSoundGroup.SCAFFOLDING, 0F);
        map.put(BlockSoundGroup.GLOW_LICHEN, 0F);
        map.put(BlockSoundGroup.CROP, 0F);
        map.put(BlockSoundGroup.FUNGUS, 0F);
        map.put(BlockSoundGroup.LILY_PAD, 0F);
        map.put(BlockSoundGroup.LARGE_AMETHYST_BUD, 0F);
        map.put(BlockSoundGroup.MEDIUM_AMETHYST_BUD, 0F);
        map.put(BlockSoundGroup.SMALL_AMETHYST_BUD, 0F);
        map.put(BlockSoundGroup.LADDER, 0F);
        map.put(BlockSoundGroup.CHAIN, 0F);

        blockOcclusionModifiers.forEach((modifier) -> modifier.apply(map));
    }

    public static void applyExclusion(Object2FloatMap<Object> map) {
        map.put(BlockSoundGroup.WOOL, 1.5F);
        map.put(BlockSoundGroup.MOSS_BLOCK, 0.75F);
        map.put(BlockSoundGroup.HONEY, 0.5F);
        map.put(BlockSoundGroup.GLASS, 0.1F);
        map.put(BlockSoundGroup.SNOW, 0.1F);
        map.put(BlockSoundGroup.POWDER_SNOW, 0.1F);
        map.put(BlockSoundGroup.BAMBOO, 0.1F);
        map.put(BlockSoundGroup.BAMBOO_SAPLING, 0.1F);
        map.put(BlockSoundGroup.WET_GRASS, 0.1F);
        map.put(BlockSoundGroup.MOSS_CARPET, 0.1F);
        map.put(BlockSoundGroup.WEEPING_VINES, 0F);
        map.put(BlockSoundGroup.CAVE_VINES, 0F);
        map.put(BlockSoundGroup.VINE, 0F);
        map.put(BlockSoundGroup.SWEET_BERRY_BUSH, 1F);
        map.put(BlockSoundGroup.SPORE_BLOSSOM, 1F);
        map.put(BlockSoundGroup.SMALL_DRIPLEAF, 1F);
        map.put(BlockSoundGroup.ROOTS, 0F);
        map.put(BlockSoundGroup.POINTED_DRIPSTONE, 1F);
        map.put(BlockSoundGroup.SCAFFOLDING, 1F);
        map.put(BlockSoundGroup.GLOW_LICHEN, 1F);
        map.put(BlockSoundGroup.CROP, 1F);
        map.put(BlockSoundGroup.FUNGUS, 1F);
        map.put(BlockSoundGroup.LILY_PAD, 1F);
        map.put(BlockSoundGroup.LARGE_AMETHYST_BUD, 1F);
        map.put(BlockSoundGroup.MEDIUM_AMETHYST_BUD, 1F);
        map.put(BlockSoundGroup.SMALL_AMETHYST_BUD, 1F);
        map.put(BlockSoundGroup.LADDER, 1F);
        map.put(BlockSoundGroup.CHAIN, 1F);

        blockExclusionModifiers.forEach((modifier) -> modifier.apply(map));
    }

    public static void applyReflectivity(Object2FloatMap<Object> map) {
        map.put(BlockSoundGroup.STONE, 1F / 1.5F);
        map.put(BlockSoundGroup.NETHERITE, 1F / 1.5F);
        map.put(BlockSoundGroup.TUFF, 1F / 1.5F);
        map.put(BlockSoundGroup.AMETHYST_BLOCK, 1F / 1.5F);
        map.put(BlockSoundGroup.BASALT, 1F / 1.5F);
        map.put(BlockSoundGroup.CALCITE, 1F / 1.5F);
        map.put(BlockSoundGroup.BONE, 1F / 1.5F);
        map.put(BlockSoundGroup.COPPER, 1F / 1.25F);
        map.put(BlockSoundGroup.DEEPSLATE, 1F / 1.5F);
        map.put(BlockSoundGroup.DEEPSLATE_BRICKS, 1F / 1.5F);
        map.put(BlockSoundGroup.DEEPSLATE_TILES, 1F / 1.5F);
        map.put(BlockSoundGroup.POLISHED_DEEPSLATE, 1F / 1.5F);
        map.put(BlockSoundGroup.NETHER_BRICKS, 1F / 1.5F);
        map.put(BlockSoundGroup.NETHERRACK, 1F / 1.1F);
        map.put(BlockSoundGroup.NETHER_GOLD_ORE, 1F / 1.1F);
        map.put(BlockSoundGroup.NETHER_ORE, 1F / 1.1F);
        map.put(BlockSoundGroup.STEM, 1F / 0.4F);
        map.put(BlockSoundGroup.WOOL, 1F / 0.1F);
        map.put(BlockSoundGroup.HONEY, 1F / 0.1F);
        map.put(BlockSoundGroup.MOSS_BLOCK, 1F / 0.1F);
        map.put(BlockSoundGroup.SOUL_SAND, 1F / 0.2F);
        map.put(BlockSoundGroup.SOUL_SOIL, 1F / 0.2F);
        map.put(BlockSoundGroup.CORAL, 1F / 0.2F);
        map.put(BlockSoundGroup.METAL, 1F / 1.25F);
        map.put(BlockSoundGroup.WOOD, 1F / 0.4F);
        map.put(BlockSoundGroup.GRAVEL, 1F / 0.3F);
        map.put(BlockSoundGroup.GRASS, 1F / 0.3F);
        map.put(BlockSoundGroup.GLASS, 1F / 0.75F);
        map.put(BlockSoundGroup.SAND, 1F / 0.2F);
        map.put(BlockSoundGroup.SNOW, 1F / 0.15F);

        blockReflectionModifiers.forEach((modifier) -> modifier.apply(map));
    }

    @FunctionalInterface
    public interface BlockModifier {
        void apply(Object2FloatMap<Object> map);
    }
}
