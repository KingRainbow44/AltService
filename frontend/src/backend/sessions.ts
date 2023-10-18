import { Player } from "@backend/Structures.ts";
import {
    Action,
    FrontendIds,
    GetAllSessionsScRsp,
    SessionActionCsNotify,
    UpdateSessionsScNotify,
} from "@backend/Frontend.ts";
import SocketEvent, * as socket from "@backend/socket.ts";
import { EventEmitter } from "events";

const emitter = new EventEmitter();
export let activeSession: Player | null = null;
export const sessions: { [key: string]: Player } = {};

window.addEventListener(`socket:${FrontendIds._GetAllSessionsScRsp}`, (event) => {
    const customEvent = event as SocketEvent;
    const data = customEvent.data as GetAllSessionsScRsp;

    updateSessions(data.sessions);
});

window.addEventListener(`socket:${FrontendIds._UpdateSessionsScNotify}`, (event) => {
    const customEvent = event as SocketEvent;
    const data = customEvent.data as UpdateSessionsScNotify;

    updateSessions(data.sessions);
});

/**
 * Sets the active session.
 *
 * @param session The session to set.
 */
export function setActiveSession(session: Player | null) {
    activeSession = session;
    emitter.emit("active", session);

    socket.send(
        FrontendIds._SessionActionCsNotify,
        SessionActionCsNotify.toBinary(
            SessionActionCsNotify.create({
                action: Action.Select,
                sessionId: session?.id ?? ""
            }))
    );
}

/**
 * Updates all available sessions.
 *
 * @param newSessions The new sessions.
 */
function updateSessions(newSessions: Player[]) {
    for (const session of newSessions) {
        sessions[session.id] = session;
    }

    emitter.emit("update", sessions);
    console.debug("Updated all sessions.", sessions);
}

export default emitter;
