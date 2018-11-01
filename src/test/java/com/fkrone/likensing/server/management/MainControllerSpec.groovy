package com.fkrone.likensing.server.management


import sirius.biz.tenants.TenantsHelper
import sirius.kernel.BaseSpecification
import sirius.web.http.TestRequest
import sirius.web.security.UserContextHelper

class MainControllerSpec extends BaseSpecification {

    def "GET / shows the main page"() {
        given:
        def request = TestRequest.GET("/")
        and:
        TenantsHelper.installTestTenant()
        TenantsHelper.installBackendUser(request)
        when:
        def response = request.execute()
        then:
        UserContextHelper.expectNoMessages(response)
        and:
        response.getTemplateName() == "/templates/management/start.html.pasta"
    }
}
