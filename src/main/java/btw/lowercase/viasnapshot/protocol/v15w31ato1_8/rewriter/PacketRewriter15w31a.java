package btw.lowercase.viasnapshot.protocol.v15w31ato1_8.rewriter;

import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.Protocol15w31a_To1_8;
import btw.lowercase.viasnapshot.protocol.v15w31ato1_8.packet.ServerboundPackets15w31a;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.BlockPosition;
import com.viaversion.viaversion.api.minecraft.EulerAngle;
import com.viaversion.viaversion.api.minecraft.Vector;
import com.viaversion.viaversion.api.minecraft.entitydata.EntityData;
import com.viaversion.viaversion.api.minecraft.entitydata.types.EntityDataTypes1_8;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.provider.HandItemProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// https://wiki.vg/index.php?title=Pre-release_protocol&direction=prev&oldid=6740
public class PacketRewriter15w31a {
	public static void register(final Protocol15w31a_To1_8 protocol) {
		// NOTE/TODO: Entity action no longer sends 6 for open inventory
		// TODO: client status contains open inventory now, needs to send entity action to 1.8 server

		protocol.registerClientbound(ClientboundPackets1_8.CHAT, wrapper -> {
			final String message = wrapper.read(Types.STRING);
			Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, message);
		});

		protocol.registerClientbound(ClientboundPackets1_8.SET_EQUIPPED_ITEM, wrapper -> {
			wrapper.passthrough(Types.VAR_INT); // Entity ID
			int slot = wrapper.read(Types.SHORT); // Slot
			if (slot > 0) {
				slot++;
			}

			wrapper.write(Types.BYTE, (byte) slot);
			wrapper.passthrough(Types.ITEM1_8); // Item
		});

		protocol.registerClientbound(ClientboundPackets1_8.ADD_PLAYER, wrapper -> {
			wrapper.passthrough(Types.VAR_INT); // ID
			wrapper.passthrough(Types.UUID); // UUID
			wrapper.passthrough(Types.INT); // X
			wrapper.passthrough(Types.INT); // Y
			wrapper.passthrough(Types.INT); // Z
			wrapper.passthrough(Types.BYTE); // Yaw
			wrapper.passthrough(Types.BYTE); // Pitch
			wrapper.read(Types.SHORT); // Hand Stack ID? (1.8 server)

			final List<EntityData> entityData = wrapper.read(Types.ENTITY_DATA_LIST1_8);
			wrapper.write(Types.ENTITY_DATA_LIST1_8, handleEntityData(entityData));
		});

		protocol.registerClientbound(ClientboundPackets1_8.ADD_ENTITY, wrapper -> {
			wrapper.passthrough(Types.VAR_INT); // IO
			wrapper.write(Types.UUID, UUID.randomUUID()); // UUID (1.9+)
			wrapper.passthrough(Types.BYTE); // type
			wrapper.passthrough(Types.INT); // x
			wrapper.passthrough(Types.INT); // y
			wrapper.passthrough(Types.INT); // Z
			wrapper.passthrough(Types.BYTE); // pitch
			wrapper.passthrough(Types.BYTE); // yaw
			final int dataId = wrapper.passthrough(Types.INT); // data id
			if (dataId > 0) {
				wrapper.passthrough(Types.SHORT);
				wrapper.passthrough(Types.SHORT);
				wrapper.passthrough(Types.SHORT);
			} else {
				wrapper.write(Types.SHORT, (short) 0);
				wrapper.write(Types.SHORT, (short) 0);
				wrapper.write(Types.SHORT, (short) 0);
			}
		});

		protocol.registerClientbound(ClientboundPackets1_8.ADD_MOB, wrapper -> {
			wrapper.passthrough(Types.VAR_INT); // ID
			wrapper.write(Types.UUID, UUID.randomUUID()); // UUID (1.9+)
			wrapper.passthrough(Types.UNSIGNED_BYTE); // Entity Type ID
			wrapper.passthrough(Types.INT); // X
			wrapper.passthrough(Types.INT); // Y
			wrapper.passthrough(Types.INT); // Z
			wrapper.passthrough(Types.BYTE); // Yaw
			wrapper.passthrough(Types.BYTE); // Pitch
			wrapper.passthrough(Types.BYTE); // Head Yaw
			wrapper.passthrough(Types.SHORT); // Velocity X
			wrapper.passthrough(Types.SHORT); // Velocity Y
			wrapper.passthrough(Types.SHORT); // Velocity Z

			final List<EntityData> entityData = wrapper.read(Types.ENTITY_DATA_LIST1_8);
			wrapper.write(Types.ENTITY_DATA_LIST1_8, handleEntityData(entityData));
		});

		// TODO: Remap entity data
		protocol.registerClientbound(ClientboundPackets1_8.SET_ENTITY_DATA, wrapper -> {
			wrapper.passthrough(Types.VAR_INT); // Entity Id

			final List<EntityData> entityData = wrapper.read(Types.ENTITY_DATA_LIST1_8);
			wrapper.write(Types.ENTITY_DATA_LIST1_8, handleEntityData(entityData));
		});

