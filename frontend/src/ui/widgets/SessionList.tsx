import { Component } from "preact";
import { useEffect, useState } from "preact/hooks";

import { Player } from "@backend/Protocol.ts";

import "@css/widgets/SessionList.scss";

function Session(props: { session: Player }) {
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
        <div className={"SessionList_Session"}>
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

interface IState {

}

class SessionList extends Component<IProps, IState> {
    constructor(props: IProps) {
        super(props);

        this.state = {};
    }

    render() {
        return (
            <div className={"SessionList"}>
                {
                    this.props.sessions.map((session, index) =>
                        <Session key={index} session={session} />
                    )
                }
            </div>
        );
    }
}

export default SessionList;
