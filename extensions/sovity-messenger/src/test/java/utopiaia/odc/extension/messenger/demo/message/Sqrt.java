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

package utopiaia.odc.extension.messenger.demo.message;


import com.fasterxml.jackson.annotation.JsonProperty;
import utopiaia.odc.extension.messenger.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Sqrt implements SovityMessage {

    private static final String TYPE = "demo-sqrt";

    @Override
    public String getType() {
        return TYPE;
    }

    @JsonProperty
    private double value;

}
