import { Component } from "preact";

import "@css/widgets/Chat.scss";

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
            messages: [
                "<KingRainbow44> This is a real message!",
                "<KingRainbow44> This is a real message!",
                "<KingRainbow44> This is a real message!",
                "<KingRainbow44> This is a real message!",
                "<KingRainbow44> This is a real message!",
                "<KingRainbow44> This is a really long message that might overflow the existing table! Oh wait it didnt't so i have to expand it more.",
                "<KingRainbow44> This is a real message!",
                "<KingRainbow44> This is a real message!",
                "<KingRainbow44> This is a real message!",
                "<KingRainbow44> This is a real message!",
            ]
        };
    }

    componentDidMount() {
        this.messages = document.getElementById("messages") as HTMLDivElement;
        if (this.messages) {
            // Scroll to the bottom of the messages.
            this.messages.scrollTop = this.messages.scrollHeight;
        }
    }

    componentWillUnmount() {
        this.messages = null;
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
                  onKeyPress={(event) => {
                      if (event.key == "Enter") {
                          this.setState({
                              messages: [...this.state.messages, "<KingRainbow44> " + event.currentTarget.value]
                          });

                          event.currentTarget.value = "";
                      }
                  }}
                />
            </div>
        );
    }
}

export default Chat;
