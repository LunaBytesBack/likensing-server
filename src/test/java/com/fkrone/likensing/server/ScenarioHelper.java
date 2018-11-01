package com.fkrone.likensing.server;

import com.fkrone.likensing.server.licensing.License;
import com.fkrone.likensing.server.licensing.LicenseScope;
import com.fkrone.likensing.server.licensing.ScopeFeature;
import sirius.db.jdbc.OMA;
import sirius.kernel.di.std.Part;

import java.time.LocalDate;

public class ScenarioHelper {

    @Part
    private static OMA oma;

    private static License testLicense;
    private static LicenseScope testLicenseScope;
    private static ScopeFeature testScopeFeature;

    public static License getTestLicense() {
        if (testLicense != null) {
            return testLicense;
        }
        testLicense = new License();
        testLicense.getScope().setValue(getTestLicenseScope());
        testLicense.setLicensedFeatures("test");
        testLicense.setValidUntil(LocalDate.now().plusDays(1));
        oma.update(testLicense);
        return testLicense;
    }

    public static LicenseScope getTestLicenseScope() {
        if (testLicenseScope != null) {
            return testLicenseScope;
        }

        testLicenseScope = new LicenseScope();
        testLicenseScope.setName("Testscope");
        testLicenseScope.setUid("1234");
        oma.update(testLicenseScope);
        return testLicenseScope;
    }

    public static ScopeFeature getTestScopeFeature() {
        if (testScopeFeature != null) {
            return testScopeFeature;
        }
        testScopeFeature = new ScopeFeature();
        testScopeFeature.setCode("test");
        testScopeFeature.setName("Testfeature");
        oma.update(testScopeFeature);
        return testScopeFeature;
    }
}
