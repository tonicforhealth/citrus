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

package com.consol.citrus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.functions.FunctionUtils;
import com.consol.citrus.variable.VariableUtils;

/**
 * Test case executing a list of {@link TestAction} in sequence.
 *
 * @author Christoph Deppisch
 * @since 2006
 */
public class TestCase implements BeanNameAware {

    /** Test chain containing test actions to be executed */
    private List<TestAction> testChain = new ArrayList<TestAction>();

    /** Further chain of test actions to be executed in any case (Success, error)
     * Usually used to clean up database in any case of test result */
    private List<TestAction> finallyChain = new ArrayList<TestAction>();

    /** Tests variables */
    private Map<String, String> variableDefinitions = new HashMap<String, String>();

    /** Variables valid for this test **/
    @Autowired
    private TestContext context;

    /** Name of testcase */
    private String name = TestCase.class.getSimpleName();

    /** Meta-Info */
    private TestCaseMetaInfo metaInfo = new TestCaseMetaInfo();
    
    /** TestCase description */
    private String description;

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(TestCase.class);

    /**
     * Method executes the test case and all its actions.
     */
    public void execute() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing TestCase");
        }

        /* build up the global test variables in TestContext by
         * getting the names and the current values of all variables */
        for (Entry<String, String> entry : variableDefinitions.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (VariableUtils.isVariableName(value)) {
                value = context.getVariable(value);
            } else if(context.getFunctionRegistry().isFunction(value)) {
                value = FunctionUtils.resolveFunction(value, context);
            }

            context.setVariable(key, value);
        }

        /* Debug print all variables */
        if (context.hasVariables() && log.isDebugEnabled()) {
            log.debug("TestCase using the following global variables:");
            for (Entry<String, String> entry : context.getVariables().entrySet()) {
                log.debug(entry.getKey() + " = " + entry.getValue());
            }
        }

        /* execute the test actions */
        for (int i = 0; i < testChain.size(); i++) {
            final TestAction action = testChain.get(i);

            log.info("");
            log.info("TESTACTION " + (i+1) + "/" + testChain.size());

            /* execute the test action and validate its success */
            action.execute(context);
        }
    }

    /**
     * Method that will be executed in any case of test case result (success, error)
     * Usually used for clean up tasks.
     */
    public void finish() {
        if (!finallyChain.isEmpty()) {
            log.info("Now reaching finally block to finish test case");
        }

        /* walk through the finally chain and execute the actions in there */
        for (TestAction action : finallyChain) {
            /* execute the test action and validate its success */
            action.execute(context);
        }
    }

    /**
     * Setter for test action chain.
     * @param testChain
     */
    public void setTestChain(List<TestAction> testChain) {
        this.testChain = testChain;
    }
    /**
     * Setter for variables.
     * @param variableDefinitions
     */
    public void setVariableDefinitions(Map<String, String> variableDefinitions) {
        this.variableDefinitions = variableDefinitions;
    }

    /**
     * Get actions count in this test case.
     * @return count actions
     */
    public int getCountActions() {
        return testChain.size() + finallyChain.size();
    }

    /**
     * Setter for finally chain.
     * @param finallyChain
     */
    public void setFinallyChain(List<TestAction> finallyChain) {
        this.finallyChain = finallyChain;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("[testVariables:");

        for (Entry<String, String> entry : variableDefinitions.entrySet()) {
            buf.append(entry.getKey() + "=" + entry.getValue() + ";");
        }

        buf.append("] ");

        buf.append("[testChain:");

        for (Iterator<TestAction> iter = testChain.iterator(); iter.hasNext();) {
            String className = iter.next().getClass().getName();
            buf.append(className + ";");
        }

        buf.append("] ");

        return super.toString() + buf.toString();
    }

    /**
     * Adding element to test action chain.
     * @param testAction
     */
    public void addTestChainAction(TestAction testAction) {
        this.testChain.add(testAction);
    }

    /**
     * Adding element to finally action chain.
     * @param testAction
     */
    public void addFinallyChainAction(TestAction testAction) {
        this.finallyChain.add(testAction);
    }

    /**
     * Get the test case meta information.
     * @return the metaInfo
     */
    public TestCaseMetaInfo getMetaInfo() {
        return metaInfo;
    }

    /**
     * Set the test case meta information.
     * @param metaInfo the metaInfo to set
     */
    public void setMetaInfo(TestCaseMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    /**
     * Get the test case name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the test case name.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get all actions in the finally chain.
     * @return the finallyChain
     */
    public List<TestAction> getFinallyChain() {
        return finallyChain;
    }

    /**
     * Set all actions in the finally chain.
     * @return the testChain
     */
    public List<TestAction> getTestChain() {
        return testChain;
    }

    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name) {
        if (this.name == null) {
            this.name = name;
        }
    }

    /**
     * Get the test case description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the test case description.
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the test context.
     * @return the variables
     */
    public TestContext getTestContext() {
        return context;
    }

    /**
     * Set the test context.
     * @param context the context to set
     */
    public void setTestContext(TestContext context) {
        this.context = context;
    }
}
