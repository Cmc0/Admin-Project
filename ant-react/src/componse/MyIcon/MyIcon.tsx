import * as Icon from '@ant-design/icons';
import React from "react";
import {IconBaseProps} from "@ant-design/icons/lib/components/Icon";

interface IMyIcon extends IconBaseProps {
    icon: string
}

export const IconList = Object.keys(Icon).filter(item => item !== 'default')

const MyIcon = Icon as Record<string, any>

export default function (props: IMyIcon) {

    if (IconList.includes(props.icon)) {

        const Element = MyIcon[props.icon]
        return <Element {...props} />

    } else {

        return null
    }
}
