package net.TheElm.project.mixins.Server;

import net.TheElm.project.config.SewingMachineConfig;
import net.TheElm.project.utilities.CasingUtils;
import net.TheElm.project.utilities.IntUtils;
import net.TheElm.project.utilities.MessageUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.text.NumberFormat;

@Mixin(DamageTracker.class)
public abstract class DeathMessages {
    
    @Shadow @Final
    private LivingEntity entity;
    
    @Inject(at = @At("RETURN"), method = "getDeathMessage", cancellable = true)
    public void onFetchMessage(CallbackInfoReturnable<Text> callback) {
        if (this.entity instanceof PlayerEntity) {
            long worldDay = IntUtils.timeToDays(this.entity.getEntityWorld());
            long worldYear = worldDay / SewingMachineConfig.INSTANCE.CALENDAR_DAYS.get();
            worldDay = worldDay - (worldYear * SewingMachineConfig.INSTANCE.CALENDAR_DAYS.get());
    
            NumberFormat formatter = NumberFormat.getInstance();
            String year = CasingUtils.Acronym(SewingMachineConfig.INSTANCE.CALENDAR_YEAR_EPOCH.get(), true);
            
            Text yearText = new LiteralText(formatter.format(worldYear));
            if (!year.isEmpty()) {
                yearText.append(" " + year);
                yearText.styled(MessageUtils.simpleHoverText(SewingMachineConfig.INSTANCE.CALENDAR_YEAR_EPOCH.get()));
            }
            
            Text death = callback.getReturnValue();
            callback.setReturnValue(new LiteralText("On day ")
                .append(new LiteralText(formatter.format(worldDay))
                    .formatted(Formatting.YELLOW)
                    .append(" of year ")
                    .append(yearText))
                .append(", ")
                .append(death));
        }
    }
    
}
