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

package utopiaia.odc.ext.wrapper.api.ui.pages.contract_agreement;

import utopiaia.odc.client.EdcClient;
import utopiaia.odc.client.gen.model.InitiateCustomTransferRequest;
import utopiaia.odc.client.gen.model.InitiateTransferRequest;
import utopiaia.odc.extension.e2e.connector.config.ConnectorConfig;
import utopiaia.odc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import utopiaia.odc.utils.JsonUtils;
import utopiaia.odc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.protocol.dsp.spi.types.HttpMessageProtocol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;
import java.util.UUID;

import static utopiaia.odc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
class ContractAgreementTransferApiServiceTest {

    private static final String DATA_SINK = "http://my-data-sink/api/stuff";
    private static final String COUNTER_PARTY_ADDRESS =
            "http://some-other-connector/api/v1/ids/data";

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
    void startTransferProcessForAgreementId(
            ContractNegotiationStore store,
            TransferProcessStore transferProcessStore
    ) {
        // arrange
        var contractId = UUID.randomUUID().toString();
        createContractNegotiation(store, COUNTER_PARTY_ADDRESS, contractId);

        var request = new InitiateTransferRequest(
                contractId,
                Map.of(
                        "type", "HttpData",
                        "baseUrl", DATA_SINK
                ),
                Map.of("privateProperty", "privateValue")
        );

        // act
        var result = client.uiApi().initiateTransfer(request);

        // then
        var transferProcess = transferProcessStore.findById(result.getId());
        assertThat(transferProcess).isNotNull();
        assertThat(transferProcess.getPrivateProperties()).containsAllEntriesOf(Map.of(
                "privateProperty", "privateValue"
        ));

        var dataRequest = transferProcess.getDataRequest();
        assertThat(dataRequest.getContractId()).isEqualTo(contractId);
        assertThat(dataRequest.getConnectorAddress()).isEqualTo(COUNTER_PARTY_ADDRESS);
        assertThat(dataRequest.getDataDestination().getProperties()).containsAllEntriesOf(Map.of(
                "https://w3id.org/edc/v0.0.1/ns/type", "HttpData",
                "baseUrl", DATA_SINK
        ));
    }

    @Test
    void startCustomTransferProcessForAgreementId(
            ContractNegotiationStore store,
            TransferProcessStore transferProcessStore
    ) {
        // arrange
        var contractId = UUID.randomUUID().toString();
        createContractNegotiation(store, COUNTER_PARTY_ADDRESS, contractId);

        var customRequestJson = Json.createObjectBuilder()
                .add(Prop.Edc.DATA_DESTINATION, Json.createObjectBuilder()
                        .add(Prop.Edc.TYPE, "HttpData")
                        .add(Prop.Edc.BASE_URL, DATA_SINK))
                .add(Prop.Edc.PRIVATE_PROPERTIES, Json.createObjectBuilder()
                        .add(Prop.Edc.RECEIVER_HTTP_ENDPOINT, "http://my-pull-backend")
                        .add("this-will-disappear", "because-its-not-an-url")
                        .add("http://unknown/custom-prop", "value"))
                .add(Prop.Edc.CTX + "protocol", HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
                .build();
        var request = new InitiateCustomTransferRequest(
                contractId,
                JsonUtils.toJson(customRequestJson)
        );

        // act
        var result = client.uiApi().initiateCustomTransfer(request);

        // then
        var transferProcess = transferProcessStore.findById(result.getId());
        assertThat(transferProcess).isNotNull();
        assertThat(transferProcess.getPrivateProperties()).containsAllEntriesOf(Map.of(
                Prop.Edc.RECEIVER_HTTP_ENDPOINT, "http://my-pull-backend",
                "http://unknown/custom-prop", "value"
        ));

        var dataRequest = transferProcess.getDataRequest();
        assertThat(dataRequest.getContractId()).isEqualTo(contractId);
        assertThat(dataRequest.getConnectorAddress()).isEqualTo(COUNTER_PARTY_ADDRESS);
        assertThat(dataRequest.getDataDestination().getProperties()).containsAllEntriesOf(Map.of(
                Prop.Edc.TYPE, "HttpData",
                Prop.Edc.BASE_URL, DATA_SINK
        ));
    }

    private ContractNegotiation createContractNegotiation(
            ContractNegotiationStore store,
            String counterPartyAddress,
            String agreementId
    ) {
        var assetId = UUID.randomUUID().toString();
        var agreement = ContractAgreement.Builder.newInstance()
                .id(agreementId)
                .providerId(UUID.randomUUID().toString())
                .consumerId(UUID.randomUUID().toString())
                .assetId(assetId)
                .policy(getPolicy())
                .build();

        var negotiation = ContractNegotiation.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .counterPartyId(UUID.randomUUID().toString())
                .counterPartyAddress(counterPartyAddress)
                .protocol(HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
                .contractAgreement(agreement)
                .contractOffer(createContractOffer(assetId))
                .state(ContractNegotiationStates.FINALIZED.code())
                .build();

        store.save(negotiation);
        return negotiation;
    }

    private Policy getPolicy() {
        return Policy.Builder.newInstance().build();
    }

    private ContractOffer createContractOffer(String assetId) {
        return ContractOffer.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .assetId(assetId)
                .policy(getPolicy())
                .build();
    }
}
