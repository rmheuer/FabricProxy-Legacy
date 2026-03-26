package one.oktw.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.OldUsersConverter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OldUsersConverter.class)
public class OldUsersConverterMixin {
  @Redirect(
      method = "lookupPlayers",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/server/MinecraftServer;usesAuthentication()Z"))
  private static boolean forceOnlineMode(MinecraftServer server) {
    return true;
  }
}
