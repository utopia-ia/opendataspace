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
import utopiaia.odc.ext.wrapper.api.common.model.UiPolicy;
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
@Schema(description = "Catalog Data Offer's Contract Offer as required by the UI")
public class UiContractOffer {
    @Schema(description = "Contract Offer ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contractOfferId;

    @Schema(description = "Policy", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiPolicy policy;
}
