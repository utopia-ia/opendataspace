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

package utopiaia.odc.ext.wrapper.api.ee.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Available and used resources of a connector.")
public class ConnectorLimits {
    @Schema(description = "Current amount of active consuming contract agreements.", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer numActiveConsumingContractAgreements;

    @Schema(description = "Maximum amount of active consuming contract agreements. A value of 'null' or a negative value means that there are no limit.")
    private Integer maxActiveConsumingContractAgreements;
}

