package com.fkrone.likensing.server.licensing;

import sirius.biz.web.BizController;
import sirius.biz.web.SQLPageHelper;
import sirius.db.mixing.query.QueryField;
import sirius.kernel.di.std.Register;
import sirius.web.controller.Controller;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;
import sirius.web.security.LoginRequired;

/**
 * Provides routes for managing {@link ScopeFeature scope features}.
 */
@Register(classes = Controller.class)
public class ScopeFeatureController extends BizController {

    /**
     * Route for listing all available scope features.
     *
     * @param ctx the current request
     */
    @Routed("/features")
    @LoginRequired
    public void viewFeatures(WebContext ctx) {

        SQLPageHelper<ScopeFeature> featurePage =
                SQLPageHelper.withQuery(oma.select(ScopeFeature.class).orderAsc(ScopeFeature.NAME));
        featurePage.withContext(ctx);
        featurePage.withSearchFields(QueryField.contains(ScopeFeature.NAME));

        ctx.respondWith().template("templates/licensing/features.html.pasta", featurePage.asPage());
    }

    /**
     * Route for editing a scope feature or creating a new scope feature.
     *
     * @param ctx       the current request
     * @param featureId the id of the scope feature to edit
     */
    @Routed("/feature/:1")
    @LoginRequired
    public void featureEdit(WebContext ctx, String featureId) {
        ScopeFeature scopeFeature = find(ScopeFeature.class, featureId);

        SaveHelper saveHelper = prepareSave(ctx).withAfterCreateURI("/feature/${id}").withAfterSaveURI("/features");

        boolean requestHandled = saveHelper.saveEntity(scopeFeature);
        if (!requestHandled) {
            validate(scopeFeature);
            ctx.respondWith().template("templates/licensing/feature-details.html.pasta", scopeFeature);
        }
    }

    /**
     * Route for deleting a scope feature.
     *
     * @param ctx       the current request
     * @param featureId the id of the scope feature to delete
     */
    @Routed("/feature/:1/delete")
    @LoginRequired
    public void deleteFeature(WebContext ctx, String featureId) {
        oma.find(ScopeFeature.class, featureId).ifPresent(feature -> {
            oma.delete(feature);
            showDeletedMessage();
        });

        viewFeatures(ctx);
    }
}
