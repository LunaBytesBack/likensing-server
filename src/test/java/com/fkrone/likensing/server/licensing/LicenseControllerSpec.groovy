package com.fkrone.likensing.server.licensing

import com.fkrone.likensing.server.ScenarioHelper
import sirius.biz.tenants.TenantsHelper
import sirius.db.jdbc.OMA
import sirius.kernel.BaseSpecification
import sirius.kernel.commons.Context
import sirius.kernel.di.std.Part
import sirius.kernel.nls.NLS
import sirius.web.controller.Page
import sirius.web.http.TestRequest
import sirius.web.security.UserContextHelper

import java.time.LocalDate

class LicenseControllerSpec extends BaseSpecification {

    @Part
    private static OMA oma

    private License testLicense
    private LicenseScope testLicenseScope
    private ScopeFeature testScopeFeature

    def setup() {
        testLicense = ScenarioHelper.getTestLicense()
        testLicenseScope = ScenarioHelper.getTestLicenseScope()
        testScopeFeature = ScenarioHelper.getTestScopeFeature()
    }

    def "GET /licenses shows a list of licenses"() {
        given:
        def request = TestRequest.GET("/licenses")
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoMessages(response)
        and:
        response.getTemplateName() == "/templates/licensing/licenses.html.pasta"
        and:
        response.getTemplateParameter(0).coerce(Page.class, null).getItems().size() >= 1
        and:
        response.getTemplateParameter(0).coerce(Page.class, null).getItems().contains(testLicense)
    }

    def "GET /license/new shows an editor"() {
        given:
        def request = TestRequest.GET("/license/new")
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoMessages(response)
        and:
        response.getTemplateName() == "/templates/licensing/create-license.html.pasta"
    }

    def "SAFEPOST /license/new creates a new license and shows the license afterwards"() {
        given:
        def validity = LocalDate.now().plusDays(7)
        and:
        def request = TestRequest.SAFEPOST("/license/new").withParameters(
                Context.create()
                       .set(License.SCOPE.getName(), testLicenseScope.getIdAsString())
                       .set(testScopeFeature.getCode(), true)
                       .set(License.VALID_UNTIL.getName(), NLS.toUserString(validity)))
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoMessages(response)
        and:
        def createdLicense = oma.select(License.class).eq(License.VALID_UNTIL, validity).orderDesc(License.ID).queryFirst()
        and:
        createdLicense != null
        and:
        response.getRedirectUrl() == "/license/" + createdLicense.getIdAsString()
    }
}
