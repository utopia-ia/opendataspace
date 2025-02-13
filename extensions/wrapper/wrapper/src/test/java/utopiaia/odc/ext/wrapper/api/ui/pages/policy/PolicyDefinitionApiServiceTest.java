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

package utopiaia.odc.ext.wrapper.api.ui.pages.policy;


import utopiaia.odc.client.EdcClient;
import utopiaia.odc.client.gen.model.OperatorDto;
import utopiaia.odc.client.gen.model.PolicyDefinitionCreateDto;
import utopiaia.odc.client.gen.model.PolicyDefinitionDto;
import utopiaia.odc.client.gen.model.UiPolicyConstraint;
import utopiaia.odc.client.gen.model.UiPolicyExpression;
import utopiaia.odc.client.gen.model.UiPolicyExpressionType;
import utopiaia.odc.client.gen.model.UiPolicyLiteral;
import utopiaia.odc.client.gen.model.UiPolicyLiteralType;
import utopiaia.odc.ext.db.jooq.Tables;
import utopiaia.odc.extension.db.directaccess.DslContextFactory;
import utopiaia.odc.extension.e2e.connector.config.ConnectorConfig;
import utopiaia.odc.extension.e2e.db.EdcRuntimeExtensionWithTestDatabase;
import lombok.val;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.spi.entity.Entity;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

import static utopiaia.odc.extension.e2e.connector.config.ConnectorConfigFactory.forTestDatabase;
import static org.assertj.core.api.Assertions.assertThat;

@ApiTest
class PolicyDefinitionApiServiceTest {
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

    UiPolicyExpression expression = UiPolicyExpression.builder()
        .type(UiPolicyExpressionType.CONSTRAINT)
        .constraint(UiPolicyConstraint.builder()
            .left("a")
            .operator(OperatorDto.EQ)
            .right(UiPolicyLiteral.builder()
                .type(UiPolicyLiteralType.STRING)
                .value("b")
                .build())
            .build())
        .build();

    @Test
    void getPolicyList() {
        // arrange
        createPolicyDefinition("my-policy-def-1");

        // act
        var response = client.uiApi().getPolicyDefinitionPage();

        // assert
        var policyDefinitions = response.getPolicies();
        assertThat(policyDefinitions).hasSize(2);
        var policyDefinition = policyDefinitions.stream()
            .filter(it -> it.getPolicyDefinitionId().equals("my-policy-def-1"))
            .findFirst().get();
        assertThat(policyDefinition.getPolicyDefinitionId()).isEqualTo("my-policy-def-1");
        assertThat(policyDefinition.getPolicy().getExpression()).usingRecursiveComparison().isEqualTo(expression);
    }

    @Test
    void sortPoliciesFromNewestToOldest(DslContextFactory dslContextFactory) {
        // arrange
        createPolicyDefinition("my-policy-def-0");
        createPolicyDefinition("my-policy-def-1");
        createPolicyDefinition("my-policy-def-2");

        dslContextFactory.transaction(dsl ->
            Map.of(
                "my-policy-def-0", 1628956800000L,
                "my-policy-def-1", 1628956801000L,
                "my-policy-def-2", 1628956802000L
            ).forEach((id, time) -> setPolicyDefCreatedAt(dsl, id, time)));

        // act
        var result = client.uiApi().getPolicyDefinitionPage();

        // assert
        assertThat(result.getPolicies())
            .extracting(PolicyDefinitionDto::getPolicyDefinitionId)
            .containsExactly(
                "always-true",
                "my-policy-def-2",
                "my-policy-def-1",
                "my-policy-def-0");
    }

    private static void setPolicyDefCreatedAt(DSLContext dsl, String id, Long time) {
        val p = Tables.EDC_POLICYDEFINITIONS;
        dsl.update(p)
            .set(p.CREATED_AT, time)
            .where(p.POLICY_ID.eq(id))
            .execute();
    }

    @Test
    void test_delete(PolicyDefinitionService policyDefinitionService) {
        // arrange
        createPolicyDefinition("my-policy-def-1");
        assertThat(policyDefinitionService.query(QuerySpec.max()).getContent().toList())
            .extracting(Entity::getId).contains("always-true", "my-policy-def-1");

        // act
        var response = client.uiApi().deletePolicyDefinition("my-policy-def-1");

        // assert
        assertThat(response.getId()).isEqualTo("my-policy-def-1");
        assertThat(policyDefinitionService.query(QuerySpec.max()).getContent())
            .extracting(Entity::getId).containsExactly("always-true");
    }

    private void createPolicyDefinition(String policyDefinitionId) {
        var policyDefinition = new PolicyDefinitionCreateDto(policyDefinitionId, expression);
        client.uiApi().createPolicyDefinitionV2(policyDefinition);
    }

}
