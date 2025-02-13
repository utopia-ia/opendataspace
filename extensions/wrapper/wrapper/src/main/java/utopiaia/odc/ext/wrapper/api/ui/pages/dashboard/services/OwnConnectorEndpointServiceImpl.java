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

package utopiaia.odc.ext.wrapper.api.ui.pages.dashboard.services;

import utopiaia.odc.ext.wrapper.api.common.mappers.asset.OwnConnectorEndpointService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OwnConnectorEndpointServiceImpl implements OwnConnectorEndpointService {
    private final SelfDescriptionService selfDescriptionService;

    @Override
    public boolean isOwnConnectorEndpoint(String endpoint) {
        return selfDescriptionService.getConnectorEndpoint().equals(endpoint);
    }
}
