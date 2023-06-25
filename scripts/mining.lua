function init(context)
    ScriptLib.sendMessage(context, Player, "I'm going to begin mining!")
    ScriptLib.setGlobal(context, "mining_block", 0)
end

function tick(context)
    local current = ScriptLib.getGlobal(context, "mining_block")
    if current == -1 then
        ScriptLib.setGlobal(context, "mining_block", 0)
        current = 1
    end

    if current == 1 then
        ScriptLib.breakBlock(context, Player, 0, 0, -1)
        ScriptLib.breakBlock(context, Player, 0, -1, -1)
    elseif current == 2 then
        ScriptLib.breakBlock(context, Player, 0, 0, -2)
        ScriptLib.breakBlock(context, Player, 0, -1, -2)
    elseif current == 3 then
        ScriptLib.move(context, Player, 0, 0, -1)
        current = 0
    end

    ScriptLib.setGlobal(context, "mining_block", current + 1)
end
