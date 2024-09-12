package btw.lowercase.viasnapshot.protocol.v15w31ato1_8.rewriter;

import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.Protocol15w31a_To1_8;
import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.packet.ServerboundPackets15w31a;
import com.viaversion.viaversion.api.minecraft.BlockPosition;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.libs.gson.Gson;
import com.viaversion.viaversion.libs.gson.GsonBuilder;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_8;

import java.util.List;
import java.util.UUID;

// https://wiki.vg/index.php?title=Pre-release_protocol&direction=prev&oldid=6740
public class PacketRewriter15w31a {
    private static Gson gson = new GsonBuilder().setLenient().create();

    public static void register(final Protocol15w31a_To1_8 protocol) {
        // TODO: fix 1.8.x, weird json issue / race
        // TODO: ignore status 6 in packet 0x07 (swap offhand)
        // NOTE/TODO: Entity action no longer sends 6 for open inventory
        // TODO: client status contains open inventory now, needs to send entity action to 1.8 server

        protocol.registerClientbound(ClientboundPackets1_8.ADD_PLAYER, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.VAR_INT); // ID
                map(Types.UUID); // UUID
                map(Types.INT); // X
                map(Types.INT); // Y
                map(Types.INT); // Z
                map(Types.BYTE); // Yaw
                map(Types.BYTE); // Pitch
                read(Types.SHORT); // Hand Stack ID? (1.8 server)
                map(Types1_8.ENTITY_DATA_LIST);
                // TODO: Fix entity data
                handler(wrapper -> {
                    wrapper.set(Types1_8.ENTITY_DATA_LIST, 0, List.of());
                });
            }
        });

        protocol.registerClientbound(ClientboundPackets1_8.ADD_ENTITY, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.VAR_INT); // IO
                create(Types.UUID, UUID.randomUUID()); // UUID (1.9+)
                map(Types.BYTE); // type
                map(Types.INT); // x
                map(Types.INT); // y
                map(Types.INT); // Z
                map(Types.BYTE); // pitch
                map(Types.BYTE); // yaw
                map(Types.INT); // data id
                handler(wrapper -> {
                    if (wrapper.get(Types.INT, 3) > 0) {
                        wrapper.passthrough(Types.SHORT);
                        wrapper.passthrough(Types.SHORT);
                        wrapper.passthrough(Types.SHORT);
                    } else {
                        wrapper.write(Types.SHORT, (short) 0);
                        wrapper.write(Types.SHORT, (short) 0);
                        wrapper.write(Types.SHORT, (short) 0);
                    }
                });
            }
        });

        protocol.registerClientbound(ClientboundPackets1_8.ADD_MOB, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.VAR_INT); // ID
                create(Types.UUID, UUID.randomUUID()); // UUID (1.9+)
                map(Types.UNSIGNED_BYTE); // Entity Type ID
                map(Types.INT); // X
                map(Types.INT); // Y
                map(Types.INT); // Z
                map(Types.BYTE); // Yaw
                map(Types.BYTE); // Pitch
                map(Types.BYTE); // Head Yaw
                map(Types.SHORT); // Velocity X
                map(Types.SHORT); // Velocity Y
                map(Types.SHORT); // Velocity Z
                map(Types1_8.ENTITY_DATA_LIST);
                // TODO: Fix entity data
                handler(wrapper -> {
                    wrapper.set(Types1_8.ENTITY_DATA_LIST, 0, List.of());
                });
            }
        });

        protocol.registerClientbound(ClientboundPackets1_8.SET_EQUIPPED_ITEM, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.VAR_INT); // Entity ID
                handler(wrapper -> {
                    int slot = wrapper.read(Types.SHORT); // Slot
                    if (slot > 0) {
                        slot++;
                    }
//					else {
//						slot = 1; // TODO: possible config option to make everyone offhand
//					}
                    wrapper.write(Types.BYTE, (byte) slot);
                });
                map(Types.ITEM1_8); // Item
            }
        });

        protocol.registerServerbound(ServerboundPackets15w31a.INTERACT, new PacketHandlers() {
            @Override
            public void register() {
                map(Types.VAR_INT); // Target
                map(Types.VAR_INT); // Type
                handler(wrapper -> {
                    final int type = wrapper.get(Types.VAR_INT, 1);
                    if (type == 2 /* interact at */) {
                        wrapper.passthrough(Types.FLOAT); // Target x
                        wrapper.passthrough(Types.FLOAT); // Target y
                        wrapper.passthrough(Types.FLOAT); // Target z
                    }
                    if (type == 2 /* interact at */ || type == 0 /* interact */) {
                        wrapper.read(Types.BYTE);
                    }
                });
            }
        });

        protocol.registerServerbound(ServerboundPackets15w31a.CONTAINER_CLICK, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.UNSIGNED_BYTE); // Window id
                map(Types.SHORT); // Slot
                map(Types.BYTE); // Button
                map(Types.SHORT); // Action number
                map(Types.BYTE); // Mode
                map(Types.ITEM1_8); // Clicked item
                handler(wrapper -> {
                    short slot = wrapper.get(Types.SHORT, 0);
                    if (slot == 45) {
                        // TODO: set cursor back to item
                        wrapper.cancel(); // Ignore offhand slot
                    }
                });
            }
        });

        protocol.registerServerbound(ServerboundPackets15w31a.USE_ITEM, null, wrapper -> {
            wrapper.cancel();
            // NOTE: Possibly bannable/noticable by anti-cheats?
            final PacketWrapper useItemOn = PacketWrapper.create(ServerboundPackets1_8.USE_ITEM_ON, wrapper.user());
            useItemOn.write(Types.BLOCK_POSITION1_8, new BlockPosition(0, 0, 0)); // Block Position
            useItemOn.write(Types.UNSIGNED_BYTE, (short) -1); // Direction
            useItemOn.write(Types.ITEM1_8, new DataItem()); // Item
            useItemOn.write(Types.UNSIGNED_BYTE, (short) 0); // X
            useItemOn.write(Types.UNSIGNED_BYTE, (short) 0); // Y
            useItemOn.write(Types.UNSIGNED_BYTE, (short) 0); // Z
            useItemOn.sendToServer(Protocol15w31a_To1_8.class);
        });

        protocol.registerServerbound(ServerboundPackets15w31a.USE_ITEM_ON, new PacketHandlers() {
            @Override
            public void register() {
                map(Types.BLOCK_POSITION1_8); // Block Position
                map(Types.VAR_INT, Types.UNSIGNED_BYTE); // Direction
                read(Types.VAR_INT); // Hand
                create(Types.ITEM1_8, new DataItem()); // Item;
                map(Types.UNSIGNED_BYTE); // X
                map(Types.UNSIGNED_BYTE); // Y
                map(Types.UNSIGNED_BYTE); // Z
            }
        });

        protocol.registerServerbound(ServerboundPackets15w31a.SWING, wrapper -> {
            wrapper.read(Types.BYTE); // Hand (Skip, since 1.8 doesn't have this)
        });

        protocol.registerServerbound(ServerboundPackets15w31a.CLIENT_INFORMATION, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.STRING);        // Locale
                map(Types.BYTE);          // View Distance
                map(Types.BYTE);          // Chat Mode
                map(Types.BOOLEAN);       // Chat Colors
                map(Types.UNSIGNED_BYTE); // Skin Parts
                read(Types.BYTE);         // Hand (Ignore for 1.8)
            }
        });

        // Workarounds / Broken stuff
        {
            // Workaround/Fix for some servers (currently not ideal)
            protocol.registerClientbound(ClientboundPackets1_8.CHAT, wrapper -> {
                String data = wrapper.read(Types.STRING);
                byte unknown = wrapper.read(Types.BYTE);
                try {
                    JsonObject object = gson.fromJson(data, JsonObject.class);
                    wrapper.write(Types.COMPONENT, object);
                    wrapper.write(Types.BYTE, unknown);
                } catch (Exception exception) {
                    wrapper.cancel();
                }
            });

            // ? causes weird kick
            protocol.registerClientbound(ClientboundPackets1_8.SET_TITLES, PacketWrapper::cancel);

            // Causes byte kick (1.8.x servers)
            protocol.registerClientbound(ClientboundPackets1_8.TAB_LIST, PacketWrapper::cancel);

            // TODO: Fix entity data
            protocol.registerClientbound(ClientboundPackets1_8.SET_ENTITY_DATA, PacketWrapper::cancel);

            // (can cause json issues)
            protocol.registerClientbound(ClientboundPackets1_8.UPDATE_SIGN, PacketWrapper::cancel);
        }
    }
}
