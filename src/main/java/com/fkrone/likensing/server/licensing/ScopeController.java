package com.fkrone.likensing.server.licensing;

import sirius.biz.web.BizController;
import sirius.biz.web.SQLPageHelper;
import sirius.db.jdbc.OMA;
import sirius.db.mixing.query.QueryField;
import sirius.kernel.di.std.Register;
import sirius.web.controller.AutocompleteHelper;
import sirius.web.controller.Controller;
import sirius.web.controller.Routed;
import sirius.web.http.WebContext;
import sirius.web.security.LoginRequired;

/**
 * Provides routes for managing {@link LicenseScope scopes}.
 */
@Register(classes = Controller.class)
public class ScopeController extends BizController {

    /**
     * Route for listing all available scopes.
     *
     * @param ctx the current request
     */
    @Routed("/scopes")
    @LoginRequired
    public void viewScopes(WebContext ctx) {

        SQLPageHelper<LicenseScope> scopePage =
                SQLPageHelper.withQuery(oma.select(LicenseScope.class).orderAsc(LicenseScope.NAME));
        scopePage.withContext(ctx);
        scopePage.withSearchFields(QueryField.contains(LicenseScope.NAME));

        ctx.respondWith().template("templates/licensing/scopes.html.pasta", scopePage.asPage());
    }

    /**
     * Route for editing a scope or creating a new scope.
     *
     * @param ctx     the current request
     * @param scopeId the id of the scope to edit
     */
    @Routed("/scope/:1")
    @LoginRequired
    public void scopeEdit(WebContext ctx, String scopeId) {
        LicenseScope licenseScope = find(LicenseScope.class, scopeId);

        SaveHelper saveHelper = prepareSave(ctx).withAfterCreateURI("/scope/${id}").withAfterSaveURI("/scopes");

        boolean requestHandled = saveHelper.saveEntity(licenseScope);
        if (!requestHandled) {
            validate(licenseScope);
            ctx.respondWith().template("templates/licensing/scope-details.html.pasta", licenseScope);
        }
    }

    /**
     * Route for deleting a scope.
     *
     * @param ctx     the current request
     * @param scopeId the id of the scope to delete
     */
    @Routed("/scope/:1/delete")
    @LoginRequired
    public void deleteScope(WebContext ctx, String scopeId) {
        oma.find(LicenseScope.class, scopeId).ifPresent(scope -> {
            oma.delete(scope);
            showDeletedMessage();
        });

        viewScopes(ctx);
    }

    /**
     * Route for autocompletion for scopes.
     *
     * @param ctx the current request
     */
    @LoginRequired
    @Routed("/scopes/autocomplete")
    public void scopesAutocomplete(final WebContext ctx) {
        AutocompleteHelper.handle(ctx,
                                  (query, result) -> oma.select(LicenseScope.class)
                                                        .where(OMA.FILTERS.like(LicenseScope.NAME)
                                                                          .contains(query)
                                                                          .ignoreEmpty()
                                                                          .build())
                                                        .limit(10)
                                                        .iterateAll(licenseScope -> result.accept(new AutocompleteHelper.Completion(
                                                                licenseScope.getIdAsString(),
                                                                licenseScope.getName(),
                                                                licenseScope.getName()))));
    }
}
