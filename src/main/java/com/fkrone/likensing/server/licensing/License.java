package com.fkrone.likensing.server.licensing;

import com.fkrone.likensing.server.keygeneration.LicenseKeyGenerator;
import sirius.biz.jdbc.BizEntity;
import sirius.biz.web.Autoloaded;
import sirius.db.jdbc.SQLEntityRef;
import sirius.db.mixing.Mapping;
import sirius.db.mixing.annotations.BeforeDelete;
import sirius.db.mixing.annotations.BeforeSave;
import sirius.db.mixing.annotations.Lob;
import sirius.db.mixing.annotations.NullAllowed;
import sirius.db.mixing.annotations.Transient;
import sirius.kernel.Sirius;
import sirius.kernel.commons.Strings;
import sirius.kernel.di.std.Part;
import sirius.kernel.health.Exceptions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a generated license for the set scope.
 */
public class License extends BizEntity {

    /**
     * The license generator.
     */
    @Transient
    @Part
    private static LicenseKeyGenerator licenseKeyGenerator;

    /**
     * Contains the scope the license is for.
     */
    public static final Mapping SCOPE = Mapping.named("scope");
    @Autoloaded
    private final SQLEntityRef<LicenseScope> scope = SQLEntityRef.on(LicenseScope.class, SQLEntityRef.OnDelete.CASCADE);

    /**
     * Contains the generated license key.
     */
    public static final Mapping LICENSE_KEY = Mapping.named("licenseKey");
    @Lob
    private String licenseKey;

    /**
     * Contains a comma separated string of the licensed features.
     */
    public static final Mapping LICENSED_FEATURES = Mapping.named("licensedFeatures");
    @NullAllowed
    @Lob
    private String licensedFeatures;

    /**
     * Contains the max validity of the license.
     */
    public static final Mapping VALID_UNTIL = Mapping.named("validUntil");
    @Autoloaded
    private LocalDate validUntil;

    @Transient
    private List<String> licensedFeaturesList;

    @BeforeSave(priority = 99)
    protected void checkFieldsFilled() {
        if (validUntil == null) {
            throw Exceptions.createHandled().withNLSKey("License.errValidUntilNotSet").handle();
        }
        if (scope.isEmpty()) {
            throw Exceptions.createHandled().withNLSKey("License.errScopeNotFilled").handle();
        }
    }

    @BeforeSave
    protected void checkDate() {
        Duration maxValidity = Duration.ofMillis(Sirius.getSettings().getMilliseconds("licensing.maxValidity"));
        if (validUntil.isAfter(LocalDateTime.now().plus(maxValidity).toLocalDate())) {
            throw Exceptions.createHandled().withNLSKey("License.errLicenseTooLongValid").handle();
        }
    }

    @BeforeSave(priority = 999)
    protected void generateLicense() {
        licenseKey =
                licenseKeyGenerator.generateLicense(getLicensedFeaturesList(), validUntil, scope.getValue().getUid());
    }

    @BeforeDelete
    protected void preventDelete() {
        throw Exceptions.handle().withNLSKey("License.errCannotDelete").handle();
    }

    /**
     * Easy getter for retrieving a list of licensed features
     * e.g. to display them.
     *
     * @return a list of the licensed features
     */
    public List<String> getLicensedFeaturesList() {
        if (Strings.isEmpty(licensedFeatures)) {
            return Collections.emptyList();
        }
        if (licensedFeaturesList == null) {
            licensedFeaturesList = Arrays.asList(licensedFeatures.split(","));
        }
        return licensedFeaturesList;
    }

    /**
     * Determines if the license is still valid.
     *
     * @return <tt>true</tt> if the license is still valid, <tt>false</tt> otherwise
     */
    public boolean isValid() {
        return LocalDate.now().isBefore(validUntil) || LocalDate.now().isEqual(validUntil);
    }

    public SQLEntityRef<LicenseScope> getScope() {
        return scope;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getLicensedFeatures() {
        return licensedFeatures;
    }

    public void setLicensedFeatures(String licensedFeatures) {
        this.licensedFeatures = licensedFeatures;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }
}
