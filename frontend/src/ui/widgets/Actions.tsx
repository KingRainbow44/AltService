import { Component } from "preact";

import Button from "@components/Button.tsx";

import "@css/widgets/Actions.scss";

interface IProps {

}

interface IState {
    disconnect: boolean;
    reconnect: boolean;
    following: boolean;
    guarding: boolean;
}

class Actions extends Component<IProps, IState> {
    private disconnectTimer: number | null = null;

    constructor(props: IProps) {
        super(props);

        this.state = {
            disconnect: false,
            reconnect: false,
            following: false,
            guarding: false,
        };
    }

    /**
     * Flips the current boolean state.
     *
     * @param state The state to flip.
     * @private
     */
    private flipState(state: string) {
        const currentState = (this.state as any)[state];
        this.setState({ [state]: !currentState });
    }

    /**
     * Tries to disconnect from the current server.
     * @private
     */
    private tryDisconnect() {
        if (!this.state.disconnect) {
            this.flipState("disconnect");
            this.disconnectTimer = setTimeout(() => {
                this.setState({ disconnect: false });
                this.disconnectTimer = null;
            }, 5e3);

            return;
        }

        if (this.disconnectTimer) {
            clearTimeout(this.disconnectTimer);
        }

        // TODO: Disconnect from server.
        this.flipState("disconnect");
    }

    render() {
        return (
            <div className={"Actions"}>
                <div class={"Actions_Column"}>
                    <Button className={"Actions_Button Actions_Disconnect"}
                            onClick={this.tryDisconnect.bind(this)}
                            label={this.state.disconnect ? "Really?" : "Disconnect"}
                    />

                    <Button className={"Actions_Button Actions_Reconnect"}
                            onClick={() => this.flipState("reconnect")}
                            label={this.state.reconnect ? "Disable" : "Reconnect"}
                            style={{ background: this.state.reconnect ? "#187d36" : undefined }}
                    />
                </div>

                <div class={"Actions_Column"}>
                    <Button className={"Actions_Button Actions_Move"}
                            onClick={() => alert("Open movement panel.")}
                            label={"Move"}
                    />

                    <Button className={"Actions_Button Actions_Move"}
                            onClick={() => alert("Open rotate panel.")}
                            label={"Rotate"}
                    />

                    <Button className={"Actions_Button Actions_Move"}
                            onClick={() => alert("Open viewer panel.")}
                            label={"Show View"}
                    />
                </div>

                <div class={"Actions_Column"}>
                    <Button className={"Actions_Button Actions_Target"}
                            onClick={() => this.flipState("following")}
                            label={this.state.following ? "Disable" : "Follow"}
                            style={{ background: this.state.following ? "#77a4b7" : undefined }}
                    />

                    <Button className={"Actions_Button Actions_Target"}
                            onClick={() => this.flipState("guarding")}
                            label={this.state.guarding ? "Disable" : "Guard"}
                            style={{ background: this.state.guarding ? "#77a4b7" : undefined }}
                    />
                </div>
            </div>
        );
    }
}

export default Actions;
