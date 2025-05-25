package com.nhnacademy.back.product.product.park.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;

public class AladdinOpenAPIHandler extends DefaultHandler {
	private List<Item> Items = new ArrayList<>();
	private Item currentItem;
	private boolean inItemElement = false;
	private String tempValue;

	public List<Item> getItems() {
		return Items; // null 걱정 없음
	}

	public AladdinOpenAPIHandler() {
	}


	@Override
	public void startDocument() throws SAXException {
		Items = new ArrayList<>();
	}


	public void startElement(String namespace, String localName, String qName, Attributes atts) {
		if (localName.equals("item")) {
			currentItem = new Item();
			inItemElement = true;
		} else if (localName.equals("title")) {
			tempValue = "";
		} else if (localName.equals("link")) {
			tempValue = "";
		} else if (localName.equals("publisher")) {
			tempValue = "";
		} else if (localName.equals("author")) {
			tempValue = "";
		} else if (localName.equals("description")) {
			tempValue = "";
		} else if (localName.equals("isbn13")) {
			tempValue = "";
		} else if (localName.equals("priceStandard")) {
			tempValue = "";
		} else if (localName.equals("priceSales")) {
			tempValue = "";
		} else if (localName.equals("cover")) {
			tempValue = "";
		} else if (localName.equals("stockstatus")) {
			tempValue = "";
		}  else if (localName.equals("pubDate")) {
			tempValue = "";
	}

}

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempValue = tempValue + new String(ch, start, length);
	}

	public void endElement(String namespaceURI, String localName, String qName) {
		if (inItemElement) {
			if (localName.equals("item")) {
				Items.add(currentItem);
				currentItem = null;
				inItemElement = false;
			} else if (localName.equals("title")) {
				currentItem.Title = tempValue;
			} else if (localName.equals("link")) {
				currentItem.Link = tempValue;
			} else if (localName.equals("isbn13")) {
				currentItem.isbn13 = tempValue;
			} else if (localName.equals("priceStandard")) {
				currentItem.priceStandard = Integer.parseInt(tempValue);
			} else if (localName.equals("priceSales")) {
				currentItem.priceSales = Integer.parseInt(tempValue);
			} else if (localName.equals("description")) {
				currentItem.description = tempValue;
			} else if (localName.equals("publisher")) {
				currentItem.publisher = tempValue;
			} else if (localName.equals("author")) {
				currentItem.author = tempValue;
			} else if (localName.equals("cover")) {
				currentItem.cover = tempValue;
			} else if (localName.equals("stockstatus")) {
				currentItem.stockstatus = tempValue;
			} else if (localName.equals("pubDate")) {
				currentItem.pubDate = LocalDate.parse(tempValue);
			}
		}
	}

	public void parseXml(String xmlUrl) throws Exception {
		URL url = new URL(xmlUrl);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();

		String fixedXml = response.toString()
			.replaceAll("<hr>", "<hr/>")
			.replaceAll("&nbsp;", " ");

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		ParserAdapter pa = new ParserAdapter(sp.getParser());

		pa.setContentHandler(this);
		pa.parse(new InputSource(new StringReader(fixedXml)));
	}

}