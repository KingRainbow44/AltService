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
}

Options = {
    follow = false,
    attack = false,
    look = false,
    guard = false,
    behave = false,

    guardPlayers = true,
    guardMobs = true
}
