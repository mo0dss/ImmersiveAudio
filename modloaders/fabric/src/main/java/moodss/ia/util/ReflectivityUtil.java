package moodss.ia.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import moodss.ia.ImmersiveAudio;
import moodss.ia.ray.BlockRayHitResult;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Util;

import java.util.Map;

//TODO: Move reflectivity and raytracing completely off-thread.
//TODO: Remake, ugly and slow
public class ReflectivityUtil {
    
    private static final Map<BlockSoundGroup, String> group2name = Util.make(new Object2ObjectOpenHashMap<>(), (map) -> {
       map.put(BlockSoundGroup.STONE, "stone");
       map.put(BlockSoundGroup.NETHERITE, "netherite_block");
       map.put(BlockSoundGroup.TUFF, "tuff");
       map.put(BlockSoundGroup.AMETHYST_BLOCK, "amethyst");
       map.put(BlockSoundGroup.BASALT, "basalt");
       map.put(BlockSoundGroup.CALCITE, "calcite");
       map.put(BlockSoundGroup.BONE, "bone_block");
       map.put(BlockSoundGroup.COPPER, "copper");
       map.put(BlockSoundGroup.DEEPSLATE, "deepslate");
       map.put(BlockSoundGroup.DEEPSLATE_BRICKS, "deepslate_bricks");
       map.put(BlockSoundGroup.DEEPSLATE_TILES,"deepslate_tiles");
       map.put(BlockSoundGroup.POLISHED_DEEPSLATE, "polished_deepslate");
       map.put(BlockSoundGroup.NETHER_BRICKS, "nether_bricks");
       map.put(BlockSoundGroup.NETHERRACK, "netherrack");
       map.put(BlockSoundGroup.NETHER_GOLD_ORE, "nether_gold_ore");
       map.put(BlockSoundGroup.NETHER_ORE, "nether_ore");
       map.put(BlockSoundGroup.STEM, "stem");
       map.put(BlockSoundGroup.WOOL, "wool");
       map.put(BlockSoundGroup.HONEY, "honey_block");
       map.put(BlockSoundGroup.MOSS_BLOCK, "moss");
       map.put(BlockSoundGroup.SOUL_SAND, "soul_sand");
       map.put(BlockSoundGroup.SOUL_SOIL,"soul_soil");
       map.put(BlockSoundGroup.CORAL,  "coral_block");
       map.put(BlockSoundGroup.METAL, "metal");
       map.put(BlockSoundGroup.WOOD, "wood");
       map.put(BlockSoundGroup.GRAVEL, "gravel");
       map.put(BlockSoundGroup.GRASS, "grass");
       map.put(BlockSoundGroup.GLASS, "glass");
       map.put(BlockSoundGroup.SAND, "sand");
       map.put(BlockSoundGroup.SNOW, "snow");
    });
    
    public static float getReflectivity0(BlockRayHitResult result) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockState state = client.world.getBlockState(result.getPos());

        Map<String, Float> map = ImmersiveAudio.CONFIG.reflectivity.surfaceReflectivity;
        float def = map.get("all");
        
        return map.getOrDefault(group2name.get(state.getSoundGroup()), def);
    }
}
