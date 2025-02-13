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
import utopiaia.odc.ext.wrapper.api.common.mappers.AssetMapper;
import utopiaia.odc.ext.wrapper.api.common.mappers.LegacyPolicyMapper;
import utopiaia.odc.ext.wrapper.api.common.mappers.PlaceholderEndpointService;
import utopiaia.odc.ext.wrapper.api.common.mappers.PolicyMapper;
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
import utopiaia.odc.ext.wrapper.api.common.mappers.policy.AtomicConstraintMapper;
import utopiaia.odc.ext.wrapper.api.common.mappers.policy.ExpressionExtractor;
import utopiaia.odc.ext.wrapper.api.common.mappers.policy.ExpressionMapper;
import utopiaia.odc.ext.wrapper.api.common.mappers.policy.LiteralMapper;
import utopiaia.odc.ext.wrapper.api.common.mappers.policy.OperatorMapper;
import utopiaia.odc.ext.wrapper.api.common.mappers.policy.PolicyValidator;
import utopiaia.odc.ext.wrapper.api.ui.UiResourceImpl;
import utopiaia.odc.ext.wrapper.api.ui.pages.asset.AssetApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.asset.AssetIdValidator;
import utopiaia.odc.ext.wrapper.api.ui.pages.catalog.CatalogApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.catalog.UiDataOfferBuilder;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_agreements.ContractAgreementPageApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_agreements.ContractAgreementTerminationApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_agreements.ContractAgreementTransferApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementDataFetcher;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementPageCardBuilder;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementUtils;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractNegotiationUtils;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_agreements.services.ParameterizationCompatibilityUtils;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_agreements.services.TransferRequestBuilder;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_definitions.ContractDefinitionApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_definitions.ContractDefinitionBuilder;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_definitions.CriterionLiteralMapper;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_definitions.CriterionMapper;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_definitions.CriterionOperatorMapper;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_negotiations.ContractNegotiationApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_negotiations.ContractNegotiationBuilder;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_negotiations.ContractNegotiationStateService;
import utopiaia.odc.ext.wrapper.api.ui.pages.contract_negotiations.ContractOfferMapper;
import utopiaia.odc.ext.wrapper.api.ui.pages.dashboard.DashboardPageApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.dashboard.services.DapsConfigService;
import utopiaia.odc.ext.wrapper.api.ui.pages.dashboard.services.DashboardDataFetcher;
import utopiaia.odc.ext.wrapper.api.ui.pages.dashboard.services.MiwConfigService;
import utopiaia.odc.ext.wrapper.api.ui.pages.dashboard.services.OwnConnectorEndpointServiceImpl;
import utopiaia.odc.ext.wrapper.api.ui.pages.dashboard.services.SelfDescriptionService;
import utopiaia.odc.ext.wrapper.api.ui.pages.data_offer.DataOfferPageApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.policy.PolicyDefinitionApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageApiService;
import utopiaia.odc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageAssetFetcherService;
import utopiaia.odc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessStateService;
import utopiaia.odc.ext.wrapper.api.usecase.UseCaseResourceImpl;
import utopiaia.odc.ext.wrapper.api.usecase.pages.catalog.FilterExpressionLiteralMapper;
import utopiaia.odc.ext.wrapper.api.usecase.pages.catalog.FilterExpressionMapper;
import utopiaia.odc.ext.wrapper.api.usecase.pages.catalog.FilterExpressionOperatorMapper;
import utopiaia.odc.ext.wrapper.api.usecase.pages.catalog.UseCaseCatalogApiService;
import utopiaia.odc.ext.wrapper.api.usecase.services.KpiApiService;
import utopiaia.odc.ext.wrapper.api.usecase.services.SupportedPolicyApiService;
import utopiaia.odc.ext.wrapper.controller.PlaceholderEndpointController;
import utopiaia.odc.extension.contacttermination.ContractAgreementTerminationService;
import utopiaia.odc.extension.db.directaccess.DslContextFactory;
import utopiaia.odc.extension.policy.services.AlwaysTruePolicyDefinitionService;
import utopiaia.odc.utils.catalog.DspCatalogService;
import utopiaia.odc.utils.catalog.mapper.DspDataOfferBuilder;
import utopiaia.odc.utils.config.ConfigProps;
import lombok.NoArgsConstructor;
import lombok.val;
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
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * Manual Dependency Injection.
 * <p>
 * We want to develop as Java Backend Development is done, but we have no CDI / DI Framework to rely
 * on.
 * <p>
 * EDC {@link Inject} only works in {@link WrapperExtension}.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class WrapperExtensionContextBuilder {

    public static WrapperExtensionContext buildContext(
        AssetIndex assetIndex,
        AssetService assetService,
        CatalogService catalogService,
        Config config,
        ContractAgreementService contractAgreementService,
        ContractAgreementTerminationService contractAgreementTerminationService,
        ContractDefinitionService contractDefinitionService,
        ContractDefinitionStore contractDefinitionStore,
        ContractNegotiationService contractNegotiationService,
        ContractNegotiationStore contractNegotiationStore,
        DslContextFactory dslContextFactory,
        JsonLd jsonLd,
        Monitor monitor,
        ObjectMapper objectMapper,
        PolicyDefinitionService policyDefinitionService,
        PolicyDefinitionStore policyDefinitionStore,
        PolicyEngine policyEngine,
        RuleBindingRegistry ruleBindingRegistry,
        TransferProcessService transferProcessService,
        TransferProcessStore transferProcessStore,
        TypeTransformerRegistry typeTransformerRegistry
    ) {
        // UI API
        var operatorMapper = new OperatorMapper();
        var criterionOperatorMapper = new CriterionOperatorMapper();
        var criterionLiteralMapper = new CriterionLiteralMapper();
        var criterionMapper = new CriterionMapper(criterionOperatorMapper, criterionLiteralMapper);
        var edcPropertyUtils = new EdcPropertyUtils();
        var selfDescriptionService = new SelfDescriptionService(config);
        var ownConnectorEndpointService = new OwnConnectorEndpointServiceImpl(selfDescriptionService);
        var placeholderEndpointService = new PlaceholderEndpointService(
            ConfigProps.MY_EDC_DATASOURCE_PLACEHOLDER_BASEURL.getStringOrThrow(config)
        );
        var assetMapper = newAssetMapper(typeTransformerRegistry, jsonLd, ownConnectorEndpointService, placeholderEndpointService);
        var policyMapper = newPolicyMapper(objectMapper, typeTransformerRegistry, operatorMapper);
        var transferProcessStateService = new TransferProcessStateService();
        var contractNegotiationUtils = new ContractNegotiationUtils(
            contractNegotiationService,
            selfDescriptionService
        );
        var contractAgreementPageCardBuilder = new ContractAgreementPageCardBuilder(
            policyMapper,
            transferProcessStateService,
            assetMapper,
            contractNegotiationUtils
        );
        var contractAgreementDataFetcher = new ContractAgreementDataFetcher(
            contractAgreementService,
            contractNegotiationStore,
            transferProcessService,
            assetIndex
        );
        var contractAgreementApiService = new ContractAgreementPageApiService(
            contractAgreementDataFetcher,
            contractAgreementPageCardBuilder
        );
        var contactDefinitionBuilder = new ContractDefinitionBuilder(criterionMapper);
        var contractDefinitionApiService = new ContractDefinitionApiService(
            contractDefinitionService,
            criterionMapper,
            contactDefinitionBuilder);
        var transferHistoryPageApiService = new TransferHistoryPageApiService(
            assetService,
            contractAgreementService,
            contractNegotiationStore,
            transferProcessService,
            transferProcessStateService
        );
        var transferHistoryPageAssetFetcherService = new TransferHistoryPageAssetFetcherService(
            assetService,
            transferProcessService,
            assetMapper,
            contractNegotiationStore,
            contractNegotiationUtils
        );
        var contractAgreementUtils = new ContractAgreementUtils(contractAgreementService);
        var parameterizationCompatibilityUtils = new ParameterizationCompatibilityUtils();
        var assetIdValidator = new AssetIdValidator();
        var assetApiService = new AssetApiService(
            assetService,
            assetMapper,
            assetIdValidator,
            selfDescriptionService
        );
        var transferRequestBuilder = new TransferRequestBuilder(
            contractAgreementUtils,
            contractNegotiationUtils,
            edcPropertyUtils,
            typeTransformerRegistry,
            parameterizationCompatibilityUtils
        );
        var contractAgreementTransferApiService = new ContractAgreementTransferApiService(
            transferRequestBuilder,
            transferProcessService
        );
        var contractAgreementTerminationApiService = new ContractAgreementTerminationApiService(contractAgreementTerminationService);
        var legacyPolicyMapper = new LegacyPolicyMapper();
        var policyDefinitionApiService = new PolicyDefinitionApiService(
            policyDefinitionService,
            policyMapper,
            legacyPolicyMapper
        );
        var dataOfferBuilder = new DspDataOfferBuilder(jsonLd);
        var uiDataOfferBuilder = new UiDataOfferBuilder(assetMapper, policyMapper);
        var dspCatalogService = new DspCatalogService(catalogService, dataOfferBuilder);
        var catalogApiService = new CatalogApiService(
            uiDataOfferBuilder,
            dspCatalogService
        );
        var contractOfferMapper = new ContractOfferMapper(policyMapper);
        var contractNegotiationBuilder = new ContractNegotiationBuilder(contractOfferMapper);
        var contractNegotiationStateService = new ContractNegotiationStateService();
        var contractNegotiationApiService = new ContractNegotiationApiService(
            contractNegotiationService,
            contractNegotiationBuilder,
            contractNegotiationStateService
        );
        var miwConfigBuilder = new MiwConfigService(config);
        var dapsConfigBuilder = new DapsConfigService(config);
        var dashboardDataFetcher = new DashboardDataFetcher(
            contractNegotiationStore,
            transferProcessService,
            assetIndex,
            policyDefinitionService,
            contractDefinitionService
        );
        var dashboardApiService = new DashboardPageApiService(
            dashboardDataFetcher,
            transferProcessStateService,
            dapsConfigBuilder,
            miwConfigBuilder,
            selfDescriptionService
        );
        var alwaysTruePolicyService = new AlwaysTruePolicyDefinitionService(
            policyDefinitionService
        );
        var dataOfferPageApiService = new DataOfferPageApiService(
            assetApiService,
            contractDefinitionApiService,
            policyDefinitionApiService,
            alwaysTruePolicyService
        );
        var uiResource = new UiResourceImpl(
            contractAgreementApiService,
            contractAgreementTransferApiService,
            contractAgreementTerminationApiService,
            transferHistoryPageApiService,
            transferHistoryPageAssetFetcherService,
            assetApiService,
            policyDefinitionApiService,
            catalogApiService,
            contractDefinitionApiService,
            contractNegotiationApiService,
            dashboardApiService,
            dslContextFactory,
            dataOfferPageApiService
        );

        // Use Case API
        var filterExpressionOperatorMapper = new FilterExpressionOperatorMapper();
        var filterExpressionLiteralMapper = new FilterExpressionLiteralMapper();
        var filterExpressionMapper = new FilterExpressionMapper(
            filterExpressionOperatorMapper,
            filterExpressionLiteralMapper
        );

        var kpiApiService = new KpiApiService(
            assetIndex,
            policyDefinitionStore,
            contractDefinitionStore,
            transferProcessStore,
            contractAgreementService,
            transferProcessStateService
        );
        var supportedPolicyApiService = new SupportedPolicyApiService(policyEngine);
        var useCaseCatalogApiService = new UseCaseCatalogApiService(
            uiDataOfferBuilder,
            dspCatalogService,
            filterExpressionMapper
        );
        var useCaseResource = new UseCaseResourceImpl(
            kpiApiService,
            supportedPolicyApiService,
            useCaseCatalogApiService
        );
        val placeholderEndpointController = new PlaceholderEndpointController();

        // Collect all JAX-RS resources
        return new WrapperExtensionContext(
            List.of(uiResource, useCaseResource),
            List.of(placeholderEndpointController),
            selfDescriptionService
        );
    }

    @NotNull
    private static AssetMapper newAssetMapper(
        TypeTransformerRegistry typeTransformerRegistry,
        JsonLd jsonLd,
        OwnConnectorEndpointService ownConnectorEndpointService,
        PlaceholderEndpointService placeholderEndpointService
    ) {
        var edcPropertyUtils = new EdcPropertyUtils();
        var assetJsonLdUtils = new AssetJsonLdUtils();
        var assetEditRequestMapper = new AssetEditRequestMapper();
        var shortDescriptionBuilder = new ShortDescriptionBuilder();
        var assetJsonLdParser = new AssetJsonLdParser(
            assetJsonLdUtils,
            shortDescriptionBuilder,
            ownConnectorEndpointService
        );
        var httpHeaderMapper = new HttpHeaderMapper();
        var httpDataSourceMapper = new HttpDataSourceMapper(httpHeaderMapper, placeholderEndpointService);
        var dataSourceMapper = new DataSourceMapper(
            edcPropertyUtils,
            httpDataSourceMapper
        );
        var assetJsonLdBuilder = new AssetJsonLdBuilder(
            dataSourceMapper,
            assetJsonLdParser,
            assetEditRequestMapper
        );
        return new AssetMapper(
            typeTransformerRegistry,
            assetJsonLdBuilder,
            assetJsonLdParser,
            jsonLd
        );
    }

    @NotNull
    private static PolicyMapper newPolicyMapper(
        ObjectMapper objectMapper,
        TypeTransformerRegistry typeTransformerRegistry,
        OperatorMapper operatorMapper
    ) {
        var literalMapper = new LiteralMapper(objectMapper);
        var atomicConstraintMapper = new AtomicConstraintMapper(literalMapper, operatorMapper);
        var policyValidator = new PolicyValidator();
        var expressionMapper = new ExpressionMapper(atomicConstraintMapper);
        var constraintExtractor = new ExpressionExtractor(policyValidator, expressionMapper);
        return new PolicyMapper(
            constraintExtractor,
            expressionMapper,
            typeTransformerRegistry
        );
    }
}
