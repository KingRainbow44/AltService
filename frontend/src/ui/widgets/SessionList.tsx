import { Component } from "preact";
import { useEffect, useState } from "preact/hooks";

import { Player } from "@backend/Structures.ts";

import "@css/widgets/SessionList.scss";
import { activeSession, setActiveSession } from "@backend/sessions.ts";

function Session(props: {
    active: boolean, session: Player,
    setSession: () => void
}) {
    const [playerIcon, setPlayerIcon] = useState("");

    useEffect(() => {
        (async() => {
            const response = await fetch(`http://localhost:1111/skin/${props.session.name}`);
            if (response.status == 200) {
                setPlayerIcon(await response.text());
            }
        })();
    }, []);

    return (
        <div className={"SessionList_Session"}
             onClick={props.setSession}
             style={{
                 border: `3px solid ${props.active ?
                     "var(--distinct-color)" :
                     "var(--accent-color)"}`
             }}
        >
            <img
                alt={props.session.name}
                className={"SessionList_Session_Icon"}
                src={playerIcon}
                onError={() => {
                    setPlayerIcon("/public/fallback.png");
                }}
            />
        </div>
    )
}

interface IProps {
    sessions: Player[];
}

class SessionList extends Component<IProps, never> {
    constructor(props: IProps) {
        super(props);
    }

    render() {
        return (
            <div className={"SessionList"}>
                {
                    this.props.sessions.map((session, index) =>
                        <Session key={index} session={session}
                                 active={activeSession == session}
                                 setSession={() => {
                                     setActiveSession(
                                         activeSession == session
                                             ? null : session);
                                     this.forceUpdate();
                                 }}
                        />
                    )
                }
            </div>
        );
    }
}

export default SessionList;
