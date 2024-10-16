package btw.lowercase.viasnapshot.protocol.v15w31bto15w31a;

import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.packet.ClientboundPackets15w31a;
import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.packet.ServerboundPackets15w31a;
import btw.lowercase.viasnapshot.protocol.v15w31bto15w31a.packet.ClientboundPackets15w31b;
import btw.lowercase.viasnapshot.protocol.v15w31bto15w31a.packet.ServerboundPackets15w31b;
import btw.lowercase.viasnapshot.protocol.v15w31bto15w31a.rewriter.PacketRewriter15w31b;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;

public class Protocol15w31b_To15w31a extends AbstractProtocol<ClientboundPackets15w31a, ClientboundPackets15w31b, ServerboundPackets15w31a, ServerboundPackets15w31b> {
    public Protocol15w31b_To15w31a() {
        super(ClientboundPackets15w31a.class, ClientboundPackets15w31b.class, ServerboundPackets15w31a.class, ServerboundPackets15w31b.class);
    }

    @Override
    protected void registerPackets() {
        PacketRewriter15w31b.register(this);
    }
}
