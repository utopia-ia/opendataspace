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
 *       sovity GmbH - init
 *
 */

package utopiaia.odc.utils.config.model;

import java.util.Map;


@FunctionalInterface
public interface ConfigPropDefaultValueFn {
    String apply(Map<String, String> props);
}
