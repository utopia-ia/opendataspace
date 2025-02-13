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

package utopiaia.odc.ext.wrapper.api.usecase.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "EDC-status-defining KPIs")
public class KpiResult {
    @Schema(description = "Counts of assets", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer assetsCount;

    @Schema(description = "Counts of policies", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer policiesCount;

    @Schema(description = "Counts of contract definitions", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer contractDefinitionsCount;

    @Schema(description = "Counts of contract agreements", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer contractAgreementsCount;

    @Schema(description = "Counts of incoming and outgoing TransferProcesses and status",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private TransferProcessStatesDto transferProcessDto;
}
