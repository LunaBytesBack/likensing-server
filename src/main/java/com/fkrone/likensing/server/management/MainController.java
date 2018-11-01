package com.fkrone.likensing.server.management;

import com.fkrone.likensing.server.licensing.License;
import sirius.biz.web.BizController;
import sirius.kernel.di.std.Register;
import sirius.web.controller.Controller;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;
import sirius.web.security.LoginRequired;

/**
 * Controller for having a main route
 */
@Register(classes = Controller.class)
public class MainController extends BizController {

    /**
     * Shows the start page.
     *
     * @param ctx the current request
     */
    @Routed("/")
    @LoginRequired
    public void mainRoute(WebContext ctx) {
        ctx.respondWith().template("templates/management/start.html.pasta", (int) oma.select(License.class).count());
    }
}
