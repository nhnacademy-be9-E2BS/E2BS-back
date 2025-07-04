package com.nhnacademy.back.product.product.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;

public class AladdinOpenAPIHandler extends DefaultHandler {
	private List<Item> items = new ArrayList<>();
	private Item currentItem;
	private boolean inItemElement = false;
	private String tempValue;

	public List<Item> getItems() {
		return items;
	}

	@Override
	public void startDocument() throws SAXException {
		items = new ArrayList<>();
	}

	@Override
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
		} else if (localName.equals("pubDate")) {
			tempValue = "";
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempValue = tempValue + new String(ch, start, length);
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) {
		if (inItemElement) {
			if (localName.equals("item")) {
				items.add(currentItem);
				currentItem = null;
				inItemElement = false;
			} else if (localName.equals("title")) {
				currentItem.setTitle(tempValue);
			} else if (localName.equals("link")) {
				currentItem.setLink(tempValue);
			} else if (localName.equals("isbn13")) {
				currentItem.setIsbn13(tempValue);
			} else if (localName.equals("priceStandard")) {
				currentItem.setPriceStandard(Integer.parseInt(tempValue));
			} else if (localName.equals("priceSales")) {
				currentItem.setPriceSales(Integer.parseInt(tempValue));
			} else if (localName.equals("description")) {
				currentItem.setDescription(tempValue);
			} else if (localName.equals("publisher")) {
				currentItem.setPublisher(tempValue);
			} else if (localName.equals("author")) {
				currentItem.setAuthor(tempValue);
			} else if (localName.equals("cover")) {
				currentItem.setCover(tempValue);
			} else if (localName.equals("stockstatus")) {
				currentItem.setStockstatus(tempValue);
			} else if (localName.equals("pubDate")) {
				currentItem.setPubDate(LocalDate.parse(tempValue));
			}
		}
	}

	public void parseXml(String xmlUrl) throws IOException, ParserConfigurationException, SAXException {
		HttpURLConnection conn = null;
		StringBuilder response = new StringBuilder();

		try {
			URI uri = new URI(xmlUrl);             // URL 대신 URI 사용
			URL url = uri.toURL();                 // 변환 후 사용

			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000); // 5초 타임아웃 설정
			conn.setReadTimeout(5000);    // 5초 읽기 타임아웃

			try (BufferedReader in = new BufferedReader(
				new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = in.readLine()) != null) {
					response.append(line);
				}
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e); // NOSONAR
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		String fixedXml = response.toString()
			.replace("<hr>", "<hr/>")
			.replace("&nbsp;", " ");

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		ParserAdapter pa = new ParserAdapter(sp.getParser());

		pa.setContentHandler(this);
		pa.parse(new InputSource(new StringReader(fixedXml)));
	}

}