/*
 * Copyright 2006-2015 the original author or authors.
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

package com.consol.citrus.camel.actions;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Christoph Deppisch
 * @since 2.4
 */
public class CreateCamelRouteAction extends AbstractCamelRouteAction {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(CreateCamelRouteAction.class);

    /** Camel route */
    private List<RouteDefinition> routes;

    /**
     * Default constructor.
     */
    public CreateCamelRouteAction() {
        setName("create-routes");
    }

    @Override
    public void doExecute(TestContext context) {
        for (RouteDefinition routeDefinition : routes) {
            try {
                log.info(String.format("Creating new Camel route '%s' in context '%s'", routeDefinition.getId(), camelContext.getName()));
                camelContext.addRouteDefinition(routeDefinition);
                log.info(String.format("Successfully created new Camel route '%s'", routeDefinition.getId()));
            } catch (Exception e) {
                throw new CitrusRuntimeException(String.format("Failed to create route definition '%s' in context", routeDefinition.getId(), camelContext.getName()), e);
            }
        }
    }

    /**
     * Gets the route definitions.
     * @return
     */
    public List<RouteDefinition> getRoutes() {
        return routes;
    }

    /**
     * Sets the route definitions.
     * @param routes
     */
    public CreateCamelRouteAction setRoutes(List<RouteDefinition> routes) {
        this.routes = routes;
        return this;
    }
}
