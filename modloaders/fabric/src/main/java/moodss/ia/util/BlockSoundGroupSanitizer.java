package moodss.ia.util;

import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.ia.user.SupportedSoundType;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BlockSoundGroupSanitizer implements ImmersiveAudioConfig.Sanitizer {

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

    @Override
    public void sanitizeOcclusion(Map<SupportedSoundType, Float> map) {
        blockOcclusionModifiers.forEach((modifier) -> modifier.apply(map));
    }

    @Override
    public void sanitizeExclusion(Map<SupportedSoundType, Float> map) {
        blockExclusionModifiers.forEach((modifier) -> modifier.apply(map));
    }

    @Override
    public void sanitizeReflectivity(Map<SupportedSoundType, Float> map) {
        blockReflectionModifiers.forEach((modifier) -> modifier.apply(map));
    }

    @FunctionalInterface
    public static interface BlockModifier {
        void apply(Map<SupportedSoundType, Float> map);
    }
}
