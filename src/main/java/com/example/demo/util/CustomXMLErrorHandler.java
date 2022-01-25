package com.example.demo.util;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CustomXMLErrorHandler implements ErrorHandler {

	private List<SAXException> exceptionList = new ArrayList<>();

	public List<SAXException> getExceptionList() {
		return exceptionList;
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {

		exceptionList.add(exception);

	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		exceptionList.add(exception);

	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		exceptionList.add(exception);

	}

}
