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

import utopiaia.odc.ext.wrapper.api.ui.model.DashboardMiwConfig;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import static com.apicatalog.jsonld.StringUtils.isBlank;

@RequiredArgsConstructor
public class MiwConfigService {
    private final Config config;

    private static final String MIW_AUTHORITY_ID = configKey("TX_SSI_MIW_AUTHORITY_ID");

    private static final String MIW_URL = configKey("TX_SSI_MIW_URL");

    private static final String MIW_TOKEN_URL = configKey("TX_SSI_OAUTH_TOKEN_URL");

    public DashboardMiwConfig buildMiwConfigOrNull() {
        var miwConfig = new DashboardMiwConfig();
        miwConfig.setUrl(configValue(MIW_URL));
        miwConfig.setAuthorityId(configValue(MIW_AUTHORITY_ID));
        miwConfig.setTokenUrl(configValue(MIW_TOKEN_URL));
        return isBlank(miwConfig.getUrl()) ? null : miwConfig;
    }

    String configValue(String configKey) {
        return config.getString(configKey, "");
    }

    /**
     * Maps a {@literal CONFIG_KEY} to {@literal config.key}
     *
     * @param envVarKey {@literal CONFIG_KEY}
     * @return {@literal config.key}
     */
    public static String configKey(String envVarKey) {
        return String.join(".", envVarKey.split("_")).toLowerCase();
    }
}
