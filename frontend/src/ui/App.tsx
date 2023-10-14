import { Component } from "preact";

import "@css/App.scss";
import Inventory from "@ui/widgets/Inventory.tsx";
import Statistics from "@ui/widgets/Statistics.tsx";
import Chat from "@ui/widgets/Chat.tsx";

interface IProps {

}

interface IState {

}

const testInventory = {
    items: Array(27).fill({}),
    hotbar: Array(9).fill({}),
    helmet: {},
    chestplate: {},
    leggings: {},
    boots: {},
    shield: {},
};

const testPosition = {
    x: 0, y: 0, z: 0,
};

const testStatistics = {
    health: 11,
    armor: 5,
    hunger: 9,
    xpLevel: 5,
    xpProgress: 101
};

class App extends Component<IProps, IState> {
    constructor(props: IProps) {
        super(props);

        this.state = {};
    }

    render() {
        return (
            <div className={"App"}>
                <h1 className={"App_Title"}>Alt Service</h1>

                <div className={"App_Panel"}>
                    <div className={"App_Row"}>
                        <div>
                            <Inventory inventory={testInventory} />
                            <p class={"App_Header App_Inventory"}>Inventory</p>
                        </div>
                    </div>

                    <div className={"App_Row"}>
                        <div>
                            <p className={"App_Header App_Statistics"}>Statistics</p>
                            <Statistics position={testPosition} statistics={testStatistics} />
                        </div>

                        <div>
                            <p className={"App_Header App_Chat"}>Chat</p>
                            <Chat />
                        </div>
                    </div>
                </div>

                <div className={"App_Sessions"}>

                </div>
            </div>
        );
    }
}

export default App;
