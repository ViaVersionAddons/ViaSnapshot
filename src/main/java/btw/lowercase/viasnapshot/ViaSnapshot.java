package btw.lowercase.viasnapshot;

import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.Protocol15w31a_To1_8;
import btw.lowercase.viasnapshot.protocol.v15w31bto15w31a.Protocol15w31b_To15w31a;
import btw.lowercase.viasnapshot.protocol.v15w31cto15w31b.Protocol15w31c_To15w31b;
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
    private void onInitializeEvent(ProtocolTranslatorInitEvent event) {
        event.registerPlatform(() -> {
            // Adds this to the VV dump
            Via.getManager().getSubPlatforms().add(String.format("git-ViaSnapshot-%s", getVersion()));
            final ProtocolManager protocolManager = Via.getManager().getProtocolManager();
            protocolManager.registerProtocol(new Protocol15w31a_To1_8(), SnapshotProtocolVersion.v15w31a, ProtocolVersion.v1_8);
            protocolManager.registerProtocol(new Protocol15w31b_To15w31a(), SnapshotProtocolVersion.v15w31b, SnapshotProtocolVersion.v15w31a);
            protocolManager.registerProtocol(new Protocol15w31c_To15w31b(), SnapshotProtocolVersion.v15w31c, SnapshotProtocolVersion.v15w31b);
            return null;
        });
    }
}
