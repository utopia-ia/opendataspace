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

package utopiaia.odc.ext.wrapper.api.ui.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Contract Definition List Entry for Contract Definition Page")
public class ContractDefinitionEntry {

    @Schema(description = "Contract Definition ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractDefinitionId;

    @Schema(description = "Access Policy ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accessPolicyId;

    @Schema(description = "Contract Policy ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractPolicyId;

    @Schema(description = "Criteria for the contract", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UiCriterion> assetSelector;
}
