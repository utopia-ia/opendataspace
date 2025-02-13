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

package utopiaia.odc.ext.wrapper.api.ui.pages.catalog;

import utopiaia.odc.client.EdcClient;
import utopiaia.odc.client.gen.model.ContractDefinitionRequest;
import utopiaia.odc.client.gen.model.DataSourceType;
import utopiaia.odc.client.gen.model.PolicyDefinitionCreateDto;
import utopiaia.odc.client.gen.model.UiAssetCreateRequest;
import utopiaia.odc.client.gen.model.UiCriterion;
import utopiaia.odc.client.gen.model.UiCriterionLiteral;
import utopiaia.odc.client.gen.model.UiCriterionLiteralType;
import utopiaia.odc.client.gen.model.UiCriterionOperator;
import utopiaia.odc.client.gen.model.UiDataSource;
import utopiaia.odc.client.gen.model.UiDataSourceHttpData;
import utopiaia.odc.client.gen.model.UiPolicyExpression;
import utopiaia.odc.client.gen.model.UiPolicyExpressionType;
import utopiaia.odc.extension.e2e.connector.config.ConnectorConfig;
import utopiaia.odc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import utopiaia.odc.extension.utils.junit.DisabledOnGithub;
import utopiaia.odc.utils.jsonld.vocab.Prop;
import lombok.SneakyThrows;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static utopiaia.odc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static org.assertj.core.api.Assertions.assertThat;


@ApiTest
class CatalogApiTest {
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

    private final String dataOfferId = "my-data-offer-2023-11";

    /**
     * There used to be issues with the Prop.DISTRIBUTION field being occupied by core EDC.
     * This test verifies that the field can be used by us.
     */
    @DisabledOnGithub
    @Test
    @SneakyThrows
    void testDistributionKey() {
        // arrange
        createAsset();
        createPolicy();
        createContractDefinition();
        // act
        var catalogPageDataOffers = client.uiApi().getCatalogPageDataOffers(config.getProtocolApiUrl());

        // assert
        assertThat(catalogPageDataOffers.size()).isEqualTo(1);
        assertThat(catalogPageDataOffers.get(0).getAsset().getTitle()).isEqualTo("My Data Offer");
        assertThat(catalogPageDataOffers.get(0).getAsset().getMediaType()).isEqualTo("Media Type");
    }

    private void createAsset() {
        var dataSource = UiDataSource.builder()
            .type(DataSourceType.HTTP_DATA)
            .httpData(UiDataSourceHttpData.builder()
                .baseUrl("https://a")
                .build())
            .build();

        var asset = UiAssetCreateRequest.builder()
                .id(dataOfferId)
                .title("My Data Offer")
                .description("Example Data Offer.")
                .version("2023-11")
                .language("EN")
                .publisherHomepage("https://my-department.my-org.com/my-data-offer")
                .licenseUrl("https://my-department.my-org.com/my-data-offer#license")
                .mediaType("Media Type")
                .dataSource(dataSource)
                .build();

        client.uiApi().createAsset(asset);
    }

    private void createPolicy() {
        var policyDefinition = PolicyDefinitionCreateDto.builder()
                .policyDefinitionId(dataOfferId)
                .expression(UiPolicyExpression.builder().type(UiPolicyExpressionType.EMPTY).build())
                .build();

        client.uiApi().createPolicyDefinitionV2(policyDefinition);
    }

    private void createContractDefinition() {
        var contractDefinition = ContractDefinitionRequest.builder()
                .contractDefinitionId(dataOfferId)
                .accessPolicyId(dataOfferId)
                .contractPolicyId(dataOfferId)
                .assetSelector(List.of(UiCriterion.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(UiCriterionOperator.EQ)
                        .operandRight(UiCriterionLiteral.builder()
                                .type(UiCriterionLiteralType.VALUE)
                                .value(dataOfferId)
                                .build())
                        .build()))
                .build();

        client.uiApi().createContractDefinition(contractDefinition);
    }

}
