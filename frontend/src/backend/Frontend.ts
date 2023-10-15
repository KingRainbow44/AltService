// @generated by protobuf-ts 2.9.1
// @generated from protobuf file "Frontend.proto" (syntax proto3)
// tslint:disable
import type { BinaryWriteOptions } from "@protobuf-ts/runtime";
import type { IBinaryWriter } from "@protobuf-ts/runtime";
import { WireType } from "@protobuf-ts/runtime";
import type { BinaryReadOptions } from "@protobuf-ts/runtime";
import type { IBinaryReader } from "@protobuf-ts/runtime";
import { UnknownFieldHandler } from "@protobuf-ts/runtime";
import type { PartialMessage } from "@protobuf-ts/runtime";
import { reflectionMergePartial } from "@protobuf-ts/runtime";
import { MESSAGE_TYPE } from "@protobuf-ts/runtime";
import { MessageType } from "@protobuf-ts/runtime";
import { Vector3 } from "./Structures";
import { Player } from "./Structures";
/**
 * @generated from protobuf message GetAllSessionsScRsp
 */
export interface GetAllSessionsScRsp {
    /**
     * @generated from protobuf field: repeated Player sessions = 1;
     */
    sessions: Player[];
}
/**
 * @generated from protobuf message UpdateSessionsScNotify
 */
export interface UpdateSessionsScNotify {
    /**
     * @generated from protobuf field: repeated Player sessions = 1;
     */
    sessions: Player[];
}
/**
 * This should be directly forwarded to the target server.
 *
 * @generated from protobuf message SessionActionCsNotify
 */
export interface SessionActionCsNotify {
    /**
     * @generated from protobuf field: string session_id = 1;
     */
    sessionId: string;
    /**
     * @generated from protobuf field: Action action = 2;
     */
    action: Action;
    /**
     * @generated from protobuf oneof: data
     */
    data: {
        oneofKind: "move";
        /**
         * @generated from protobuf field: MoveAction move = 3;
         */
        move: MoveAction;
    } | {
        oneofKind: "target";
        /**
         * @generated from protobuf field: TargetAction target = 4;
         */
        target: TargetAction;
    } | {
        oneofKind: undefined;
    };
}
/**
 * @generated from protobuf message MoveAction
 */
export interface MoveAction {
    /**
     * @generated from protobuf field: Vector3 position = 1;
     */
    position?: Vector3;
    /**
     * @generated from protobuf field: Vector3 direction = 2;
     */
    direction?: Vector3;
}
/**
 * @generated from protobuf message TargetAction
 */
export interface TargetAction {
    /**
     * @generated from protobuf field: string new_target = 1;
     */
    newTarget: string;
    /**
     * @generated from protobuf field: bool guard = 2;
     */
    guard: boolean;
    /**
     * @generated from protobuf field: bool follow = 3;
     */
    follow: boolean;
}
/**
 * This is bi-directional.
 *
 * @generated from protobuf message ChatMessageNotify
 */
export interface ChatMessageNotify {
    /**
     * @generated from protobuf field: string message = 1;
     */
    message: string; // This is the raw message to render. Should include color code.
}
/**
 * @generated from protobuf enum FrontendIds
 */
export enum FrontendIds {
    /**
     * @generated from protobuf enum value: _FrontendJoinCsReq = 0;
     */
    _FrontendJoinCsReq = 0,
    /**
     * @generated from protobuf enum value: _FrontendJoinScRsp = 1;
     */
    _FrontendJoinScRsp = 1,
    /**
     * @generated from protobuf enum value: _GetAllSessionsCsReq = 2;
     */
    _GetAllSessionsCsReq = 2,
    /**
     * @generated from protobuf enum value: _GetAllSessionsScRsp = 3;
     */
    _GetAllSessionsScRsp = 3,
    /**
     * @generated from protobuf enum value: _UpdateSessionsScNotify = 4;
     */
    _UpdateSessionsScNotify = 4,
    /**
     * @generated from protobuf enum value: _SessionActionCsNotify = 5;
     */
    _SessionActionCsNotify = 5,
    /**
     * @generated from protobuf enum value: _ChatMessageNotify = 6;
     */
    _ChatMessageNotify = 6
}
/**
 * @generated from protobuf enum Action
 */
