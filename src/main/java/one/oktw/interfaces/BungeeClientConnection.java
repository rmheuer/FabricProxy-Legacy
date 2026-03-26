package one.oktw.interfaces;

import com.mojang.authlib.properties.Property;

import java.util.UUID;

public interface BungeeClientConnection {
  UUID fabricProxy$getSpoofedUUID();

  void fabricProxy$setSpoofedUUID(UUID uuid);

  Property[] fabricProxy$getSpoofedProfile();

  void fabricProxy$setSpoofedProfile(Property[] profile);
}
