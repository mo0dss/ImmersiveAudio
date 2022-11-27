package moodss.ia.mixins.features.bidirectionalAudio.reverb;

import moodss.ia.ImmersiveAudio;
import moodss.ia.ImmersiveAudioMod;
import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.sfx.openal.source.AlSource;
import net.minecraft.client.sound.Source;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Source.class)
public class SourceMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int pointer, CallbackInfo ci) {
        ImmersiveAudio commons = ImmersiveAudioMod.instance();
        if(!commons.config().eaxReverb) {
            return;
        }

        AlSource source = AlSource.wrap(pointer);
        ImmersiveAudioClientMod.DEVICE.run(context -> {
            commons.eaxReverbController().applyToSource(context, commons.auxiliaryEffectManager(), source);
        });
    }
}
