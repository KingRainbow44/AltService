import * as React from "preact/compat";

import "@css/components/Button.scss";

interface IProps {
    label?: string;

    id?: string;
    className?: string;
    style?: React.CSSProperties;

    onClick?: () => void;
    onContext?: (e: MouseEvent) => void;

    children?: React.ReactNode | React.ReactNode[];
}

export default function Button(props: IProps) {
    return (
        <div class={`Button ${props.className}`}
             id={props.id} style={props.style}
             onClick={props.onClick}
             onContextMenu={props.onContext}
        >
            {
                !props.label ? null :
                    <p className={"Button_Label"}>
                        {props.label}
                    </p>
            }

            {props.children}
        </div>
    );
}
