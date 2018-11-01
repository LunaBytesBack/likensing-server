package com.fkrone.likensing.server.licensing

import com.fkrone.likensing.server.ScenarioHelper
import sirius.biz.tenants.TenantsHelper
import sirius.db.jdbc.OMA
import sirius.kernel.BaseSpecification
import sirius.kernel.commons.Context
import sirius.kernel.di.std.Part
import sirius.web.controller.Page
import sirius.web.http.TestRequest
import sirius.web.security.UserContextHelper

class ScopeControllerSpec extends BaseSpecification {

    @Part
    private static OMA oma

    private LicenseScope testLicenseScope

    def setup() {
        testLicenseScope = ScenarioHelper.getTestLicenseScope()
    }

    def "GET /scopes shows a list of scopes"() {
        given:
        def request = TestRequest.GET("/scopes")
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoMessages(response)
        and:
        response.getTemplateName() == "/templates/licensing/scopes.html.pasta"
        and:
        response.getTemplateParameter(0).coerce(Page.class, null).getItems().size() >= 1
        and:
        response.getTemplateParameter(0).coerce(Page.class, null).getItems().contains(testLicenseScope)
    }

    def "GET /scope/new shows an editor"() {
        given:
        def request = TestRequest.GET("/scope/new")
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoMessages(response)
        and:
        response.getTemplateName() == "/templates/licensing/scope-details.html.pasta"
    }

    def "SAFEPOST /scope/new creates a new scope and shows the scope afterwards"() {
        given:
        def request = TestRequest.SAFEPOST("/scope/new").withParameters(
                Context.create()
                       .set(LicenseScope.NAME.getName(), "new scope")
                       .set(LicenseScope.UID.getName(), "scopeUid"))
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoMessages(response)
        and:
        def createdScope = oma.select(LicenseScope.class).eq(LicenseScope.UID, "scopeUid").queryFirst()
        and:
        createdScope != null
        and:
        response.getRedirectUrl() == "/scope/" + createdScope.getIdAsString()
    }

    def "SAFEPOST /scope/:1/delete deletes a scope and shows list afterwards"() {
        given:
        def deleteScope = new LicenseScope()
        deleteScope.setName("DeleteScope")
        deleteScope.setUid("delete")
        oma.update(deleteScope)
        and:
        def request = TestRequest.SAFEPOST("/scope/" + deleteScope.getId() + "/delete")
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoErrorMessages(response)
        UserContextHelper.expectSuccessMessage(response)
        and:
        response.getTemplateName() == "/templates/licensing/scopes.html.pasta"
        and:
        !response.getTemplateParameter(0).coerce(Page.class, null).getItems().contains(deleteScope)
        and:
        !oma.find(LicenseScope.class, deleteScope.getId()).isPresent()
    }
}
