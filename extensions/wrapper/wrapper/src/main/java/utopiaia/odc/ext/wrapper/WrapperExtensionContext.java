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

package utopiaia.odc.ext.wrapper;

import utopiaia.odc.ext.wrapper.api.ui.pages.dashboard.services.SelfDescriptionService;

import java.util.List;


/**
 * Manual Dependency Injection result
 *
 * @param managementApiResources Jax RS Resource implementations to register. Implementations of
 *                               APIs supported by our EDC API Client that don't have their own
 *                               extension should land here.
 * @param dspApiResources     Jax RS Resource implementations to register. Rare DSP API
 * @param selfDescriptionService Required here for validation on start-up
 */
public record WrapperExtensionContext(
        List<Object> managementApiResources,
        List<Object> dspApiResources,
        SelfDescriptionService selfDescriptionService
) {
}
