package com.fkrone.likensing.server.licensing;

import sirius.biz.jdbc.BizEntity;
import sirius.biz.web.Autoloaded;
import sirius.db.mixing.Mapping;
import sirius.db.mixing.annotations.Length;
import sirius.db.mixing.annotations.Trim;
import sirius.db.mixing.annotations.Unique;
import sirius.kernel.nls.NLS;

/**
 * Represents a scope the license can be generated for.
 */
public class LicenseScope extends BizEntity {

    /**
     * Contains the name of the scope.
     * <p>
     * This can be used to easily differentiate different scopes
     * by their commonly known names.
     */
    public static final Mapping NAME = Mapping.named("name");
    @Autoloaded
    @Length(255)
    @Trim
    private String name;

    /**
     * Contains the unique id of the scope.
     * <p>
     * This id should be unique to prevent license reusing.
     */
    public static final Mapping UID = Mapping.named("uid");
    @Autoloaded
    @Length(255)
    @Unique
    private String uid;

    @Override
    public String toString() {
        if (isNew()) {
            return NLS.get("LicenseScope.new");
        }
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
