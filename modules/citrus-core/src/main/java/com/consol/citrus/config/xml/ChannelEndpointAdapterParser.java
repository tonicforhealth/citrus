/*
 * Copyright 2006-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.config.xml;

import com.consol.citrus.channel.ChannelEndpointAdapter;
import com.consol.citrus.channel.ChannelSyncEndpointConfiguration;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parses endpoint adapter bean definition for channel endpoint adapter.
 * @author Christoph Deppisch
 * @since 1.4
 */
public class ChannelEndpointAdapterParser extends AbstractBeanDefinitionParser {

    @Override
    public AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ChannelEndpointAdapter.class);

        BeanDefinitionBuilder endpointConfiguration = BeanDefinitionBuilder.genericBeanDefinition(ChannelSyncEndpointConfiguration.class);
        new ChannelSyncEndpointParser().parseEndpointConfiguration(endpointConfiguration, element, parserContext);

        String endpointConfigurationId = element.getAttribute(ID_ATTRIBUTE) + "EndpointAdapterConfiguration";
        BeanDefinitionHolder configurationHolder = new BeanDefinitionHolder(endpointConfiguration.getBeanDefinition(), endpointConfigurationId);
        registerBeanDefinition(configurationHolder, parserContext.getRegistry());
        if (shouldFireEvents()) {
            BeanComponentDefinition componentDefinition = new BeanComponentDefinition(configurationHolder);
            postProcessComponentDefinition(componentDefinition);
            parserContext.registerComponent(componentDefinition);
        }

        builder.addConstructorArgReference(endpointConfigurationId);

        return builder.getBeanDefinition();
    }
}
