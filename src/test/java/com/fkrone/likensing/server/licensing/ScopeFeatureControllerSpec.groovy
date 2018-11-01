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

class ScopeFeatureControllerSpec extends BaseSpecification {

    @Part
    private static OMA oma

    private ScopeFeature testScopeFeature

    def setup() {
        testScopeFeature = ScenarioHelper.getTestScopeFeature()
    }

    def "GET /features shows a list of features"() {
        given:
        def request = TestRequest.GET("/features")
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoMessages(response)
        and:
        response.getTemplateName() == "/templates/licensing/features.html.pasta"
        and:
        response.getTemplateParameter(0).coerce(Page.class, null).getItems().size() >= 1
        and:
        response.getTemplateParameter(0).coerce(Page.class, null).getItems().contains(testScopeFeature)
    }

    def "GET /feature/new shows an editor"() {
        given:
        def request = TestRequest.GET("/feature/new")
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoMessages(response)
        and:
        response.getTemplateName() == "/templates/licensing/feature-details.html.pasta"
    }

    def "SAFEPOST /feature/new creates a new feature and shows the feature afterwards"() {
        given:
        def request = TestRequest.SAFEPOST("/feature/new").withParameters(
                Context.create()
                       .set(ScopeFeature.CODE.getName(), "new-scope-feature")
                       .set(ScopeFeature.NAME.getName(), "new scope feature"))
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoMessages(response)
        and:
        def createdFeature = oma.select(ScopeFeature.class).eq(ScopeFeature.CODE, "new-scope-feature").queryFirst()
        and:
        createdFeature != null
        and:
        response.getRedirectUrl() == "/feature/" + createdFeature.getIdAsString()
    }

    def "SAFEPOST /feature/:1/delete deletes a feature and shows list afterwards"() {
        given:
        def deleteFeature = new ScopeFeature()
        deleteFeature.setCode("delete-scope-feature")
        deleteFeature.setName("delete scope feature")
        oma.update(deleteFeature)
        and:
        def request = TestRequest.SAFEPOST("/feature/" + deleteFeature.getId() + "/delete")
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoErrorMessages(response)
        UserContextHelper.expectSuccessMessage(response)
        and:
        response.getTemplateName() == "/templates/licensing/features.html.pasta"
        and:
        !response.getTemplateParameter(0).coerce(Page.class, null).getItems().contains(deleteFeature)
        and:
        !oma.find(ScopeFeature.class, deleteFeature.getId()).isPresent()
    }
}
