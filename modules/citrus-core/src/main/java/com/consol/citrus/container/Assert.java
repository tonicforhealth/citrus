/*
 * Copyright 2006-2010 ConSol* Software GmbH.
 * 
 * This file is part of Citrus.
 * 
 * Citrus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Citrus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Citrus. If not, see <http://www.gnu.org/licenses/>.
 */

package com.consol.citrus.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.consol.citrus.TestAction;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.exceptions.ValidationException;

/**
 * Assert exception to happen in nested test action.
 * 
 * @author Christoph Deppisch
 * @since 2009
 */
public class Assert extends AbstractTestAction {
    /** Nested test action */
    private TestAction action;

    /** Asserted exception */
    private Class<? extends Throwable> exception = CitrusRuntimeException.class;

    /** Localized exception message for control */
    private String message = null;
    
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(Assert.class);

    /**
     * @see com.consol.citrus.TestAction#execute()
     */
    @Override
    public void execute(TestContext context) {
        log.info("Assert container asserting exceptions of type " + exception);

        try {
            action.execute(context);
        } catch (Exception e) {
            log.info("Validating caught exception ...");
            if (exception.isAssignableFrom(e.getClass())) {
                
                if(message != null && !message.equals(e.getLocalizedMessage())) {
                    throw new ValidationException("Validation failed for asserted exception message - expected: '" + message + "' but was: '" + e.getLocalizedMessage() + "'");
                }
                
                log.info("Exception is as expected: " + e.getClass() + ": " + e.getLocalizedMessage());
                log.info("Exception validation successful");
                return;
            } else {
                throw new ValidationException("Validation failed for asserted exception type - expected: '" + exception + "' but was: '" + e.getClass().getName() + "'");
            }
        }

        throw new ValidationException("Missing asserted exception '" + exception + "'");
    }

    /**
     * Set the exception.
     * @param exception the exception to set
     */
    public void setException(Class<Throwable> exception) {
        this.exception = exception;
    }

    /**
     * Set the nested test action.
     * @param action the action to set
     */
    public void setAction(TestAction action) {
        this.action = action;
    }

    /**
     * Set the message to send.
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the message to send.
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
