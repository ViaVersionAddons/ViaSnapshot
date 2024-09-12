package btw.lowercase.viasnapshot.protocol.v15w31ato1_8.packet;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;

public enum ServerboundPackets15w31a implements ServerboundPacketType {
    KEEP_ALIVE,
    CHAT,
    INTERACT,
    MOVE_PLAYER_STATUS_ONLY,
    MOVE_PLAYER_POS,
    MOVE_PLAYER_ROT,
    MOVE_PLAYER_POS_ROT,
    PLAYER_ACTION,
    USE_ITEM,
    USE_ITEM_ON,
    SET_CARRIED_ITEM,
    SWING,
    PLAYER_COMMAND,
    PLAYER_INPUT,
    CONTAINER_CLOSE,
    CONTAINER_CLICK,
    CONTAINER_ACK,
    SET_CREATIVE_MODE_SLOT,
    CONTAINER_BUTTON_CLICK,
    SIGN_UPDATE,
    PLAYER_ABILITIES,
    COMMAND_SUGGESTION,
    CLIENT_INFORMATION,
    CLIENT_COMMAND,
    CUSTOM_PAYLOAD,
    TELEPORT_TO_ENTITY,
    RESOURCE_PACK;

    private ServerboundPackets15w31a() {
    }

    public int getId() {
        return this.ordinal();
    }

    public String getName() {
        return this.name();
    }
}
