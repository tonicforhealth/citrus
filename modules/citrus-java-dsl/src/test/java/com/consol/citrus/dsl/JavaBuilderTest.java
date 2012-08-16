/*
 * Copyright 2006-2012 the original author or authors.
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

package com.consol.citrus.dsl;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.consol.citrus.actions.EchoAction;
import com.consol.citrus.actions.JavaAction;
import com.consol.citrus.context.TestContext;

public class JavaBuilderTest {
    
    @Test
    public void testJavaBuilderWithClassName() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final List<Object> constructorArgs = new ArrayList<Object>();
        constructorArgs.add(5);
        constructorArgs.add(7);
        
        final List<Object> methodArgs = new ArrayList<Object>();
        methodArgs.add(4);
        
        TestNGCitrusTestBuilder builder = new TestNGCitrusTestBuilder() {
            @Override
            public void configure() {
                java("com.consol.citrus.dsl.util.JavaTest")
                      .constructorArgs(constructorArgs)
                      .methodArgs(methodArgs)
                      .method("add");
            }
        };
        
        builder.configure();
        
        Assert.assertEquals(builder.getTestCase().getActions().size(), 1);
        Assert.assertEquals(builder.getTestCase().getActions().get(0).getClass(), JavaAction.class);
        
        JavaAction action = ((JavaAction)builder.getTestCase().getActions().get(0));
        Assert.assertEquals(action.getName(), JavaAction.class.getSimpleName());
        
        Assert.assertEquals(action.getClassName(), "com.consol.citrus.dsl.util.JavaTest");
        Assert.assertEquals(action.getMethodName(), "add");
        Assert.assertEquals(action.getMethodArgs().size(), 1);
    }
    
    @Test
    public void testJavaBuilderWithClass() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final List<Object> methodArgs = new ArrayList<Object>();
        methodArgs.add(new TestContext());
        
        TestNGCitrusTestBuilder builder = new TestNGCitrusTestBuilder() {
            @Override
            public void configure() {
                java(EchoAction.class)
                      .methodArgs(methodArgs)
                      .method("execute");
            }
        };
        
        builder.configure();
        
        Assert.assertEquals(builder.getTestCase().getActions().size(), 1);
        Assert.assertEquals(builder.getTestCase().getActions().get(0).getClass(), JavaAction.class);
        
        JavaAction action = ((JavaAction)builder.getTestCase().getActions().get(0));
        Assert.assertEquals(action.getName(), JavaAction.class.getSimpleName());
        
        Assert.assertEquals(action.getClassName(), EchoAction.class.getSimpleName());
        Assert.assertEquals(action.getMethodName(), "execute");
        Assert.assertEquals(action.getMethodArgs().size(), 1);
    }

}