		protocol.registerClientbound(ClientboundPackets1_8.OPEN_SCREEN, wrapper -> {
			wrapper.passthrough(Types.UNSIGNED_BYTE); // Window id
			wrapper.passthrough(Types.STRING); // Window type
			Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, wrapper.read(Types.STRING)); // Window title
			wrapper.passthrough(Types.UNSIGNED_BYTE); // Number of slots
		});

		protocol.registerClientbound(ClientboundPackets1_8.UPDATE_SIGN, wrapper -> {
			wrapper.passthrough(Types.BLOCK_POSITION1_8); // Block Position
			Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, wrapper.read(Types.STRING)); // Line 1
			Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, wrapper.read(Types.STRING)); // Line 2
			Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, wrapper.read(Types.STRING)); // Line 3
			Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, wrapper.read(Types.STRING)); // Line 4
		});

		protocol.registerClientbound(ClientboundPackets1_8.DISCONNECT, wrapper -> {
			Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, wrapper.read(Types.STRING)); // Kick Message
		});

		// TODO: Issues in 1.21?
		protocol.registerClientbound(ClientboundPackets1_8.SET_TITLES, wrapper -> {
			final int action = wrapper.passthrough(Types.VAR_INT); // Action
			if (action == 0 || action == 1) {
				Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, wrapper.read(Types.STRING));
			}
		});

		// TODO: Issues in 1.21?
		protocol.registerClientbound(ClientboundPackets1_8.TAB_LIST, wrapper -> {
			Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, wrapper.read(Types.STRING)); // Title Text
			Protocol1_8To1_9.STRING_TO_JSON.write(wrapper, wrapper.read(Types.STRING)); // Footer Text
		});

		// TODO: Issues in 1.21?
		protocol.registerServerbound(ServerboundPackets15w31a.INTERACT, wrapper -> {
			wrapper.passthrough(Types.VAR_INT); // Target
			final int type = wrapper.passthrough(Types.VAR_INT); // Type
			if (type == 2 /* interact at */) {
				wrapper.passthrough(Types.FLOAT); // Target x
				wrapper.passthrough(Types.FLOAT); // Target y
				wrapper.passthrough(Types.FLOAT); // Target z
			}

			if (type == 2 /* interact at */ || type == 0 /* interact */) {
				wrapper.read(Types.BYTE);
			}
		});

		protocol.registerServerbound(ServerboundPackets15w31a.PLAYER_ACTION, wrapper -> {
			final byte status = wrapper.passthrough(Types.BYTE);
			if (status == 6) {
				wrapper.cancel(); // Disable off-hand key-bind/action
			}
		});

		protocol.registerServerbound(ServerboundPackets15w31a.CONTAINER_CLICK, wrapper -> {
			wrapper.passthrough(Types.UNSIGNED_BYTE); // Window id
			final int slot = wrapper.passthrough(Types.SHORT); // Slot
			if (slot == 45) {
				// TODO: set cursor back to item
				wrapper.cancel(); // Ignore offhand slot
			}

			wrapper.passthrough(Types.BYTE); // Button
			wrapper.passthrough(Types.SHORT); // Action number
			wrapper.passthrough(Types.BYTE); // Mode
			wrapper.passthrough(Types.ITEM1_8); // Clicked item
		});

		protocol.registerServerbound(ServerboundPackets15w31a.USE_ITEM, null, wrapper -> {
			wrapper.cancel();

			// NOTE: Possibly bannable/noticeable by anti-cheats?
			final Item item = Via.getManager().getProviders().get(HandItemProvider.class).getHandItem(wrapper.user());
			if (item != null && !item.isEmpty()) {
				final PacketWrapper useItemOn = PacketWrapper.create(ServerboundPackets1_8.USE_ITEM_ON, wrapper.user());
				useItemOn.write(Types.BLOCK_POSITION1_8, new BlockPosition(-1, -1, -1)); // Block Position
				useItemOn.write(Types.UNSIGNED_BYTE, (short) 255); // Direction
				useItemOn.write(Types.ITEM1_8, item); // Item
				useItemOn.write(Types.UNSIGNED_BYTE, (short) 0); // X
				useItemOn.write(Types.UNSIGNED_BYTE, (short) 0); // Y
				useItemOn.write(Types.UNSIGNED_BYTE, (short) 0); // Z
				useItemOn.sendToServer(Protocol15w31a_To1_8.class);
			} else {
				System.out.printf("Item was null! %s%n", item == null ? "null" : item);
			}
		});

		protocol.registerServerbound(ServerboundPackets15w31a.USE_ITEM_ON, wrapper -> {
			wrapper.passthrough(Types.BLOCK_POSITION1_8); // Block Position
			wrapper.passthroughAndMap(Types.VAR_INT, Types.UNSIGNED_BYTE); // Direction
			wrapper.read(Types.VAR_INT); // Hand
			wrapper.write(Types.ITEM1_8, Via.getManager().getProviders().get(HandItemProvider.class).getHandItem(wrapper.user())); // Item;
			wrapper.passthrough(Types.UNSIGNED_BYTE); // X
			wrapper.passthrough(Types.UNSIGNED_BYTE); // Y
			wrapper.passthrough(Types.UNSIGNED_BYTE); // Z
		});

		protocol.registerServerbound(ServerboundPackets15w31a.SWING, wrapper -> {
			wrapper.read(Types.BYTE); // Hand (Skip, since 1.8 doesn't have this)
		});

		protocol.registerServerbound(ServerboundPackets15w31a.CLIENT_INFORMATION, wrapper -> {
			wrapper.passthrough(Types.STRING); // Locale
			wrapper.passthrough(Types.BYTE); // View Distance
			wrapper.passthrough(Types.BYTE); // Chat Mode
			wrapper.passthrough(Types.BOOLEAN); // Chat Colors
			wrapper.passthrough(Types.UNSIGNED_BYTE); // Skin Parts
			wrapper.read(Types.BYTE); // Hand (Ignore for 1.8)
		});
	}

	private static List<EntityData> handleEntityData(List<EntityData> entityData) {
		List<EntityData> newData = new ArrayList<>();
		for (final EntityData data : entityData) {
			final Object value = data.getValue();
			if (value == null) {
				continue;
			}

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
