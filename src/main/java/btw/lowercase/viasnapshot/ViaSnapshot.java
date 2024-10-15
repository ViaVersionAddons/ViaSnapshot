package btw.lowercase.viasnapshot;

import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.Protocol15w31a_To1_8;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.ProtocolManager;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.lenni0451.lambdaevents.EventHandler;
import net.raphimc.viaproxy.ViaProxy;
import net.raphimc.viaproxy.plugins.ViaProxyPlugin;
import net.raphimc.viaproxy.plugins.events.ProtocolTranslatorInitEvent;

public class ViaSnapshot extends ViaProxyPlugin {
    @Override
    public void onEnable() {
        ViaProxy.EVENT_MANAGER.register(this);
    }

    @EventHandler
    public void onInitializeEvent(ProtocolTranslatorInitEvent event) {
        event.registerPlatform(() -> {
            // Adds this to the VV dump
            Via.getManager().getSubPlatforms().add(String.format("git-ViaSnapshot-%s", getVersion()));

            final ProtocolManager protocolManager = Via.getManager().getProtocolManager();
            protocolManager.registerProtocol(new Protocol15w31a_To1_8(), SnapshotProtocolVersion.v15w31a, ProtocolVersion.v1_8);
            return null;
        });
    }
}
