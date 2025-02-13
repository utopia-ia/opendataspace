
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

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Contract Negotiation Information")
public class UiContractNegotiation {

    @Schema(description = "Contract Negotiation Id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractNegotiationId;

    @Schema(description = "Contract Negotiation Creation Time", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime createdAt;

    @Schema(description = "Contract Agreement Id")
    private String contractAgreementId;

    @Schema(description = "State of the Contract Negotiation state machine", requiredMode = Schema.RequiredMode.REQUIRED)
    private ContractNegotiationState state;
}