export enum Action {
    /**
     * @generated from protobuf enum value: Reconnect = 0;
     */
    Reconnect = 0,
    /**
     * @generated from protobuf enum value: Disconnect = 1;
     */
    Disconnect = 1,
    /**
     * @generated from protobuf enum value: Move = 2;
     */
    Move = 2,
    /**
     * @generated from protobuf enum value: Target = 3;
     */
    Target = 3
}
// @generated message type with reflection information, may provide speed optimized methods
class GetAllSessionsScRsp$Type extends MessageType<GetAllSessionsScRsp> {
    constructor() {
        super("GetAllSessionsScRsp", [
            { no: 1, name: "sessions", kind: "message", repeat: 1 /*RepeatType.PACKED*/, T: () => Player }
        ]);
    }
    create(value?: PartialMessage<GetAllSessionsScRsp>): GetAllSessionsScRsp {
        const message = { sessions: [] };
        globalThis.Object.defineProperty(message, MESSAGE_TYPE, { enumerable: false, value: this });
        if (value !== undefined)
            reflectionMergePartial<GetAllSessionsScRsp>(this, message, value);
        return message;
    }
    internalBinaryRead(reader: IBinaryReader, length: number, options: BinaryReadOptions, target?: GetAllSessionsScRsp): GetAllSessionsScRsp {
        let message = target ?? this.create(), end = reader.pos + length;
        while (reader.pos < end) {
            let [fieldNo, wireType] = reader.tag();
            switch (fieldNo) {
                case /* repeated Player sessions */ 1:
                    message.sessions.push(Player.internalBinaryRead(reader, reader.uint32(), options));
                    break;
                default:
                    let u = options.readUnknownField;
                    if (u === "throw")
                        throw new globalThis.Error(`Unknown field ${fieldNo} (wire type ${wireType}) for ${this.typeName}`);
                    let d = reader.skip(wireType);
                    if (u !== false)
                        (u === true ? UnknownFieldHandler.onRead : u)(this.typeName, message, fieldNo, wireType, d);
            }
        }
        return message;
    }
    internalBinaryWrite(message: GetAllSessionsScRsp, writer: IBinaryWriter, options: BinaryWriteOptions): IBinaryWriter {
        /* repeated Player sessions = 1; */
        for (let i = 0; i < message.sessions.length; i++)
            Player.internalBinaryWrite(message.sessions[i], writer.tag(1, WireType.LengthDelimited).fork(), options).join();
        let u = options.writeUnknownFields;
        if (u !== false)
            (u == true ? UnknownFieldHandler.onWrite : u)(this.typeName, message, writer);
        return writer;
    }
}
/**
 * @generated MessageType for protobuf message GetAllSessionsScRsp
 */
