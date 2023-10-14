import { Component } from "preact";

import "@css/widgets/Scripting.scss";
import Button from "@components/Button.tsx";

interface IProps {

}

interface IState {

}

class Scripting extends Component<IProps, IState> {
    constructor(props: IProps) {
        super(props);

        this.state = {};
    }

    render() {
        return (
            <div className={"Scripting"}>
                <div className={"Scripting_Section"}>
                    <p className={"Scripting_Title"}>Available</p>

                    <div className={"Scripting_List"}>
                        <p>Script 1</p>
                        <p>Script 2</p>
                        <p>Script 3</p>
                        <p>Script 4</p>
                        <p>Script 5</p>
                    </div>
                </div>

                <div className={"Scripting_Actions"}>
                    <Button label={"Remove"} className={"Scripting_Button Scripting_Remove"}
                            onClick={() => alert("Remove selected script.")}
                    />

                    <Button label={"Add"} className={"Scripting_Button Scripting_Add"}
                            onClick={() => alert("Add selected script.")}
                    />

                    <Button label={"Edit"} className={"Scripting_Button Scripting_Edit"}
                            onClick={() => alert("Open script editor.")}
                    />
                </div>

                <div className={"Scripting_Section"}>
                    <p className={"Scripting_Title"}>Active</p>

                    <div className={"Scripting_List"}>
                        <p>Script 1</p>
                        <p>Script 2</p>
                        <p>Script 3</p>
                        <p>Script 4</p>
                        <p>Script 5</p>
                    </div>
                </div>
            </div>
        );
    }
}

export default Scripting;
