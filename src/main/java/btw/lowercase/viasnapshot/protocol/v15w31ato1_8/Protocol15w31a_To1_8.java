package btw.lowercase.viasnapshot.protocol.v15w31ato1_8;

import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.packet.ClientboundPackets15w31a;
import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.packet.ServerboundPackets15w31a;
import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.rewriter.PacketRewriter15w31a;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.provider.HandItemProvider;
import com.viaversion.viaversion.protocols.v1_8to1_9.storage.EntityTracker1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.storage.InventoryTracker;

public class Protocol15w31a_To1_8 extends AbstractProtocol<ClientboundPackets1_8, ClientboundPackets15w31a, ServerboundPackets1_8, ServerboundPackets15w31a> {
	public Protocol15w31a_To1_8() {
		super(ClientboundPackets1_8.class, ClientboundPackets15w31a.class, ServerboundPackets1_8.class, ServerboundPackets15w31a.class);
	}

	@Override
	protected void registerPackets() {
		PacketRewriter15w31a.register(this);
	}

	@Override
	public void register(ViaProviders providers) {
		providers.register(HandItemProvider.class, new HandItemProvider());
	}

	@Override
	public void init(UserConnection connection) {
		connection.addEntityTracker(this.getClass(), new EntityTracker1_9(connection));
		connection.put(new InventoryTracker());
	}
}
