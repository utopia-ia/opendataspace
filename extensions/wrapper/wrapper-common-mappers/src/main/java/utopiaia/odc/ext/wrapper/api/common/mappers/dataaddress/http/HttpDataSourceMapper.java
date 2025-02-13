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

package utopiaia.odc.ext.wrapper.api.common.mappers.dataaddress.http;

import utopiaia.odc.ext.wrapper.api.common.mappers.PlaceholderEndpointService;
import utopiaia.odc.ext.wrapper.api.common.model.UiDataSourceHttpData;
import utopiaia.odc.ext.wrapper.api.common.model.UiDataSourceOnRequest;
import utopiaia.odc.utils.jsonld.vocab.Prop;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;


@RequiredArgsConstructor
public class HttpDataSourceMapper {
    private final HttpHeaderMapper httpHeaderMapper;
    private final PlaceholderEndpointService placeholderEndpointService;

    /**
     * Data Address for type HTTP_DATA
     *
     * @param httpData {@link UiDataSourceHttpData}
     * @return properties for {@link org.eclipse.edc.spi.types.domain.DataAddress}
     */
    public Map<String, String> buildDataAddress(@NonNull UiDataSourceHttpData httpData) {
        var baseUrl = requireNonNull(httpData.getBaseUrl(), "baseUrl must not be null");
        var props = new HashMap<>(Map.of(
            Prop.Edc.TYPE, Prop.Edc.DATA_ADDRESS_TYPE_HTTP_DATA,
            Prop.Edc.BASE_URL, baseUrl
        ));

        if (httpData.getMethod() != null) {
            props.put(Prop.Edc.METHOD, httpData.getMethod().name());
        }

        if (StringUtils.isNotBlank(httpData.getQueryString())) {
            props.put(Prop.Edc.QUERY_PARAMS, httpData.getQueryString());
        }

        if (StringUtils.isNotBlank(httpData.getAuthHeaderName())) {
            props.put(Prop.Edc.AUTH_KEY, httpData.getAuthHeaderName());
            if (httpData.getAuthHeaderValue() != null) {
                if (httpData.getAuthHeaderValue().getRawValue() != null) {
                    props.put(Prop.Edc.AUTH_CODE, httpData.getAuthHeaderValue().getRawValue());
                } else if (httpData.getAuthHeaderValue().getSecretName() != null) {
                    props.put(Prop.Edc.SECRET_NAME, httpData.getAuthHeaderValue().getSecretName());
                }
            }
        }

        props.putAll(httpHeaderMapper.buildHeaderProps(httpData.getHeaders()));

        // Parameterization
        if (Boolean.TRUE.equals(httpData.getEnableMethodParameterization())) {
            props.put(Prop.Edc.PROXY_METHOD, "true");
        }
        if (Boolean.TRUE.equals(httpData.getEnablePathParameterization())) {
            props.put(Prop.Edc.PROXY_PATH, "true");
        }
        if (Boolean.TRUE.equals(httpData.getEnableQueryParameterization())) {
            props.put(Prop.Edc.PROXY_QUERY_PARAMS, "true");
        }
        if (Boolean.TRUE.equals(httpData.getEnableBodyParameterization())) {
            props.put(Prop.Edc.PROXY_BODY, "true");
        }

        return props;
    }

    public Map<String, String> buildOnRequestDataAddress(@NonNull UiDataSourceOnRequest onRequest) {
        var contactEmail = requireNonNull(onRequest.getContactEmail(), "contactEmail must not be null");
        var contactEmailSubject = requireNonNull(
            onRequest.getContactPreferredEmailSubject(),
            "Need contactPreferredEmailSubject"
        );

        var placeholderEndpointForAsset = placeholderEndpointService.getPlaceholderEndpointForAsset(
            onRequest.getContactEmail(),
            onRequest.getContactPreferredEmailSubject());

        var actualDataSource = UiDataSourceHttpData.builder()
            .baseUrl(placeholderEndpointForAsset)
            .build();

        var props = buildDataAddress(actualDataSource);
        props.put(Prop.UtopiaiaDcatExt.DATA_SOURCE_AVAILABILITY, Prop.UtopiaiaDcatExt.DATA_SOURCE_AVAILABILITY_ON_REQUEST);
        props.put(Prop.UtopiaiaDcatExt.CONTACT_EMAIL, contactEmail);
        props.put(Prop.UtopiaiaDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT, contactEmailSubject);
        return props;
    }

    /**
     * Public information from Data Address
     *
     * @param dataAddress data address
     * @return json object to be merged with asset properties
     */
    public JsonObject enhanceAssetWithDataSourceHints(Map<String, String> dataAddress) {
        var json = Json.createObjectBuilder();

        // Parameterization Hints
        var isOnRequest = Prop.UtopiaiaDcatExt.DATA_SOURCE_AVAILABILITY_ON_REQUEST
            .equals(dataAddress.get(Prop.UtopiaiaDcatExt.DATA_SOURCE_AVAILABILITY));
        if (!isOnRequest) {
            Map.of(
                Prop.Edc.PROXY_METHOD, Prop.UtopiaiaDcatExt.HttpDatasourceHints.METHOD,
                Prop.Edc.PROXY_PATH, Prop.UtopiaiaDcatExt.HttpDatasourceHints.PATH,
                Prop.Edc.PROXY_QUERY_PARAMS, Prop.UtopiaiaDcatExt.HttpDatasourceHints.QUERY_PARAMS,
                Prop.Edc.PROXY_BODY, Prop.UtopiaiaDcatExt.HttpDatasourceHints.BODY
            ).forEach((prop, hint) ->
                // Will add hints as "true" or "false"
                json.add(hint, String.valueOf("true".equals(dataAddress.get(prop))))
            );
        }

        // On Request information
        Set.of(
            Prop.UtopiaiaDcatExt.DATA_SOURCE_AVAILABILITY,
            Prop.UtopiaiaDcatExt.CONTACT_EMAIL,
            Prop.UtopiaiaDcatExt.CONTACT_PREFERRED_EMAIL_SUBJECT
        ).forEach(prop -> {
            var value = dataAddress.get(prop);
            if (StringUtils.isNotBlank(value)) {
                json.add(prop, value);
            }
        });

        return json.build();
    }
}
