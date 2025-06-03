package btw.lowercase.viasnapshot.protocol.v15w31ato1_8.rewriter;

import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.Protocol15w31a_To1_8;
import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.packet.ServerboundPackets15w31a;
import com.viaversion.viaversion.api.minecraft.BlockPosition;
import com.viaversion.viaversion.api.minecraft.EulerAngle;
import com.viaversion.viaversion.api.minecraft.Vector;
import com.viaversion.viaversion.api.minecraft.entitydata.EntityData;
import com.viaversion.viaversion.api.minecraft.entitydata.types.EntityDataTypes1_8;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_8;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// https://wiki.vg/index.php?title=Pre-release_protocol&direction=prev&oldid=6740
public class PacketRewriter15w31a {
    public static void register(final Protocol15w31a_To1_8 protocol) {
        // NOTE/TODO: Entity action no longer sends 6 for open inventory
        // TODO: client status contains open inventory now, needs to send entity action to 1.8 server

        protocol.registerClientbound(ClientboundPackets1_8.CHAT, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.STRING, Protocol1_8To1_9.STRING_TO_JSON);
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
                handler(wrapper -> {
                    List<EntityData> entityData = wrapper.get(Types1_8.ENTITY_DATA_LIST, 0);
                    if (!entityData.isEmpty()) {
                        wrapper.set(Types1_8.ENTITY_DATA_LIST, 0, handleEntityData(entityData));
                    }
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
                handler(wrapper -> {
                    List<EntityData> entityData = wrapper.get(Types1_8.ENTITY_DATA_LIST, 0);
                    if (!entityData.isEmpty()) {
                        wrapper.set(Types1_8.ENTITY_DATA_LIST, 0, handleEntityData(entityData));
                    }
                });
            }
        });

        // TODO: Remap entity data
        protocol.registerClientbound(ClientboundPackets1_8.SET_ENTITY_DATA, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.VAR_INT); // Entity Id
                map(Types1_8.ENTITY_DATA_LIST);
                handler(wrapper -> {
                    List<EntityData> entityData = wrapper.get(Types1_8.ENTITY_DATA_LIST, 0);
                    if (!entityData.isEmpty()) {
                        wrapper.set(Types1_8.ENTITY_DATA_LIST, 0, handleEntityData(entityData));
                    }
                });
            }
        });

        protocol.registerClientbound(ClientboundPackets1_8.OPEN_SCREEN, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.UNSIGNED_BYTE); // Window id
                map(Types.STRING); // Window type
                map(Types.STRING, Protocol1_8To1_9.STRING_TO_JSON); // Window title
                map(Types.UNSIGNED_BYTE); // Number of slots
            }
        });

        protocol.registerClientbound(ClientboundPackets1_8.UPDATE_SIGN, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.BLOCK_POSITION1_8); // Block Position
                map(Types.STRING, Protocol1_8To1_9.STRING_TO_JSON); // Line 1
                map(Types.STRING, Protocol1_8To1_9.STRING_TO_JSON); // Line 2
                map(Types.STRING, Protocol1_8To1_9.STRING_TO_JSON); // Line 3
                map(Types.STRING, Protocol1_8To1_9.STRING_TO_JSON); // Line 4
            }
        });

        protocol.registerClientbound(ClientboundPackets1_8.DISCONNECT, new PacketHandlers() {
            public void register() {
                map(Types.STRING, Protocol1_8To1_9.STRING_TO_JSON); // Kick Message
            }
        });

        // TODO: Issues in 1.21?
        protocol.registerClientbound(ClientboundPackets1_8.SET_TITLES, new PacketHandlers() {
            public void register() {
                map(Types.VAR_INT); // Action
                handler((wrapper) -> {
                    final int action = wrapper.get(Types.VAR_INT, 0);
                    if (action == 0 || action == 1) {
                        Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, wrapper.read(Types.STRING));
                    }
                });
            }
        });

        // TODO: Issues in 1.21?
        protocol.registerClientbound(ClientboundPackets1_8.TAB_LIST, new PacketHandlers() {
            public void register() {
                map(Types.STRING, Protocol1_8To1_9.STRING_TO_JSON); // Title Text
                map(Types.STRING, Protocol1_8To1_9.STRING_TO_JSON); // Footer Text
            }
        });

        // TODO: Issues in 1.21?
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

        protocol.registerServerbound(ServerboundPackets15w31a.PLAYER_ACTION, new PacketHandlers() {
            @Override
            protected void register() {
                map(Types.BYTE);
                handler(wrapper -> {
                    final byte status = wrapper.get(Types.BYTE, 0);
                    if (status == 6) {
                        wrapper.cancel(); // Disable off-hand key-bind/action
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
                    final short slot = wrapper.get(Types.SHORT, 0);
                    if (slot == 45) {
                        // TODO: set cursor back to item
                        wrapper.cancel(); // Ignore offhand slot
                    }
                });
            }
        });

        protocol.registerServerbound(ServerboundPackets15w31a.USE_ITEM, null, wrapper -> {
            wrapper.cancel();
            // NOTE: Possibly bannable/noticeable by anti-cheats?
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
                map(Types.STRING); // Locale
                map(Types.BYTE); // View Distance
                map(Types.BYTE); // Chat Mode
                map(Types.BOOLEAN); // Chat Colors
                map(Types.UNSIGNED_BYTE); // Skin Parts
                read(Types.BYTE); // Hand (Ignore for 1.8)
            }
        });
    }

    private static List<EntityData> handleEntityData(List<EntityData> entityData) {
        List<EntityData> newData = new ArrayList<>();

        for (EntityData data : entityData) {
            Object value = data.getValue();
            if (value == null)
                continue;
            switch ((EntityDataTypes1_8) data.dataType()) {
                case BYTE:
                    if (data.dataType() == EntityDataTypes1_8.BYTE) {
                        data.setValue(value);
                    }

                    if (data.dataType() == EntityDataTypes1_8.INT) {
                        data.setValue(((Integer) value).byteValue());
                    }

                    if (data.dataType() == EntityDataTypes1_8.FLOAT) {
                        data.setValue(((Float) value).byteValue());
                    }

                    if (data.dataType() == EntityDataTypes1_8.SHORT) {
                        data.setValue(((Short) value).byteValue());
                    }

                    break;
                case FLOAT:
                case STRING:
                case INT:
                case SHORT:
                case ITEM:
                    data.setValue(value);
                    break;
                case BLOCK_POSITION:
                    Vector vector = (Vector) value;
                    data.setValue(vector);
                    break;
                case ROTATIONS:
                    EulerAngle angle = (EulerAngle) value;
                    data.setValue(angle);
                    break;
                default:
                    throw new RuntimeException("Unhandled EntityDataType: " + data.dataType());
            }
        }

        return newData;
    }
}
