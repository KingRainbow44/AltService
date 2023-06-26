function init(context)
    ScriptLib.setGlobal(context, "square_prog", 0)
end

function tick(context)
    local current = ScriptLib.getGlobal(context, "square_prog")

    if current == -1 then
        ScriptLib.setGlobal(context, "square_prog", 0)
        current = 1
    end

    if current == 1 then
        ScriptLib.placeBlock(context, Player, "netherrack", -2, -1, 1)
    elseif current == 2 then
        ScriptLib.placeBlock(context, Player, "netherrack", -2, -1, 0)
    elseif current == 3 then
        ScriptLib.placeBlock(context, Player, "netherrack", -2, -1, -1)
    end

    if current == 4 then
        ScriptLib.placeBlock(context, Player, "netherrack", 2, -1, 1)
    elseif current == 5 then
        ScriptLib.placeBlock(context, Player, "netherrack", 2, -1, 0)
    elseif current == 6 then
        ScriptLib.placeBlock(context, Player, "netherrack", 2, -1, -1)
    end

    if current == 7 then
        ScriptLib.placeBlock(context, Player, "netherrack", 1, -1, 2)
    elseif current == 8 then
        ScriptLib.placeBlock(context, Player, "netherrack", 0, -1, 2)
    elseif current == 9 then
        ScriptLib.placeBlock(context, Player, "netherrack", -1, -1, 2)
    end

    if current == 10 then
        ScriptLib.placeBlock(context, Player, "netherrack", 1, -1, -2)
    elseif current == 11 then
        ScriptLib.placeBlock(context, Player, "netherrack", 0, -1, -2)
    elseif current == 12 then
        ScriptLib.placeBlock(context, Player, "netherrack", -1, -1, -2)
        ScriptLib.removeBehavior(context, Player, "square.lua")
    end

    ScriptLib.setGlobal(context, "square_prog", current + 1)
end
