/*
 *  Copyright (c) 2022 sovity GmbH
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

package utopiaia.odc.client.examples;

import utopiaia.odc.client.EdcClient;
import utopiaia.odc.client.gen.api.UseCaseApi;
import utopiaia.odc.client.gen.model.KpiResult;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
class GreetingResourceTest {
    @InjectMock
    EdcClient edcClient;

    UseCaseApi useCaseApi;

    @BeforeEach
    void setup() {
        useCaseApi = mock(UseCaseApi.class);
        when(edcClient.useCaseApi()).thenReturn(useCaseApi);
    }

    @Test
    void testGetKpis() {
        var kpiResult = new KpiResult();
        kpiResult.setAssetsCount(3);
        when(useCaseApi.getKpis()).thenReturn(kpiResult);

        given()
                .when().get("/")
                .then()
                .statusCode(200)
                .body("assetsCount", is(3));
    }

}
