package btw.lowercase.viasnapshot.protocol.v15w31cto15w31b;

import btw.lowercase.viasnapshot.protocol.v15w31bto15w31a.packet.ClientboundPackets15w31b;
import btw.lowercase.viasnapshot.protocol.v15w31bto15w31a.packet.ServerboundPackets15w31b;
import btw.lowercase.viasnapshot.protocol.v15w31cto15w31b.packet.ClientboundPackets15w31c;
import btw.lowercase.viasnapshot.protocol.v15w31cto15w31b.packet.ServerboundPackets15w31c;
import btw.lowercase.viasnapshot.protocol.v15w31cto15w31b.rewriter.PacketRewriter15w31c;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;

// https://wiki.vg/index.php?title=Pre-release_protocol&oldid=6780
public class Protocol15w31c_To15w31b extends AbstractProtocol<ClientboundPackets15w31b, ClientboundPackets15w31c, ServerboundPackets15w31b, ServerboundPackets15w31c> {
    public Protocol15w31c_To15w31b() {
        super(ClientboundPackets15w31b.class, ClientboundPackets15w31c.class, ServerboundPackets15w31b.class, ServerboundPackets15w31c.class);
    }

    @Override
    protected void registerPackets() {
        PacketRewriter15w31c.register(this);
    }
}
