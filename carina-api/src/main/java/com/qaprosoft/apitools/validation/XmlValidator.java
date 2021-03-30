package com.qaprosoft.apitools.validation;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class XmlValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private XmlValidator() {
    }

    /**
     * @param mode - determines how to compare 2 XMLs. See type description for more details.
     */
    public static void validateXml(String actualXmlData, String expectedXmlPath, XmlCompareMode mode) {
        try {
            String expectedXmlData = Files.lines(Path.of(expectedXmlPath))
                    .collect(Collectors.joining("\n"));
            if (mode == XmlCompareMode.NON_STRICT) {
                XmlComparator.nonStrictOrderCompare(actualXmlData, expectedXmlData);
            } else {
                XmlComparator.strictCompare(actualXmlData, expectedXmlData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Validation of xml data successfully passed");
    }

    public static void validateXmlAgainstSchema(String xmlSchemaPath, String xmlData) {

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(new File(xmlSchemaPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlData)));
        } catch (SAXException e) {
            throw new AssertionError("Validation against Xml schema failed " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Can't read xml from String: " + e.getMessage(), e);
        }
        LOGGER.info("Validation against Xml schema successfully passed");
    }
}

