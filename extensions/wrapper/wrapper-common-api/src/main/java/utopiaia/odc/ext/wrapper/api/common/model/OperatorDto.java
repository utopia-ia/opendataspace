/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer ISST - initial implementation
 *       sovity GmbH - documentation changes
 *
 */

package utopiaia.odc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Schema(description = "Type-Safe ODRL Policy Operator as supported by the sovity product landscape", enumAsRef = true)
public enum OperatorDto {
    EQ,
    NEQ,
    GT,
    GEQ,
    LT,
    LEQ,
    IN,
    HAS_PART,
    IS_A,
    IS_ALL_OF,
    IS_ANY_OF,
    IS_NONE_OF
}
