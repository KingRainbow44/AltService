events = {
    { event = EventType.BLOCK_CHANGE, condition = "cond_isWood", action = "action_nextBlock" }
}

function init(context)
    ScriptLib.placeBlock(context, Player, "minecraft:sapling", 0, 0, 3)
end

function tick(context)

end

function cond_isWood(context, event)
    local oldBlock = event:getOldBlock()
    if oldBlock == nil or oldBlock:getIdentifier() ~= "minecraft:air" then
        return false
    end

    return true
end

function action_nextBlock(context, event)
    ScriptLib.placeBlock(context, Player, "minecraft:sapling", 0, 0, 3)
end
