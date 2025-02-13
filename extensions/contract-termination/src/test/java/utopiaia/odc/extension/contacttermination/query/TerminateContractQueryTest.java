/*
 *  Copyright (c) 2024 sovity GmbH
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

package utopiaia.odc.extension.contacttermination.query;

import utopiaia.odc.extension.contacttermination.ContractTerminationParam;
import utopiaia.odc.extension.db.directaccess.DslContextFactory;
import utopiaia.odc.extension.e2e.connector.config.ConnectorConfig;
import utopiaia.odc.extension.e2e.extension.Consumer;
import utopiaia.odc.extension.e2e.extension.E2eScenario;
import utopiaia.odc.extension.e2e.extension.E2eTestExtension;
import utopiaia.odc.extension.e2e.extension.Provider;
import utopiaia.odc.extension.utils.junit.DisabledOnGithub;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static utopiaia.odc.ext.db.jooq.enums.ContractTerminatedBy.COUNTERPARTY;
import static utopiaia.odc.extension.e2e.extension.Helpers.defaultE2eTestExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class TerminateContractQueryTest {

    @RegisterExtension
    private static E2eTestExtension e2eTestExtension = defaultE2eTestExtension();

    @DisabledOnGithub
    @Test
    void terminateConsumerAgreementOrThrow_shouldInsertRowInTerminationTable(
        E2eScenario scenario,
        @Consumer DslContextFactory dslContextFactory,
        @Provider ConnectorConfig providerConfig
    ) {
        val assetId = scenario.createAsset();
        scenario.createContractDefinition(assetId);
        val negotiation = scenario.negotiateAssetAndAwait(assetId);

        dslContextFactory.rollbackTransaction(trx -> {

                // arrange
                val query = new TerminateContractQuery();
                val agreementId = negotiation.getContractAgreementId();

                val details = new ContractTerminationParam(
                    agreementId,
                    "Some detail",
                    "Some reason"
                );
                val now = OffsetDateTime.now();

                // act
                val terminatedAt = query.terminateConsumerAgreementOrThrow(trx.dsl(), details, COUNTERPARTY);

                // assert
                assertThat(terminatedAt).isNotNull();

                val detailsQuery = new ContractAgreementTerminationDetailsQuery();
                val detailsAfterTermination = detailsQuery.fetchAgreementDetailsOrThrow(trx.dsl(), agreementId);

                assertThat(detailsAfterTermination.contractAgreementId()).isEqualTo(agreementId);
                assertThat(detailsAfterTermination.counterpartyId()).isEqualTo("provider");
                assertThat(detailsAfterTermination.counterpartyAddress())
                    .isEqualTo(providerConfig.getProtocolApiUrl());
                assertThat(detailsAfterTermination.type()).isEqualTo(ContractNegotiation.Type.CONSUMER);
                assertThat(detailsAfterTermination.providerAgentId()).isEqualTo("provider");
                assertThat(detailsAfterTermination.consumerAgentId()).isEqualTo("consumer");
                assertThat(detailsAfterTermination.reason()).isEqualTo("Some reason");
                assertThat(detailsAfterTermination.detail()).isEqualTo("Some detail");
                assertThat(detailsAfterTermination.terminatedAt()).isCloseTo(now, within(2, ChronoUnit.SECONDS));
                assertThat(detailsAfterTermination.terminatedBy()).isEqualTo(COUNTERPARTY);
            }
        );
    }
}
