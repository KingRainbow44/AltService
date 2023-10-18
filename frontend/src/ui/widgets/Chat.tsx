import { Component } from "preact";

import "@css/widgets/Chat.scss";
import SocketEvent, * as socket from "@backend/socket.ts";
import { ChatMessageNotify, FrontendIds } from "@backend/Frontend.ts";

interface IProps {

}

interface IState {
    messages: string[];
}

class Chat extends Component<IProps, IState> {
    private messages: HTMLDivElement | null = null;

    constructor(props: IProps) {
        super(props);

        this.state = {
            messages: []
        };
    }

    /**
     * Updates the chat message state.
     *
     * @param event The socket event.
     * @private
     */
    private updateChatMessages(event: Event): void {
        const socketEvent = event as SocketEvent;
        const data = socketEvent.data as ChatMessageNotify;

        // Add the message to the state.
        const newMessages = [...this.state.messages];
        newMessages.push(data.message);
        this.setState({ messages: newMessages });
    }

    componentDidMount() {
        this.messages = document.getElementById("messages") as HTMLDivElement;
        if (this.messages) {
            // Scroll to the bottom of the messages.
            this.messages.scrollTop = this.messages.scrollHeight;
        }

        // Add event listeners.
        window.addEventListener(
            `socket:${FrontendIds._ChatMessageNotify}`,
            this.updateChatMessages.bind(this));
    }

    componentWillUnmount() {
        this.messages = null;

        // Remove event listeners.
        window.removeEventListener(
            `socket:${FrontendIds._ChatMessageNotify}`,
            this.updateChatMessages.bind(this));
    }

    componentDidUpdate(_a: Readonly<IProps>, previousState: Readonly<IState>, _b: any) {
        // Check if the messages have changed.
        if (previousState.messages.length != this.state.messages.length) {
            // Scroll to the bottom of the messages.
            if (this.messages) {
                this.messages.scrollTop = this.messages.scrollHeight;
            }
        }
    }

    render() {
        return (
            <div className={"Chat"}>
                <div id={"messages"} class={"Chat_Messages"}>
                    {
                        this.state.messages.map((message, index) =>
                            <p key={index}>{message}</p>
                        )
                    }
                </div>

                <input
                    type={"text"}
                    autoCapitalize={"off"}
                    autocorrect={"off"}
                    autocomplete={"off"}
                    placeholder={"Send a message..."}
                    className={"relative top-[83%]"}
                    onKeyPress={(event) => {
                        if (event.key == "Enter") {
                            const message = event.currentTarget.value;
                            socket.send(FrontendIds._ChatMessageNotify,
                                ChatMessageNotify.toBinary({ message }));

                            event.currentTarget.value = "";
                        }
                    }}
                />
            </div>
        );
    }
}

export default Chat;