export const GetAllSessionsScRsp = new GetAllSessionsScRsp$Type();
// @generated message type with reflection information, may provide speed optimized methods
class UpdateSessionsScNotify$Type extends MessageType<UpdateSessionsScNotify> {
    constructor() {
        super("UpdateSessionsScNotify", [
            { no: 1, name: "sessions", kind: "message", repeat: 1 /*RepeatType.PACKED*/, T: () => Player }
        ]);
    }
    create(value?: PartialMessage<UpdateSessionsScNotify>): UpdateSessionsScNotify {
        const message = { sessions: [] };
        globalThis.Object.defineProperty(message, MESSAGE_TYPE, { enumerable: false, value: this });
        if (value !== undefined)
            reflectionMergePartial<UpdateSessionsScNotify>(this, message, value);
        return message;
    }
    internalBinaryRead(reader: IBinaryReader, length: number, options: BinaryReadOptions, target?: UpdateSessionsScNotify): UpdateSessionsScNotify {
        let message = target ?? this.create(), end = reader.pos + length;
        while (reader.pos < end) {
            let [fieldNo, wireType] = reader.tag();
            switch (fieldNo) {
                case /* repeated Player sessions */ 1:
                    message.sessions.push(Player.internalBinaryRead(reader, reader.uint32(), options));
                    break;
                default:
                    let u = options.readUnknownField;
                    if (u === "throw")
                        throw new globalThis.Error(`Unknown field ${fieldNo} (wire type ${wireType}) for ${this.typeName}`);
                    let d = reader.skip(wireType);
                    if (u !== false)
                        (u === true ? UnknownFieldHandler.onRead : u)(this.typeName, message, fieldNo, wireType, d);
            }
        }
        return message;
    }
    internalBinaryWrite(message: UpdateSessionsScNotify, writer: IBinaryWriter, options: BinaryWriteOptions): IBinaryWriter {
        /* repeated Player sessions = 1; */
        for (let i = 0; i < message.sessions.length; i++)
            Player.internalBinaryWrite(message.sessions[i], writer.tag(1, WireType.LengthDelimited).fork(), options).join();
        let u = options.writeUnknownFields;
        if (u !== false)
            (u == true ? UnknownFieldHandler.onWrite : u)(this.typeName, message, writer);
        return writer;
    }
}
/**
 * @generated MessageType for protobuf message UpdateSessionsScNotify
 */
export const UpdateSessionsScNotify = new UpdateSessionsScNotify$Type();
// @generated message type with reflection information, may provide speed optimized methods
class SessionActionCsNotify$Type extends MessageType<SessionActionCsNotify> {
    constructor() {
        super("SessionActionCsNotify", [
            { no: 1, name: "session_id", kind: "scalar", T: 9 /*ScalarType.STRING*/ },
            { no: 2, name: "action", kind: "enum", T: () => ["Action", Action] },
            { no: 3, name: "move", kind: "message", oneof: "data", T: () => MoveAction },
            { no: 4, name: "target", kind: "message", oneof: "data", T: () => TargetAction }
        ]);
    }
    create(value?: PartialMessage<SessionActionCsNotify>): SessionActionCsNotify {
        const message = { sessionId: "", action: 0, data: { oneofKind: undefined } };
        globalThis.Object.defineProperty(message, MESSAGE_TYPE, { enumerable: false, value: this });
        if (value !== undefined)
            reflectionMergePartial<SessionActionCsNotify>(this, message, value);
        return message;
    }
    internalBinaryRead(reader: IBinaryReader, length: number, options: BinaryReadOptions, target?: SessionActionCsNotify): SessionActionCsNotify {
        let message = target ?? this.create(), end = reader.pos + length;
        while (reader.pos < end) {
            let [fieldNo, wireType] = reader.tag();
            switch (fieldNo) {
                case /* string session_id */ 1:
                    message.sessionId = reader.string();
                    break;
                case /* Action action */ 2:
                    message.action = reader.int32();
                    break;
                case /* MoveAction move */ 3:
                    message.data = {
                        oneofKind: "move",
                        move: MoveAction.internalBinaryRead(reader, reader.uint32(), options, (message.data as any).move)
                    };
                    break;
                case /* TargetAction target */ 4:
                    message.data = {
                        oneofKind: "target",
                        target: TargetAction.internalBinaryRead(reader, reader.uint32(), options, (message.data as any).target)
                    };
                    break;
                default:
                    let u = options.readUnknownField;
                    if (u === "throw")
                        throw new globalThis.Error(`Unknown field ${fieldNo} (wire type ${wireType}) for ${this.typeName}`);
                    let d = reader.skip(wireType);
                    if (u !== false)
                        (u === true ? UnknownFieldHandler.onRead : u)(this.typeName, message, fieldNo, wireType, d);
            }
        }
        return message;
    }
    internalBinaryWrite(message: SessionActionCsNotify, writer: IBinaryWriter, options: BinaryWriteOptions): IBinaryWriter {
        /* string session_id = 1; */
        if (message.sessionId !== "")
            writer.tag(1, WireType.LengthDelimited).string(message.sessionId);
        /* Action action = 2; */
        if (message.action !== 0)
            writer.tag(2, WireType.Varint).int32(message.action);
        /* MoveAction move = 3; */
        if (message.data.oneofKind === "move")
            MoveAction.internalBinaryWrite(message.data.move, writer.tag(3, WireType.LengthDelimited).fork(), options).join();
        /* TargetAction target = 4; */
        if (message.data.oneofKind === "target")
            TargetAction.internalBinaryWrite(message.data.target, writer.tag(4, WireType.LengthDelimited).fork(), options).join();
        let u = options.writeUnknownFields;
        if (u !== false)
            (u == true ? UnknownFieldHandler.onWrite : u)(this.typeName, message, writer);
        return writer;
    }
}
/**
 * @generated MessageType for protobuf message SessionActionCsNotify
 */
