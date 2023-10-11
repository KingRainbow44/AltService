import { CSSProperties } from "preact/compat";

interface IProps {
    itemId: string;
    style?: CSSProperties;
    className?: string;
}

export default function MinecraftItem(props: IProps) {
    return (
        <img
            alt={props.itemId}
            style={props.style}
            className={props.className}
            src={`/resources/item/${props.itemId}.png`}
        />
    );
}
