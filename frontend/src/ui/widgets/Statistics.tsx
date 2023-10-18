import { Component } from "preact";

import { Attributes, Vector3 } from "@backend/Structures.ts";

import { toArray } from "@app/utils.tsx";

import "@css/widgets/Statistics.scss";

function Heart(props: { value: number }) {
    let pointStyle: string | null = null;
    if (props.value == 2) {
        pointStyle = "full";
    } else if (props.value == 1) {
        pointStyle = "half";
    }

    return (
        <div class={"Statistics_LayeredContainer"}>
            <img
                alt={"Heart Container"}
                src={"/resources/gui/sprites/hud/heart/container.png"}
            />

            {
                pointStyle && <img
                    alt={"Heart Point"}
                    src={`/resources/gui/sprites/hud/heart/${pointStyle}.png`}
                />
            }
        </div>
    )
}

function Armor(props: { value: number }) {
    let pointStyle = "empty";
    if (props.value == 2) {
        pointStyle = "full";
    } else if (props.value == 1) {
        pointStyle = "half";
    }

    return (
        <img
            alt={"Armor Point"}
            src={`/resources/gui/sprites/hud/armor_${pointStyle}.png`}
        />
    );
}

function Hunger(props: { value: number }) {
    let pointStyle: string | null = null;
    if (props.value == 2) {
        pointStyle = "full";
    } else if (props.value == 1) {
        pointStyle = "half";
    }

    return (
        <div class={"Statistics_LayeredContainer"}>
            <img
                alt={"Food Container"}
                src={"/resources/gui/sprites/hud/food_empty.png"}
            />

            {
                pointStyle && <img
                    alt={"Heart Point"}
                    src={`/resources/gui/sprites/hud/food_${pointStyle}.png`}
                />
            }
        </div>
    )
}

/**
 * Formats a vector into a string.
 * @param vector The vector to format.
 */
function format(vector: Vector3): string {
    const x = vector.x.toFixed(3);
    const y = vector.y.toFixed(3);
    const z = vector.z.toFixed(3);
    return `(${x}, ${y}, ${z})`;
}

interface IProps {
    position: Vector3;
    statistics: Attributes;
}

interface IState {

}

class Statistics extends Component<IProps, IState> {
    constructor(props: IProps) {
        super(props);

        this.state = {};
    }

    render() {
        const { position, statistics } = this.props;

        return (
            <div className={"Statistics"}>
                <p class={"Statistics_Position"}>
                    {format(position)}
                </p>

                <div class={"Statistics_Data"}>
                    <div class={"Statistics_Player"}>
                        <div class={"Statistics_Survival"}>
                            <div class={"Statistics_Container"}>
                                {toArray(statistics.armor).map((value, index) =>
                                    <Armor key={index} value={value} />
                                )}
                            </div>

                            <div class={"Statistics_Container"}>
                                {toArray(statistics.health).map((value, index) =>
                                    <Heart key={index} value={value} />
                                )}
                            </div>
                        </div>

                        <div class={"Statistics_Container"}>
                            {toArray(statistics.hunger).reverse().map((value, index) =>
                                <Hunger key={index} value={value} />
                            )}
                        </div>
                    </div>

                    <div class={"Statistics_Experience"}>
                        {
                            statistics.xpLevel > 0 &&
                            <p className={"Statistics_Levels"}>{statistics.xpLevel}</p>
                        }

                        <div class={"Statistics_ExperienceBar"}>
                            <img
                                style={{
                                    width: `${statistics.xpProgress}%`,
                                    height: 10.96,
                                    objectFit: "cover",
                                    objectPosition: "left"
                                }}
                                alt={"Experience Bar Container"}
                                src={"/resources/gui/sprites/hud/experience_bar_progress.png"}
                            />
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default Statistics;
