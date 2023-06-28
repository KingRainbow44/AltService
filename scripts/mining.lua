events = {
    { event = EventType.BLOCK_CHANGE, condition = "cond_blockChange", action = "action_blockChange" },
    { event = EventType.INV_CHANGE, condition = "cond_invChange", action = "action_invChange" }
}

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

-- Event conditions & actions

function cond_blockChange(context, event)
    return true
end

-- Action will always fire.
function action_blockChange(context, event)
    local yLevel = 0
    if event.getPosition().y == 0 then
        yLevel = -1
    end

    local offset = ScriptLib.getGlobal(context, "mining_block")
    if offset == -1 then
        offset = -2
    else
        offset = -1
    end

    ScriptLib.breakBlock(context, Player, 0, yLevel, offset)
    if offset == -2 and yLevel == -1 then
        ScriptLib.move(context, Player, 0, 0, -1)
    end
end

function cond_invChange(context, event)
    return event.getInventory().isFull() ~= true
end

-- Action will fire if the inventory is full.
function action_invChange(context, event)
    local inventory = event.getInventory()
    local item = inventory.getItem("cobbled_deepslate")

    if item ~= nil then
        inventory.dropItem(item, 64)
    end
end
