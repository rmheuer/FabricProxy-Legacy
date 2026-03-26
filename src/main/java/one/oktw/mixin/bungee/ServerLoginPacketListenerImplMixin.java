package one.oktw.mixin.bungee;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import one.oktw.interfaces.BungeeClientConnection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {
  @Shadow @Final private Connection connection;

  @Shadow private GameProfile authenticatedProfile;

  @Inject(method = "handleHello", at = @At(value = "TAIL"))
  private void initUuid(ServerboundHelloPacket packet, CallbackInfo ci) {
    // override game profile with saved information:
    Multimap<String, Property> propertiesMap = HashMultimap.create();

    if (((BungeeClientConnection) connection).fabricProxy$getSpoofedProfile() != null) {
      for (Property property :
          ((BungeeClientConnection) connection).fabricProxy$getSpoofedProfile()) {
        propertiesMap.put(property.name(), property);
      }
    }

    PropertyMap properties = new PropertyMap(propertiesMap);
    this.authenticatedProfile =
        new GameProfile(
            ((BungeeClientConnection) connection).fabricProxy$getSpoofedUUID(),
            this.authenticatedProfile.name(),
            properties);
  }

  @Redirect(
      method = "handleHello",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/server/MinecraftServer;usesAuthentication()Z"))
  private boolean skipKeyPacket(MinecraftServer minecraftServer) {
    return false;
  }
}
