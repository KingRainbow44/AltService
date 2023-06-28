events = {
    { event = EventType.BLOCK_CHANGE, condition = "cond_isObsidian", action = "action_breakNext" }
}

function init(context)
    ScriptLib.breakBlock(context, Player, 0, -2, 0)
end

function tick(context)

end

function cond_isObsidian(context, event)
    local oldBlock = event:getOldBlock()
    if oldBlock == nil or oldBlock:getIdentifier() ~= "minecraft:obsidian" then
        return false
    end

    local newBlock = event:getBlock()
    if newBlock == nil or newBlock:getIdentifier() ~= "minecraft:air" then
        return false
    end

    local currentPos = ScriptLib.getPosition(context, Player)
    if currentPos.y < 3 then
        ScriptLib.removeBehavior(context, Player, "harvest/obsidian.lua")
        ScriptLib.sendMessage(context, Player, "Obsidian harvesting is done!")
        return false
    end

    return true
end

function action_breakNext(context, event)
    ScriptLib.move(context, Player, 0, -1, 0)
    ScriptLib.breakBlock(context, Player, 0, -2, 0)
end
