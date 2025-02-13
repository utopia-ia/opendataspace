/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package utopiaia.odc.ext.wrapper.api.usecase;

import utopiaia.odc.client.EdcClient;
import utopiaia.odc.extension.e2e.connector.config.ConnectorConfig;
import utopiaia.odc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static utopiaia.odc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
class KpiApiTest {
    private static ConnectorConfig config;
    private static EdcClient client;

    @RegisterExtension
    static EdcRuntimeExtensionWithTestDatabase providerExtension = new EdcRuntimeExtensionWithTestDatabase(
        ":launchers:connectors:sovity-dev",
        "provider",
        testDatabase -> {
            config = forTestDatabase("my-edc-participant-id", testDatabase);
            client = EdcClient.builder()
                .managementApiUrl(config.getManagementApiUrl())
                .managementApiKey(config.getManagementApiKey())
                .build();
            return config.getProperties();
        }
    );

    @Test
    void getKpis() {
        // act
        var actual = client.useCaseApi().getKpis();

        // assert
        assertThat(actual.getAssetsCount()).isZero();
        assertThat(actual.getContractAgreementsCount()).isZero();
        assertThat(actual.getContractDefinitionsCount()).isZero();
        assertThat(actual.getPoliciesCount()).isEqualTo(1);
        assertThat(actual.getTransferProcessDto().getIncomingTransferProcessCounts()).isEmpty();
        assertThat(actual.getTransferProcessDto().getOutgoingTransferProcessCounts()).isEmpty();
        assertThat(actual.getAssetsCount()).isZero();
    }
}
