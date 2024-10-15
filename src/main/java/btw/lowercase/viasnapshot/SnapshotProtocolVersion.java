package btw.lowercase.viasnapshot;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocol.RedirectProtocolVersion;

public class SnapshotProtocolVersion {
    public static final ProtocolVersion v15w31a = registerSnapshot(49, "15w31a (Client Only)", ProtocolVersion.v1_8);

    private static ProtocolVersion registerSnapshot(final int version, final String name, final ProtocolVersion origin) {
        final ProtocolVersion protocolVersion = new RedirectProtocolVersion(version, name, origin);
        ProtocolVersion.register(protocolVersion);
        return protocolVersion;
    }
}
