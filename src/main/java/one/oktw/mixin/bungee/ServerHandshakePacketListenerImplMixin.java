package one.oktw.mixin.bungee;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import one.oktw.Util;
import one.oktw.interfaces.BungeeClientConnection;
import one.oktw.mixin.ConnectionAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(ServerHandshakePacketListenerImpl.class)
public class ServerHandshakePacketListenerImplMixin {
  @Unique private static final Gson gson = new Gson();

  @Shadow @Final private Connection connection;

  @Inject(method = "handleIntention", at = @At("HEAD"))
  private void onProcessHandshakeStart(ClientIntentionPacket packet, CallbackInfo ci) {
    if (ClientIntent.LOGIN.equals(packet.intention())) {
      String[] split = packet.hostName().split("\00");
      if (split.length == 3 || split.length == 4) {
        // override/insert forwarded IP into connection:
        ((ConnectionAccessor) connection)
            .fabricProxy$setAddress(
                new InetSocketAddress(
                    split[1], ((InetSocketAddress) connection.getRemoteAddress()).getPort()));

        // extract forwarded profile information and save them:
        ((BungeeClientConnection) connection).fabricProxy$setSpoofedUUID(Util.fromString(split[2]));

        if (split.length == 4) {
          ((BungeeClientConnection) connection)
              .fabricProxy$setSpoofedProfile(gson.fromJson(split[3], Property[].class));
        }
      } else {
        // no extra information found in the address, disconnecting player:
        Component disconnectMessage =
            Component.literal(
                "Bypassing proxy not allowed! If you wish to use IP forwarding, "
                    + "please enable it in your BungeeCord config as well!");
        connection.send(new ClientboundLoginDisconnectPacket(disconnectMessage));
        connection.disconnect(disconnectMessage);
      }
    }
  }
}
