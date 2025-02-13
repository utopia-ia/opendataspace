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

package utopiaia.odc.ext.wrapper.api.common.mappers;

import utopiaia.odc.ext.wrapper.api.common.mappers.asset.AssetEditRequestMapper;
import utopiaia.odc.ext.wrapper.api.common.mappers.asset.AssetJsonLdBuilder;
import utopiaia.odc.ext.wrapper.api.common.mappers.asset.AssetJsonLdParser;
import utopiaia.odc.ext.wrapper.api.common.mappers.asset.OwnConnectorEndpointService;
import utopiaia.odc.ext.wrapper.api.common.mappers.asset.utils.AssetJsonLdUtils;
import utopiaia.odc.ext.wrapper.api.common.mappers.asset.utils.EdcPropertyUtils;
import utopiaia.odc.ext.wrapper.api.common.mappers.asset.utils.ShortDescriptionBuilder;
import utopiaia.odc.ext.wrapper.api.common.mappers.dataaddress.DataSourceMapper;
import utopiaia.odc.ext.wrapper.api.common.mappers.dataaddress.http.HttpDataSourceMapper;
import utopiaia.odc.ext.wrapper.api.common.mappers.dataaddress.http.HttpHeaderMapper;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.jetbrains.annotations.NotNull;

import static org.mockito.Mockito.mock;

public class Factory {
    @NotNull
    public static AssetMapper newAssetMapper(
        TypeTransformerRegistry transformerRegistry,
        OwnConnectorEndpointService ownConnectorEndpointService
    ) {
        return new AssetMapper(
            transformerRegistry,
            newAssetJsonLdBuilder(ownConnectorEndpointService),
            newAssetJsonLdParser(ownConnectorEndpointService),
            new TitaniumJsonLd(mock(Monitor.class))
        );
    }

    @NotNull
    public static AssetJsonLdBuilder newAssetJsonLdBuilder(OwnConnectorEndpointService ownConnectorEndpointService) {
        return new AssetJsonLdBuilder(
            new DataSourceMapper(
                new EdcPropertyUtils(),
                new HttpDataSourceMapper(new HttpHeaderMapper(), new PlaceholderEndpointService("http://example.com/dummy/baseUrl"))
            ),
            newAssetJsonLdParser(ownConnectorEndpointService),
            new AssetEditRequestMapper()
        );
    }

    @NotNull
    public static AssetJsonLdParser newAssetJsonLdParser(OwnConnectorEndpointService ownConnectorEndpointService) {
        return new AssetJsonLdParser(
            new AssetJsonLdUtils(),
            new ShortDescriptionBuilder(),
            ownConnectorEndpointService
        );
    }
}
