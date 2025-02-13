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

package utopiaia.odc.extension.messenger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import utopiaia.odc.extension.messenger.controller.SovityMessageController;
import utopiaia.odc.extension.messenger.impl.JsonObjectFromSovityMessageRequest;
import utopiaia.odc.extension.messenger.impl.JsonObjectFromSovityMessageResponse;
import utopiaia.odc.extension.messenger.impl.MessageEmitter;
import utopiaia.odc.extension.messenger.impl.MessageReceiver;
import utopiaia.odc.extension.messenger.impl.ObjectMapperFactory;
import utopiaia.odc.extension.messenger.impl.SovityMessageRequest;
import lombok.val;
import org.eclipse.edc.protocol.dsp.api.configuration.DspApiConfiguration;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpRemoteMessageDispatcher;
import org.eclipse.edc.protocol.dsp.spi.serialization.JsonLdRemoteMessageSerializer;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.agent.ParticipantAgentService;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.WebService;

@Provides({SovityMessenger.class, SovityMessengerRegistry.class})
public class SovityMessengerExtension implements ServiceExtension {

    public static final String NAME = "SovityMessenger";

    @Inject
    private DspApiConfiguration dspApiConfiguration;

    @Inject
    private DspHttpRemoteMessageDispatcher dspHttpRemoteMessageDispatcher;

    @Inject
    private IdentityService identityService;

    @Inject
    private JsonLdRemoteMessageSerializer jsonLdRemoteMessageSerializer;

    @Inject
    private Monitor monitor;

    @Inject
    private RemoteMessageDispatcherRegistry registry;

    @Inject
    private TypeManager typeManager;

    @Inject
    private TypeTransformerRegistry typeTransformerRegistry;

    @Inject
    private WebService webService;

    @Inject
    private ParticipantAgentService participantAgentService;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        val objectMapper = new ObjectMapperFactory().createObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        val handlers = new SovityMessengerRegistry();
        setupSovityMessengerEmitter(context, objectMapper);
        setupSovityMessengerReceiver(context, objectMapper, handlers);
    }

    private void setupSovityMessengerEmitter(ServiceExtensionContext context, ObjectMapper objectMapper) {
        val factory = new MessageEmitter(jsonLdRemoteMessageSerializer);
        val delegate = new MessageReceiver(objectMapper);

        dspHttpRemoteMessageDispatcher.registerMessage(SovityMessageRequest.class, factory, delegate);

        typeTransformerRegistry.register(new JsonObjectFromSovityMessageRequest());

        val sovityMessenger = new SovityMessenger(registry, objectMapper, monitor);
        context.registerService(SovityMessenger.class, sovityMessenger);
    }

    private void setupSovityMessengerReceiver(ServiceExtensionContext context, ObjectMapper objectMapper, SovityMessengerRegistry handlers) {
        val receiver = new SovityMessageController(
            identityService,
            dspApiConfiguration.getDspCallbackAddress(),
            typeTransformerRegistry,
            monitor,
            objectMapper,
            participantAgentService,
            handlers);

        webService.registerResource(dspApiConfiguration.getContextAlias(), receiver);

        context.registerService(SovityMessengerRegistry.class, handlers);

        typeTransformerRegistry.register(new JsonObjectFromSovityMessageResponse());
    }
}
