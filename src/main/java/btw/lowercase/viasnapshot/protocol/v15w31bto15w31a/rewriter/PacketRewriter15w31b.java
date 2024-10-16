package btw.lowercase.viasnapshot.protocol.v15w31bto15w31a.rewriter;

import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.packet.ClientboundPackets15w31a;
import btw.lowercase.viasnapshot.protocol.v15w31bto15w31a.Protocol15w31b_To15w31a;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;

// https://wiki.vg/index.php?title=Pre-release_protocol&direction=prev&oldid=6751
public class PacketRewriter15w31b {
    public static void register(final Protocol15w31b_To15w31a protocol) {
        // Player List Item
        protocol.registerClientbound(ClientboundPackets15w31a.PLAYER_INFO, new PacketHandlers() {
            public void register() {
                map(Types.VAR_INT); // Action
                map(Types.VAR_INT); // Count
                handler((wrapper) -> {
                    final int action = wrapper.get(Types.VAR_INT, 0);
                    final int count = wrapper.get(Types.VAR_INT, 1);
                    for (int i = 0; i < count; ++i) {
                        wrapper.passthrough(Types.UUID); // UUID
                        if (action != 0) {
                            if (action != 1 && action != 2) {
                                // Update Display Name
                                if (action == 3) {
                                    String displayName = wrapper.read(Types.OPTIONAL_STRING);
                                    wrapper.write(Types.OPTIONAL_COMPONENT, displayName != null ? Protocol1_8To1_9.STRING_TO_JSON.transform(wrapper, displayName) : null);
                                }
                            } else {
                                // ?
                                wrapper.passthrough(Types.VAR_INT);
                            }
                        } else {
                            // Add Player
                            wrapper.passthrough(Types.STRING); // Name

                            final int properties = wrapper.passthrough(Types.VAR_INT); // Number of properties
                            for (int j = 0; j < properties; ++j) {
                                wrapper.passthrough(Types.STRING);
                                wrapper.passthrough(Types.STRING);
                                wrapper.passthrough(Types.OPTIONAL_STRING);
                            }

                            wrapper.passthrough(Types.VAR_INT);
                            wrapper.passthrough(Types.VAR_INT);

                            String displayName = wrapper.read(Types.OPTIONAL_STRING);
                            wrapper.write(Types.OPTIONAL_COMPONENT, displayName != null ? Protocol1_8To1_9.STRING_TO_JSON.transform(wrapper, displayName) : null);
                        }
                    }
                });
            }
        });


        protocol.cancelClientbound(ClientboundPackets15w31a.UPDATE_ENTITY_NBT); // TODO: now bossbar
    }
}
