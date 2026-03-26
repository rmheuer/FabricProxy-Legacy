package one.oktw.mixin.bungee;

import com.mojang.authlib.properties.Property;
import net.minecraft.network.Connection;
import one.oktw.interfaces.BungeeClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(Connection.class)
public abstract class ConnectionMixin implements BungeeClientConnection {
  @Unique private UUID spoofedUUID;
  @Unique private Property[] spoofedProfile;

  @Override
  public UUID fabricProxy$getSpoofedUUID() {
    return this.spoofedUUID;
  }

  @Override
  public void fabricProxy$setSpoofedUUID(UUID uuid) {
    this.spoofedUUID = uuid;
  }

  @Override
  public Property[] fabricProxy$getSpoofedProfile() {
    return this.spoofedProfile;
  }

  @Override
  public void fabricProxy$setSpoofedProfile(Property[] profile) {
    this.spoofedProfile = profile;
  }
}
