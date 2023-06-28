ScriptLib = {
    info = (function(context, message) end),
    warn = (function(context, message) end),
    error = (function(context, message) end),
    logTable = (function(context, table) end),
    setGlobal = (function(context, key, value)  end),
    getGlobal = (function(context, key) end),
    getPosition = (function(context, player) end),
    sendMessage = (function(context, player, message) end),
    move = (function(context, player, x, y, z) end),
    rotate = (function(context, player, pitch, yaw) end),
    breakBlock = (function(context, player, x, y, z) end),
    placeBlock = (function(context, player, block, x, y, z) end),
    addBehavior = (function(context, player, behavior) end),
    removeBehavior = (function(context, player, behavior) end),
    targetedAt = (function(context, message, player) end),
    parseCommand = (function(context, message, player) end),
    distance = (function(context, pos1, pos2) end),
    interactBlock = (function(context, player, block) end),
    closeInventory = (function(context, player) end),
    getInventory = (function(context, player) end),
    takeItem = (function(context, player, source, target) end),
    placeItem = (function(context, player, source, target) end)
}

Player = "" -- This will resolve to the executing player's name.

Options = {
    follow = false,
    attack = false,
    look = false,
    guard = false,
    behave = false,

    guardPlayers = true,
    guardMobs = true
}

EventType = {
    BREAK_BLOCK = 0,
    PLACE_BLOCK = 1,
    ENTITY_MOVE = 2,
    PLAYER_MOVE = 3,
    TAKE_DAMAGE = 4,
    INV_CHANGE = 5,
    MESSAGE_SENT = 6
}
