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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import utopiaia.odc.extension.contacttermination.ContractAgreementTerminationService;
import utopiaia.odc.extension.db.directaccess.DslContextFactory;
import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.connector.api.management.configuration.transform.ManagementApiTypeTransformerRegistry;
import org.eclipse.edc.connector.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.store.TransferProcessStore;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.protocol.dsp.api.configuration.DspApiConfiguration;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.CoreConstants;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.web.spi.WebService;

public class WrapperExtension implements ServiceExtension {

    public static final String EXTENSION_NAME = "WrapperExtension";

    @Inject
    private AssetIndex assetIndex;
    @Inject
    private AssetService assetService;
    @Inject
    private PolicyDefinitionService policyDefinitionService;
    @Inject
    private ContractAgreementService contractAgreementService;
    @Inject
    private ContractAgreementTerminationService contractAgreementTerminationService;
    @Inject
    private ContractDefinitionStore contractDefinitionStore;
    @Inject
    private ContractNegotiationService contractNegotiationService;
    @Inject
    private ContractNegotiationStore contractNegotiationStore;
    @Inject
    private DslContextFactory dslContextFactory;
    @Inject
    private DspApiConfiguration dspApiConfiguration;
    @Inject
    private ManagementApiConfiguration dataManagementApiConfiguration;
    @Inject
    private PolicyDefinitionStore policyDefinitionStore;
    @Inject
    private PolicyEngine policyEngine;
    @Inject
    private RuleBindingRegistry ruleBindingRegistry;
    @Inject
    private TransferProcessService transferProcessService;
    @Inject
    private TransferProcessStore transferProcessStore;
    @Inject
    private TypeManager typeManager;
    @Inject
    private ManagementApiTypeTransformerRegistry typeTransformerRegistry;
    @Inject
    private WebService webService;
    @Inject
    private ContractDefinitionService contractDefinitionService;
    @Inject
    private CatalogService catalogService;
    @Inject
    private JsonLd jsonLd;

    @Override
    public String name() {
        return EXTENSION_NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var objectMapper = typeManager.getMapper(CoreConstants.JSON_LD);
        fixObjectMapperDateSerialization(objectMapper);

        var wrapperExtensionContext = WrapperExtensionContextBuilder.buildContext(
            assetIndex,
            assetService,
            catalogService,
            context.getConfig(),
            contractAgreementService,
            contractAgreementTerminationService,
            contractDefinitionService,
            contractDefinitionStore,
            contractNegotiationService,
            contractNegotiationStore,
            dslContextFactory,
            jsonLd,
            context.getMonitor(),
            objectMapper,
            policyDefinitionService,
            policyDefinitionStore,
            policyEngine,
            ruleBindingRegistry,
            transferProcessService,
            transferProcessStore,
            typeTransformerRegistry
        );

        wrapperExtensionContext.managementApiResources().forEach(resource ->
            webService.registerResource(dataManagementApiConfiguration.getContextAlias(), resource));

        wrapperExtensionContext.dspApiResources().forEach(resource ->
            webService.registerResource(dspApiConfiguration.getContextAlias(), resource));
    }

    private void fixObjectMapperDateSerialization(ObjectMapper objectMapper) {
        // Fixes Dates in JSON-LD Object Mapper
        // The Core EDC uses longs over OffsetDateTime, so they never fixed the date format
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
