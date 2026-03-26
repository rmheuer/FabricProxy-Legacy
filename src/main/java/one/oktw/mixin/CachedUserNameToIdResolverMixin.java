package one.oktw.mixin;

import net.minecraft.server.players.CachedUserNameToIdResolver;
import net.minecraft.server.players.NameAndId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(CachedUserNameToIdResolver.class)
public class CachedUserNameToIdResolverMixin {
  @Redirect(
      method = "lookupGameProfile",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/server/players/CachedUserNameToIdResolver;createUnknownProfile(Ljava/lang/String;)Ljava/util/Optional;"))
  private Optional<NameAndId> createUnknownProfile(
      CachedUserNameToIdResolver instance, String username) {
    return Optional.empty();
  }
}
