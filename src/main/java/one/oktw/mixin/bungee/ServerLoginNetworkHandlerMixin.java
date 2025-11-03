package one.oktw.mixin.bungee;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import one.oktw.interfaces.BungeeClientConnection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {

    @Shadow
    @Final
    ClientConnection connection;

    @Shadow
    private GameProfile profile;


    @Inject(method = "onHello", at = @At(value = "TAIL"))
    private void initUuid(LoginHelloC2SPacket packet, CallbackInfo ci) {
        // override game profile with saved information:
        Multimap<String, Property> propertiesMap = HashMultimap.create();
        
        if (((BungeeClientConnection) connection).getSpoofedProfile() != null) {
            for (Property property : ((BungeeClientConnection) connection).getSpoofedProfile()) {
                propertiesMap.put(property.name(), property);
            }
        }
        
        PropertyMap properties = new PropertyMap(propertiesMap);
        this.profile = new GameProfile(((BungeeClientConnection) connection).getSpoofedUUID(), this.profile.name(), properties);
    }

    @Redirect(method = "onHello", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/MinecraftServer;isOnlineMode()Z"))
    private boolean skipKeyPacket(MinecraftServer minecraftServer) {
        return false;
    }
}
