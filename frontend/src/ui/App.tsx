import { Component } from "preact";

import "@css/App.scss";
import Inventory from "@ui/widgets/Inventory.tsx";

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
                        <div className={"App_Inventory"}>
                            <Inventory inventory={testInventory} />
                            <p>Inventory</p>
                        </div>
                    </div>

                    <div className={"App_Row"}>
                        <div className={"App_Statistics"}>

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
