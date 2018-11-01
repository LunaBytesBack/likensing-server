package com.fkrone.likensing.server.keygeneration

import sirius.kernel.BaseSpecification
import sirius.kernel.di.std.Part

import java.time.LocalDate

class LicenseKeyGeneratorSpec extends BaseSpecification {

    @Part
    private static LicenseKeyGenerator licenseKeyGenerator

    def "A valid key is generated for known data"() {
        given:
        def features = new ArrayList()
        features.add("DMC-12")
        def validUntil = LocalDate.of(2015, 10, 21)
        def scopeUid = "Doc Brown"
        when:
        def generatedLicense = licenseKeyGenerator.generateLicense(features, validUntil, scopeUid)
        then:
        generatedLicense == "PGxpY2Vuc2U+CjxsaWNlbnNlZFByb3BlcnRpZXM+CjxsaWNlbnNlZEZlYXR1cmVzPgo8ZmVhdHVyZT5ETUMtMTI8L2ZlYXR1cmU+CjwvbGljZW5zZWRGZWF0dXJlcz4KPHZhbGlkVW50aWw+MTQ0NTM3ODQwMDAwMDwvdmFsaWRVbnRpbD4KPHNjb3BlVWlkPkRvYyBCcm93bjwvc2NvcGVVaWQ+CjwvbGljZW5zZWRQcm9wZXJ0aWVzPgo8c2lnbktleT5YTjlEOE9yNE0ra3VkZTdGaTRwNmllQmduangxUm9mNjJ2QkxEcVV4eEdHK3ZtRnhpMWlacTg2dFZKL2xhV1BzSWZ0ZExGaDFVZG5XZDBYUDdiYU9QWS91U0FSakFyZTM5bG9pMEF1ODZnYjhiY3VoNTJKKzByS2R1K2plKzlwYnhodlgvUllvbzN1S2hPZVZlOHp0dW03QUNQVmZIWWpMdG1ZVVQ4NmZQNWFkSFNueEM0akxXV21kVlZhMmFqR2lzeVo2VEovV2RpZ01oTEFXV1RwSlVYblYwbXhIQk16YzdJODdZOEhFMGtOOWF5S0NxL3F6UUNsdlA5Szkvc2dLWkFPQ25xcGVPcWFkbkhRM1h4bktwcWJxUmhoSlhUWFpDYnZ1SGVpdUJNUmFwK2FqcUYxOGdvYzdGM05WVm00ZlovTUVMbVNMQlpLOTFvYkpWVlBGelE9PTwvc2lnbktleT4KPC9saWNlbnNlPgo="
    }

}
