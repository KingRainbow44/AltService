import { CSSProperties } from "preact/compat";
import { useState } from "preact/hooks";

/**
 * Converts a string to upper camel case.
 *
 * @param string The string to convert.
 */
function upperFirst(string: string): string {
    const split = string.split("_");

    let result = "";
    for (const part of split) {
        result += part.charAt(0).toUpperCase() + part.slice(1) + "_";
    }
    result = result.slice(0, -1);

    return result;
}

interface IProps {
    itemId: string;
    style?: CSSProperties;
    className?: string;
}

export default function MinecraftItem(props: IProps) {
    const [path, setPath] = useState<string>(`/resources/item/${props.itemId}.png`);

    return (
        <img
            alt={props.itemId}
            style={props.style}
            className={props.className}
            src={path}
            onError={() => {
                if (path.includes("resources")) {
                    setPath(`https://minecraft.wiki/images/${upperFirst(props.itemId)}.png`);
                } else if (path.includes("minecraft.wiki")) {
                    setPath(`https://minecraft.wiki/images/Invicon_${upperFirst(props.itemId)}.png`);
                }
            }}
        />
    );
}
