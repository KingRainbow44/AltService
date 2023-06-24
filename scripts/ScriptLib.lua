ScriptLib = {
    info = (function(message) end),
    warn = (function(message) end),
    error = (function(message) end),
    logTable = (function(table) end),
    setGlobal = (function(key, value)  end),
    getGlobal = (function(key) end),
    getPosition = (function(player) end),
    sendMessage = (function(player, message) end),
    move = (function(player, x, y, z) end),
    rotate = (function(player, pitch, yaw) end),
    breakBlock = (function(player, x, y, z) end),
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
