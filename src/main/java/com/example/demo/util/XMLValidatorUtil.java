package com.example.demo.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLValidatorUtil {

	public static void main(String[] args) {

		String xmlFilePath = "C:\\\\Users\\\\User\\\\Desktop\\\\rsa-userservice-v1\\\\src\\\\main\\\\resources\\\\records.xml";
		
	
		
		XMLValidatorUtil util = new XMLValidatorUtil();

		util.validateXML(xmlFilePath, util);

		System.out.println("COMPLETED SUCCESSFULLY");
	}

	private void validateXML(String xmlFilePath, XMLValidatorUtil util) {
		
		String xsdFilePath = "C:\\Users\\User\\Desktop\\rsa-userservice-v1\\src\\main\\resources\\records.xsd";

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try {
			Schema schema = schemaFactory.newSchema(new File(util.getURL("records.xsd")));
			Validator validator = schema.newValidator();
			
			

			Source xmlSource = new StreamSource(new File(xmlFilePath));
			validator.validate(xmlSource);
		} catch (SAXException e) {

			System.err.println(e.getMessage());
			String message=e.getMessage().toString();
			
			//System.out.println("message="+message);
			
			if(!message.contains("cvc-complex-type.2.4.d"))
			{
			

			String title[] = StringUtils.substringsBetween(e.getMessage(), "'", "'");
			
			System.out.println(title);

			util.updateXML(xmlFilePath, util, title,xsdFilePath);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void updateXML(String xmlFilePath, XMLValidatorUtil util, String title[],String xsdFilePath) {

		try {
			
			XsdElements xsdElements=new XsdElements();
			
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(xmlFilePath);

			List<String> missingElements = Arrays.asList(title[1].replaceAll("[{}]", "").split(","));
			
			System.out.println(missingElements);
			
			

			NodeList nodelist = document.getElementsByTagName(title[0]);
			//System.out.println("nodelist"+nodelist.toString());

			for (Node node : iterable(nodelist)) {
				List<String> childNodes = getChildNodeNames(node);
				
				List<String> parentNodes= xsdElements.getParentNodes(xsdFilePath);

				for (String missingElement : missingElements) {

					missingElement = missingElement.trim();
					
					

					if (!childNodes.contains(missingElement)) {
						
						if(parentNodes.contains(missingElement))
							//if(missingElement.equals("name"))
							{
								
								Element id = document.createElement(missingElement);
								id.appendChild(document.createTextNode(""));
								node.appendChild(id);
								System.out.println("inside parent"+missingElement);
								
							}
						else
						{
						

						Element id = document.createElement(missingElement);
						id.appendChild(document.createTextNode("DefaultValue"));
						node.appendChild(id);
						}

					}

				}

			}

			DOMSource domSource = new DOMSource(document);

			StreamResult streamResult = new StreamResult(new File(xmlFilePath));

			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(domSource, streamResult);

			util.validateXML(xmlFilePath, util);

		} catch (Exception e) {
			System.err.println("Exception Happened");
		}
	}

	public static Iterable<Node> iterable(final NodeList nodeList) {
		return () -> new Iterator<Node>() {

			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < nodeList.getLength();
			}

			@Override
			public Node next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return nodeList.item(index++);
			}
		};
	}

	private List<String> getChildNodeNames(Node node) {

		List<String> ChildNodeList = new ArrayList<>();

		for (Node childNode : iterable(node.getChildNodes())) {

			ChildNodeList.add(childNode.getNodeName());
		}

		return ChildNodeList;

	}

	private String getURL(String filename) {
		URL resource = getClass().getClassLoader().getResource(filename);
		Objects.requireNonNull(resource);

		return resource.getFile();
	}

}
