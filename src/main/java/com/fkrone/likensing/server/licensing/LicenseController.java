package com.fkrone.likensing.server.licensing;

import sirius.biz.protocol.TraceData;
import sirius.biz.web.BizController;
import sirius.biz.web.SQLPageHelper;
import sirius.db.mixing.query.QueryField;
import sirius.kernel.di.std.Register;
import sirius.web.controller.Controller;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;
import sirius.web.security.LoginRequired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides routes for managing {@link License licenses}.
 */
@Register(classes = Controller.class)
public class LicenseController extends BizController {

    /**
     * Route for listing all available licenses.
     *
     * @param ctx the current request
     */
    @Routed("/licenses")
    @LoginRequired
    public void viewLicenses(WebContext ctx) {
        SQLPageHelper<License> licensePage =
                SQLPageHelper.withQuery(oma.select(License.class).orderDesc(License.TRACE.inner(TraceData.CREATED_AT)));
        licensePage.withContext(ctx);
        licensePage.withSearchFields(QueryField.contains(License.SCOPE.join(LicenseScope.NAME)));

        ctx.respondWith().template("templates/licensing/licenses.html.pasta", licensePage.asPage());
    }

    /**
     * Route for creating a new license.
     *
     * @param ctx the current request
     */
    @Routed(value = "/license/new", priority = 90)
    @LoginRequired
    public void newLicense(WebContext ctx) {
        License license = new License();

        List<ScopeFeature> scopeFeatures = oma.select(ScopeFeature.class).orderAsc(ScopeFeature.NAME).queryList();
        String licensedFeatures = scopeFeatures.stream()
                                               .map(ScopeFeature::getCode)
                                               .filter(ctx::hasParameter)
                                               .collect(Collectors.joining(","));

        license.setLicensedFeatures(licensedFeatures);

        BizController.SaveHelper saveHelper = prepareSave(ctx).withAfterCreateURI("/license/${id}");
        boolean requestHandled = saveHelper.saveEntity(license);
        if (!requestHandled) {
            validate(license);
            ctx.respondWith().template("templates/licensing/create-license.html.pasta", license, scopeFeatures);
        }
    }

    /**
     * Route for viewing a license.
     *
     * @param ctx       the current request
     * @param licenseId the id of the license to view
     */
    @Routed("/license/:1")
    @LoginRequired
    public void viewLicense(WebContext ctx, String licenseId) {
        License license = oma.findOrFail(License.class, licenseId);

        ctx.respondWith()
           .template("templates/licensing/license-details.html.pasta",
                     license,
                     oma.select(ScopeFeature.class).orderAsc(ScopeFeature.NAME).queryList());
    }
}
