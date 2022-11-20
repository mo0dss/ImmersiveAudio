package moodss.ia.util;

import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import moodss.ia.user.SupportedSoundType;
import moodss.ia.user.SupportedSoundTypes;
import net.minecraft.sound.BlockSoundGroup;

import java.util.Queue;

/**
 * TODO: Move to {@link net.minecraft.util.registry.Registry}
 */
public class SupportedSoundTypeUtil {
    protected static final Queue<Runnable> pendingTasks = Queues.newArrayDeque();

    private static final Object2ReferenceMap<BlockSoundGroup, SupportedSoundType> SOUND_TYPES = new Object2ReferenceOpenHashMap<>();

    public static void register(BlockSoundGroup group, SupportedSoundType type) {
        pendingTasks.add(() -> SOUND_TYPES.putIfAbsent(group, type));
    }

    public static void init() {
        registerVanillaSoundTypes();

        pendingTasks.forEach(Runnable::run);
    }

    public static SupportedSoundType from(BlockSoundGroup group) {
        return SOUND_TYPES.get(group);
    }

    protected static void registerVanillaSoundTypes() {
        register(BlockSoundGroup.WOOD, SupportedSoundTypes.WOOD);
        register(BlockSoundGroup.GRAVEL, SupportedSoundTypes.GRAVEL);
        register(BlockSoundGroup.GRASS, SupportedSoundTypes.GRASS);
        register(BlockSoundGroup.WET_GRASS, SupportedSoundTypes.WET_GRASS);
        register(BlockSoundGroup.LILY_PAD, SupportedSoundTypes.LILY_PAD);
        register(BlockSoundGroup.STONE, SupportedSoundTypes.STONE);
        register(BlockSoundGroup.METAL, SupportedSoundTypes.METAL);
        register(BlockSoundGroup.GLASS, SupportedSoundTypes.GLASS);
        register(BlockSoundGroup.WOOL, SupportedSoundTypes.WOOL);
        register(BlockSoundGroup.SAND, SupportedSoundTypes.SAND);
        register(BlockSoundGroup.SNOW, SupportedSoundTypes.SNOW);
        register(BlockSoundGroup.POWDER_SNOW, SupportedSoundTypes.POWDERED_SNOW);
        register(BlockSoundGroup.LADDER, SupportedSoundTypes.LADDER);
        register(BlockSoundGroup.ANVIL, SupportedSoundTypes.ANVIL);
        register(BlockSoundGroup.SLIME, SupportedSoundTypes.SLIME);
        register(BlockSoundGroup.HONEY, SupportedSoundTypes.HONEY);
        register(BlockSoundGroup.CORAL, SupportedSoundTypes.CORAL);
        register(BlockSoundGroup.BAMBOO, SupportedSoundTypes.BAMBOO);
        register(BlockSoundGroup.BAMBOO_SAPLING, SupportedSoundTypes.BAMBOO_SAPLING);
        register(BlockSoundGroup.SCAFFOLDING, SupportedSoundTypes.SCAFFOLDING);
        register(BlockSoundGroup.SWEET_BERRY_BUSH, SupportedSoundTypes.SWEET_BERRY_BUSH);
        register(BlockSoundGroup.CROP, SupportedSoundTypes.CROP);
        register(BlockSoundGroup.STEM, SupportedSoundTypes.STEM);
        register(BlockSoundGroup.VINE, SupportedSoundTypes.VINE);
        register(BlockSoundGroup.NETHER_WART, SupportedSoundTypes.NETHER_WART);
        register(BlockSoundGroup.LANTERN, SupportedSoundTypes.LANTERN);
        register(BlockSoundGroup.NETHER_STEM, SupportedSoundTypes.NETHER_STEM);
        register(BlockSoundGroup.NYLIUM, SupportedSoundTypes.NYLIUM);
        register(BlockSoundGroup.FUNGUS, SupportedSoundTypes.FUNGUS);
        register(BlockSoundGroup.ROOTS, SupportedSoundTypes.ROOTS);
        register(BlockSoundGroup.SHROOMLIGHT, SupportedSoundTypes.SHROOMLIGHT);
        register(BlockSoundGroup.WEEPING_VINES, SupportedSoundTypes.WEEPING_VINES);
        register(BlockSoundGroup.WEEPING_VINES_LOW_PITCH, SupportedSoundTypes.WEEPING_VINES_LOW_PITCH);
        register(BlockSoundGroup.SOUL_SAND, SupportedSoundTypes.SOUL_SAND);
        register(BlockSoundGroup.SOUL_SOIL, SupportedSoundTypes.SOUL_SOIL);
        register(BlockSoundGroup.BASALT, SupportedSoundTypes.BASALT);
        register(BlockSoundGroup.WART_BLOCK, SupportedSoundTypes.WART);
        register(BlockSoundGroup.NETHERRACK, SupportedSoundTypes.NETHERRACK);
        register(BlockSoundGroup.NETHER_BRICKS, SupportedSoundTypes.NETHER_BRICKS);
        register(BlockSoundGroup.NETHER_SPROUTS, SupportedSoundTypes.NETHER_SPROUTS);
        register(BlockSoundGroup.NETHER_ORE, SupportedSoundTypes.NETHER_ORE);
        register(BlockSoundGroup.BONE, SupportedSoundTypes.BONE);
        register(BlockSoundGroup.NETHERITE, SupportedSoundTypes.NETHERITE);
        register(BlockSoundGroup.ANCIENT_DEBRIS, SupportedSoundTypes.ANCIENT_DEBRIS);
        register(BlockSoundGroup.LODESTONE, SupportedSoundTypes.LODESTONE);
        register(BlockSoundGroup.CHAIN, SupportedSoundTypes.CHAIN);
        register(BlockSoundGroup.NETHER_GOLD_ORE, SupportedSoundTypes.NETHER_GOLD_ORE);
        register(BlockSoundGroup.GILDED_BLACKSTONE, SupportedSoundTypes.GILDED_BLACKSTONE);
        register(BlockSoundGroup.CANDLE, SupportedSoundTypes.CANDLE);
        register(BlockSoundGroup.NETHER_GOLD_ORE, SupportedSoundTypes.NETHER_GOLD_ORE);
        register(BlockSoundGroup.AMETHYST_BLOCK, SupportedSoundTypes.AMETHYST);
        register(BlockSoundGroup.AMETHYST_CLUSTER, SupportedSoundTypes.AMETHYST_CLUSTER);
        register(BlockSoundGroup.SMALL_AMETHYST_BUD, SupportedSoundTypes.SMALL_AMETHYST_BUD);
        register(BlockSoundGroup.MEDIUM_AMETHYST_BUD, SupportedSoundTypes.MEDIUM_AMETHYST_BUD);
        register(BlockSoundGroup.LARGE_AMETHYST_BUD, SupportedSoundTypes.LARGE_AMETHYST_BUD);
        register(BlockSoundGroup.TUFF, SupportedSoundTypes.TUFF);
        register(BlockSoundGroup.CALCITE, SupportedSoundTypes.CALCITE);
        register(BlockSoundGroup.DRIPSTONE_BLOCK, SupportedSoundTypes.DRIPSTONE_BLOCK);
        register(BlockSoundGroup.POINTED_DRIPSTONE, SupportedSoundTypes.POINTED_DRIPSTONE);
        register(BlockSoundGroup.COPPER, SupportedSoundTypes.COPPER);
        register(BlockSoundGroup.CAVE_VINES, SupportedSoundTypes.CAVE_VINES);
        register(BlockSoundGroup.SPORE_BLOSSOM, SupportedSoundTypes.SPORE_BLOSSOM);
        register(BlockSoundGroup.AZALEA, SupportedSoundTypes.AZALEA);
        register(BlockSoundGroup.FLOWERING_AZALEA, SupportedSoundTypes.FLOWERING_AZALEA);
        register(BlockSoundGroup.MOSS_BLOCK, SupportedSoundTypes.MOSS);
        register(BlockSoundGroup.MOSS_CARPET, SupportedSoundTypes.MOSS_CARPET);
        register(BlockSoundGroup.SMALL_DRIPLEAF, SupportedSoundTypes.SMALL_DRIPLEAF);
        register(BlockSoundGroup.BIG_DRIPLEAF, SupportedSoundTypes.BIG_DRIPLEAF);
        register(BlockSoundGroup.HANGING_ROOTS, SupportedSoundTypes.HANGING_ROOTS);
        register(BlockSoundGroup.AZALEA_LEAVES, SupportedSoundTypes.AZALEA_LEAVES);
        register(BlockSoundGroup.SCULK_SENSOR, SupportedSoundTypes.SCULK_SENSOR);
        register(BlockSoundGroup.SCULK_CATALYST, SupportedSoundTypes.SCULK_CATALYST);
        register(BlockSoundGroup.SCULK, SupportedSoundTypes.SCULK);
        register(BlockSoundGroup.SCULK_VEIN, SupportedSoundTypes.SCULK_VEIN);
        register(BlockSoundGroup.SCULK_SHRIEKER, SupportedSoundTypes.SCULK_SHRIEKER);
        register(BlockSoundGroup.GLOW_LICHEN, SupportedSoundTypes.GLOW_LICHEN);
        register(BlockSoundGroup.DEEPSLATE, SupportedSoundTypes.DEEPSLATE);
        register(BlockSoundGroup.DEEPSLATE_BRICKS, SupportedSoundTypes.DEEPSLATE_BRICKS);
        register(BlockSoundGroup.DEEPSLATE_TILES, SupportedSoundTypes.DEEPSLATE_TILES);
        register(BlockSoundGroup.POLISHED_DEEPSLATE, SupportedSoundTypes.POLISHED_DEEPSLATE);
        register(BlockSoundGroup.FROGLIGHT, SupportedSoundTypes.FROGLIGHT);
        register(BlockSoundGroup.FROGSPAWN, SupportedSoundTypes.FROGSPAWN);
        register(BlockSoundGroup.MANGROVE_ROOTS, SupportedSoundTypes.MANGROVE_ROOTS);
        register(BlockSoundGroup.MUDDY_MANGROVE_ROOTS, SupportedSoundTypes.MUDDY_MANGROVE_ROOTS);
        register(BlockSoundGroup.MUD, SupportedSoundTypes.MUD);
        register(BlockSoundGroup.MUD_BRICKS, SupportedSoundTypes.MUD_BRICKS);
        register(BlockSoundGroup.PACKED_MUD, SupportedSoundTypes.PACKED_MUD);
    }
}