export const SessionActionCsNotify = new SessionActionCsNotify$Type();
// @generated message type with reflection information, may provide speed optimized methods
class MoveAction$Type extends MessageType<MoveAction> {
    constructor() {
        super("MoveAction", [
            { no: 1, name: "position", kind: "message", T: () => Vector3 },
            { no: 2, name: "direction", kind: "message", T: () => Vector3 }
        ]);
    }
    create(value?: PartialMessage<MoveAction>): MoveAction {
        const message = {};
        globalThis.Object.defineProperty(message, MESSAGE_TYPE, { enumerable: false, value: this });
        if (value !== undefined)
            reflectionMergePartial<MoveAction>(this, message, value);
        return message;
    }
    internalBinaryRead(reader: IBinaryReader, length: number, options: BinaryReadOptions, target?: MoveAction): MoveAction {
        let message = target ?? this.create(), end = reader.pos + length;
        while (reader.pos < end) {
            let [fieldNo, wireType] = reader.tag();
            switch (fieldNo) {
                case /* Vector3 position */ 1:
                    message.position = Vector3.internalBinaryRead(reader, reader.uint32(), options, message.position);
                    break;
                case /* Vector3 direction */ 2:
                    message.direction = Vector3.internalBinaryRead(reader, reader.uint32(), options, message.direction);
                    break;
                default:
                    let u = options.readUnknownField;
                    if (u === "throw")
                        throw new globalThis.Error(`Unknown field ${fieldNo} (wire type ${wireType}) for ${this.typeName}`);
                    let d = reader.skip(wireType);
                    if (u !== false)
                        (u === true ? UnknownFieldHandler.onRead : u)(this.typeName, message, fieldNo, wireType, d);
            }
        }
        return message;
    }
    internalBinaryWrite(message: MoveAction, writer: IBinaryWriter, options: BinaryWriteOptions): IBinaryWriter {
        /* Vector3 position = 1; */
        if (message.position)
            Vector3.internalBinaryWrite(message.position, writer.tag(1, WireType.LengthDelimited).fork(), options).join();
        /* Vector3 direction = 2; */
        if (message.direction)
            Vector3.internalBinaryWrite(message.direction, writer.tag(2, WireType.LengthDelimited).fork(), options).join();
        let u = options.writeUnknownFields;
        if (u !== false)
            (u == true ? UnknownFieldHandler.onWrite : u)(this.typeName, message, writer);
        return writer;
    }
}
/**
 * @generated MessageType for protobuf message MoveAction
 */
