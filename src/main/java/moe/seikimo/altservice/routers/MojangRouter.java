package moe.seikimo.altservice.routers;

import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import moe.seikimo.altservice.utils.HttpUtils;

public interface MojangRouter {
    /**
     * Configures the Javalin router.
     *
     * @param javalin The Javalin instance.
     */
    static void configure(Javalin javalin) {
        javalin.get("/skin/{username}", MojangRouter::getSkinUrl);
    }

    /**
     * /skin/:username
     *
     * @param ctx The Javalin context.
     */
    static void getSkinUrl(Context ctx) {
        try {
            // Get the UUID of the username.
            var username = ctx.pathParam("username");
            var response = HttpUtils.get("https://api.mojang.com/users/profiles/minecraft/" + username);
            var body = HttpUtils.parse(response, JsonObject.class);

            // Get the skin URL.
            var uuid = body.get("id").getAsString();
            ctx.result("https://crafatar.com/avatars/" + uuid + "?overlay");
        } catch (Exception exception) {
            ctx.status(500).result(exception.getMessage());
        }
    }
}
