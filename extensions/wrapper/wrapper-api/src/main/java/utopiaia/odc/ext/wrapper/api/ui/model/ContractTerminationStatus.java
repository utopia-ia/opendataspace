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

package utopiaia.odc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The contract termination status", enumAsRef = true)
public enum ContractTerminationStatus {
    ONGOING,
    TERMINATED
}
