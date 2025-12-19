package btw.lowercase.viasnapshot;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocol.SpecialProtocolVersion;

public class SnapshotProtocolVersion {
    public static final ProtocolVersion v15w31a = registerSnapshot1_8(49, "15w31a (Client Only)");
    public static final ProtocolVersion v15w31b = registerSnapshot1_8(50, "15w31b (Client Only)");
    public static final ProtocolVersion v15w31c = registerSnapshot1_8(51, "15w31c (Client Only)");

    private static ProtocolVersion registerSnapshot1_8(final int version, final String name) {
        final ProtocolVersion protocolVersion = new SpecialProtocolVersion(version, name, ProtocolVersion.v1_8);
        ProtocolVersion.register(protocolVersion);
        return protocolVersion;
    }
}
