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

package utopiaia.odc.ext.wrapper.api.common.mappers.asset.utils;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jsoup.Jsoup;

public class ShortDescriptionBuilder {

    public String buildShortDescription(String descriptionMarkdown) {
        if (descriptionMarkdown == null) {
            return null;
        }

        var text = extractMarkdownText(descriptionMarkdown);
        return abbreviate(text, 300);
    }

    public String extractMarkdownText(String markdown) {
        var options = new MutableDataSet();
        var parser = Parser.builder(options).build();
        var renderer = HtmlRenderer.builder(options).build();
        var document = parser.parse(markdown);
        var html = renderer.render(document);
        return Jsoup.parse(html).text();
    }

    String abbreviate(String text, int maxCharacters) {
        if (text == null) {
            return null;
        }
        return text.substring(0, Math.min(maxCharacters, text.length()));
    }
}
