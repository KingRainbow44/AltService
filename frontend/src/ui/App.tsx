import { Component } from "preact";

import Inventory from "@ui/widgets/Inventory.tsx";
import Statistics from "@ui/widgets/Statistics.tsx";
import Chat from "@ui/widgets/Chat.tsx";
import Actions from "@ui/widgets/Actions.tsx";
import Scripting from "@ui/widgets/Scripting.tsx";
import SessionList from "@ui/widgets/SessionList.tsx";

import * as socket from "@backend/socket.ts";
import { FrontendIds } from "@backend/Frontend.ts";
import Sessions, { activeSession, sessions } from "@backend/sessions.ts";
import { EmptyAttributes, EmptyInventory, EmptyPosition } from "@app/constants.ts";

import "@css/App.scss";

interface IProps {

}

class App extends Component<IProps, never> {
    constructor(props: IProps) {
        super(props);
    }

    componentDidMount() {
        // Attempt to handshake with the server.
        socket.handshake();

        // Add event listeners.
        window.addEventListener(`socket:${FrontendIds._FrontendJoinScRsp}`, () => {
            console.debug("Received join response from server.");

            // Request all sessions from the server.
            socket.send(FrontendIds._GetAllSessionsCsReq, null);
        });

        Sessions.on("update", this.forceUpdate.bind(this));
        Sessions.on("active", this.forceUpdate.bind(this));
    }

    componentWillUnmount() {
        Sessions.off("update", this.forceUpdate.bind(this));
        Sessions.off("active", this.forceUpdate.bind(this));
    }

    render() {
        return (
            <div className={"App"}>
                <h1 className={"App_Title"}>Alt Service</h1>

                <div className={"App_Panel"}>
                    <div className={"App_Row items-start"}>
                        <div>
                            <Inventory inventory={activeSession?.inventory ?? EmptyInventory} />
                            <p class={"App_Header App_Inventory"}>Inventory</p>
                        </div>

                        <div>
                            <Actions />
                            <p className={"App_Header App_Actions"}>Actions</p>
                        </div>
                    </div>

                    <div className={"App_Row items-end"}>
                        <div>
                            <p className={"App_Header App_Statistics"}>Statistics</p>
                            <Statistics position={activeSession?.position ?? EmptyPosition}
                                        statistics={activeSession?.attributes ?? EmptyAttributes}
                            />
                        </div>

                        <div>
                            <p className={"App_Header App_Chat"}>Chat</p>
                            <Chat />
                        </div>

                        <div>
                            <p className={"App_Header App_Scripting"}>Scripting</p>
                            <Scripting />
                        </div>
                    </div>
                </div>

                <SessionList sessions={Object.values(sessions)} />
            </div>
        );
    }
}

export default App;