export const MoveAction = new MoveAction$Type();
// @generated message type with reflection information, may provide speed optimized methods
class TargetAction$Type extends MessageType<TargetAction> {
    constructor() {
        super("TargetAction", [
            { no: 1, name: "new_target", kind: "scalar", T: 9 /*ScalarType.STRING*/ },
            { no: 2, name: "guard", kind: "scalar", T: 8 /*ScalarType.BOOL*/ },
            { no: 3, name: "follow", kind: "scalar", T: 8 /*ScalarType.BOOL*/ }
        ]);
    }
    create(value?: PartialMessage<TargetAction>): TargetAction {
        const message = { newTarget: "", guard: false, follow: false };
        globalThis.Object.defineProperty(message, MESSAGE_TYPE, { enumerable: false, value: this });
        if (value !== undefined)
            reflectionMergePartial<TargetAction>(this, message, value);
        return message;
    }
    internalBinaryRead(reader: IBinaryReader, length: number, options: BinaryReadOptions, target?: TargetAction): TargetAction {
        let message = target ?? this.create(), end = reader.pos + length;
        while (reader.pos < end) {
            let [fieldNo, wireType] = reader.tag();
            switch (fieldNo) {
                case /* string new_target */ 1:
                    message.newTarget = reader.string();
                    break;
                case /* bool guard */ 2:
                    message.guard = reader.bool();
                    break;
                case /* bool follow */ 3:
                    message.follow = reader.bool();
                    break;
                default:
                    let u = options.readUnknownField;
                    if (u === "throw")
                        throw new globalThis.Error(`Unknown field ${fieldNo} (wire type ${wireType}) for ${this.typeName}`);
                    let d = reader.skip(wireType);
                    if (u !== false)
                        (u === true ? UnknownFieldHandler.onRead : u)(this.typeName, message, fieldNo, wireType, d);
            }
        }
        return message;
    }
    internalBinaryWrite(message: TargetAction, writer: IBinaryWriter, options: BinaryWriteOptions): IBinaryWriter {
        /* string new_target = 1; */
        if (message.newTarget !== "")
            writer.tag(1, WireType.LengthDelimited).string(message.newTarget);
        /* bool guard = 2; */
        if (message.guard !== false)
            writer.tag(2, WireType.Varint).bool(message.guard);
        /* bool follow = 3; */
        if (message.follow !== false)
            writer.tag(3, WireType.Varint).bool(message.follow);
        let u = options.writeUnknownFields;
        if (u !== false)
            (u == true ? UnknownFieldHandler.onWrite : u)(this.typeName, message, writer);
        return writer;
    }
}
/**
 * @generated MessageType for protobuf message TargetAction
 */
export const TargetAction = new TargetAction$Type();
// @generated message type with reflection information, may provide speed optimized methods
class ChatMessageNotify$Type extends MessageType<ChatMessageNotify> {
    constructor() {
        super("ChatMessageNotify", [
            { no: 1, name: "message", kind: "scalar", T: 9 /*ScalarType.STRING*/ }
        ]);
    }
    create(value?: PartialMessage<ChatMessageNotify>): ChatMessageNotify {
        const message = { message: "" };
        globalThis.Object.defineProperty(message, MESSAGE_TYPE, { enumerable: false, value: this });
        if (value !== undefined)
            reflectionMergePartial<ChatMessageNotify>(this, message, value);
        return message;
    }
    internalBinaryRead(reader: IBinaryReader, length: number, options: BinaryReadOptions, target?: ChatMessageNotify): ChatMessageNotify {
        let message = target ?? this.create(), end = reader.pos + length;
        while (reader.pos < end) {
            let [fieldNo, wireType] = reader.tag();
            switch (fieldNo) {
                case /* string message */ 1:
                    message.message = reader.string();
                    break;
                default:
                    let u = options.readUnknownField;
                    if (u === "throw")
                        throw new globalThis.Error(`Unknown field ${fieldNo} (wire type ${wireType}) for ${this.typeName}`);
                    let d = reader.skip(wireType);
                    if (u !== false)
                        (u === true ? UnknownFieldHandler.onRead : u)(this.typeName, message, fieldNo, wireType, d);
            }
        }
        return message;
    }
    internalBinaryWrite(message: ChatMessageNotify, writer: IBinaryWriter, options: BinaryWriteOptions): IBinaryWriter {
        /* string message = 1; */
        if (message.message !== "")
            writer.tag(1, WireType.LengthDelimited).string(message.message);
        let u = options.writeUnknownFields;
        if (u !== false)
            (u == true ? UnknownFieldHandler.onWrite : u)(this.typeName, message, writer);
        return writer;
    }
}
/**
 * @generated MessageType for protobuf message ChatMessageNotify
 */
export const ChatMessageNotify = new ChatMessageNotify$Type();
