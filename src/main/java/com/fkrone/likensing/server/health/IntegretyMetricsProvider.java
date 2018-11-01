package com.fkrone.likensing.server.health;

import com.fkrone.likensing.server.licensing.License;
import sirius.db.jdbc.OMA;
import sirius.db.jdbc.SQLEntity;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;
import sirius.kernel.health.metrics.MetricProvider;
import sirius.kernel.health.metrics.MetricState;
import sirius.kernel.health.metrics.MetricsCollector;

/**
 * Metric to detect deleted licenses.
 * <p>
 * Licenses MUST not be deleted to have an integrate state
 * of the server to determine who generated when which license
 * for which scope.
 */
@Register
public class IntegretyMetricsProvider implements MetricProvider {

    @Part
    private static OMA oma;

    @Override
    public void gather(MetricsCollector collector) {
        long highestId = oma.select(License.class).orderDesc(License.ID).first().map(SQLEntity::getId).orElse(0L);
        long licenseCount = oma.select(License.class).count();
        double forcedDeletedEntries = (double) highestId - licenseCount;
        collector.metric("foredDeletedLicenses",
                         "Forced deleted licenses",
                         forcedDeletedEntries,
                         null,
                         forcedDeletedEntries > 0 ? MetricState.RED : MetricState.GREEN);
    }
}
