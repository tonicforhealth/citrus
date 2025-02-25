/*
 * Copyright 2006-2010 the original author or authors.
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

package com.consol.citrus.doc;

import com.consol.citrus.exceptions.CitrusRuntimeException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.xml.transform.StringSource;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

/**
 * Class to automatically generate a list of all available tests in MS Excel.
 * 
 * @author Christoph Deppisch
 * @since 2007
 */
public class ExcelTestDocGenerator extends AbstractTestDocGenerator {

    /** Test doc specific information */
    private String pageTitle = "Citrus Test Documentation";
    private String company = "Unknown";
    private String author = "Citrus Testframework";
    private Resource headers = new ClassPathResource("testdoc-header.xml", ExcelTestDocGenerator.class);
    private String customHeaders = "";
    
    /**
     * Default constructor using test doc template name.
     */
    public ExcelTestDocGenerator() {
        super("CitrusTests.xls", "testdoc.xls.template");
    }
    
    @Override
    public void doHeader(OutputStream buffered) throws TransformerException,
            IOException, SAXException {
        // no header information here.
    }
    
    @Override
    public void doBody(OutputStream buffered) throws TransformerException, IOException, SAXException {
        StreamResult res = new StreamResult(buffered);
        Transformer t = getTransformer("generate-xls-doc.xslt", "text/xml", "xml");
        
        if (StringUtils.hasText(customHeaders)) {
            t.transform(new StringSource(buildHeaderXml()), res);
        } else {
            t.transform(new StreamSource(headers.getInputStream()), res);
        }
        
        int testNumber = 1;
        for (File testFile : getTestFiles()) {
        	buffered.write("<Row>".getBytes());

            Source xml = new DOMSource(getDocumentBuilder().parse(testFile));
            buffered.write(("<Cell><Data ss:Type=\"Number\">" + testNumber + "</Data></Cell>").getBytes());

            t.transform(xml, res);
            buffered.write(("<Cell><Data ss:Type=\"String\">" + testFile.getName() + "</Data></Cell>").getBytes());
            buffered.write("</Row>".getBytes());
            
            testNumber++;
        }
    }
    
    @Override
    protected Properties getTestDocProperties() {
        Properties props = new Properties();
        props.setProperty("page.title", pageTitle);
        props.setProperty("company", company);
        props.setProperty("author", author);
        props.setProperty("date", String.format("%1$tY-%1$tm-%1$td", new GregorianCalendar()));
        
        return props;
    }

    /**
     * Builds custom header information.
     * @return
     */
    private String buildHeaderXml() {
        StringBuffer buf = new StringBuffer();
        
        buf.append("<headers xmlns=\"http://www.citrusframework.org/schema/doc/header\">");
        
        StringTokenizer tok = new StringTokenizer(customHeaders, ";");
        
        while (tok.hasMoreElements()) {
            buf.append("<header>" + tok.nextToken() + "</header>");
        }
        
        buf.append("</headers>");
        
        return buf.toString();
    }

    /**
     * Builds a new test doc generator.
     * @return
     */
    public static ExcelTestDocGenerator build() {
        return new ExcelTestDocGenerator();
    }
    
    /**
     * Adds a custom output file.
     * @param filename the output file name.
     * @return
     */
    public ExcelTestDocGenerator withOutputFile(String filename) {
        this.setOutputFile(filename);
        return this;
    }
    
    /**
     * Adds a custom page title.
     * @param pageTitle the page title.
     * @return
     */
    public ExcelTestDocGenerator withPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }
    
    /**
     * Adds a custom test source directory.
     * @param testDir the test source directory.
     * @return
     */
    public ExcelTestDocGenerator useSrcDirectory(String testDir) {
        this.setSrcDirectory(testDir);
        return this;
    }
    
    /**
     * Adds a custom author name.
     * @param author the author name.
     * @return
     */
    public ExcelTestDocGenerator withAuthor(String author) {
        this.author = author;
        return this;
    }
    
    /**
     * Adds a custom company.
     * @param company the company name.
     * @return
     */
    public ExcelTestDocGenerator withCompany(String company) {
        this.company = company;
        return this;
    }
    
    /**
     * Adds a custom header configuration.
     * @param customHeaders the header configuration.
     * @return
     */
    public ExcelTestDocGenerator withCustomHeaders(String customHeaders) {
        this.customHeaders = customHeaders;
        return this;
    }
    
    /**
     * Executable application cli.
     * @param args
     */
    public static void main(String[] args) {
        try {    
            ExcelTestDocGenerator creator = ExcelTestDocGenerator.build();
            
            creator.useSrcDirectory(args.length == 1 ? args[0] : creator.srcDirectory)
                .withOutputFile(args.length == 2 ? args[1] : creator.outputFile)
                .withPageTitle(args.length == 3 ? args[2] : creator.pageTitle)
                .withAuthor(args.length == 4 ? args[3] : creator.author)
                .withCompany(args.length == 5 ? args[4] : creator.company)
                .withCustomHeaders(args.length == 6 ? args[5] : "");
            
            creator.generateDoc();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CitrusRuntimeException("Wrong usage exception! " +
                    "Use parameters in the following way: [test.directory] [output.file]", e);
        }
    }

    /**
     * @param pageTitle the pageTitle to set
     */
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    /**
     * @return the pageTitle
     */
    public String getPageTitle() {
        return pageTitle;
    }

    /**
     * @return the company
     */
    public String getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }
}
