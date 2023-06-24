function init()
    ScriptLib:setGlobal("sugarcane_block", 0)
end

function tick()
    local current = ScriptLib:getGlobal("sugarcane_block")
    if current == -1 then
        ScriptLib:setGlobal("sugarcane_block", 0)
        current = 0
    end

    if current < 13 then
        ScriptLib:move(Player, 1, 0, 0)
        ScriptLib:breakBlock(Player, 0, 0, 0)
    elseif current > 13 then
        ScriptLib:move(Player, -1, 0, 0)
        ScriptLib:breakBlock(Player, 0, 0, 0)
    end

    if current == 13 then
        ScriptLib:move(Player, 1, 0, -2)
    end

    if current == 26 then
        ScriptLib:move(Player, -1, 0, 2)
        current = -1
    end

    ScriptLib:setGlobal("sugarcane_block", current + 1)
end
