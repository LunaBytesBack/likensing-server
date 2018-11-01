package com.fkrone.likensing.server.licensing;

import sirius.biz.jdbc.BizEntity;
import sirius.biz.web.Autoloaded;
import sirius.db.mixing.Mapping;
import sirius.db.mixing.annotations.Length;
import sirius.db.mixing.annotations.NullAllowed;
import sirius.db.mixing.annotations.Trim;
import sirius.db.mixing.annotations.Unique;
import sirius.kernel.commons.Strings;
import sirius.kernel.nls.NLS;

/**
 * Represents a licensable feature for scopes.
 */
public class ScopeFeature extends BizEntity {

    /**
     * Contains the technical code of a feature.
     * <p>
     * This code will be integrated into a license and will
     * be checked against on a license check.
     */
    public static final Mapping CODE = Mapping.named("code");
    @Length(255)
    @Autoloaded
    @Unique
    private String code;

    /**
     * Contains a human readable name of the feature.
     */
    public static final Mapping NAME = Mapping.named("name");
    @Length(255)
    @Trim
    @Autoloaded
    @NullAllowed
    private String name;

    @Override
    public String toString() {
        if (isNew()) {
            return NLS.get("ScopeFeature.new");
        }
        return getDisplayableName();
    }

    /**
     * Determines the name shown in frontend for the feature.
     *
     * @return the name for the feature shown in frontend
     */
    public String getDisplayableName() {
        if (Strings.isFilled(name)) {
            return name;
        }
        return code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
