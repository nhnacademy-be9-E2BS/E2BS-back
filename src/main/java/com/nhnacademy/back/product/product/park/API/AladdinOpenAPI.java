package com.nhnacademy.back.product.product.park.API;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.ParserAdapter;

class Item{
	public String Title = ""; //제목

	public String publisher = ""; //출판사

	public String author =""; //저자


	public String description = ""; //상품설명

	public String isbn13 = ""; //13자리 ISBN

	public int priceStandard = 0; // 정가

	public int priceSales = 0; //판매가

	public String cover = ""; //상품 이미지
	public String Link = ""; // 상품링크

	public String stockstatus = ""; //재고상태


}

class AladdinOpenAPIHandler extends DefaultHandler {
	public List<Item> Items;
	private Item currentItem;
	private boolean inItemElement = false;
	private String tempValue;

	public AladdinOpenAPIHandler( ){
		Items = new ArrayList<Item>();
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
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException{
		tempValue = tempValue + new String(ch,start,length);
	}

	public void endElement(String namespaceURI, String localName,String qName) {
		if(inItemElement){
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
			}
		}
	}

	public void parseXml(String xmlUrl) throws Exception {
		// URL에서 문자열로 읽기
		URL url = new URL(xmlUrl);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
		StringBuilder response = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();

		// HTML 태그 정제
		String fixedXml = response.toString()
			.replaceAll("<hr>", "<hr/>")
			.replaceAll("&nbsp;", " ");

		// SAX 파서
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		ParserAdapter pa = new ParserAdapter(sp.getParser());

		pa.setContentHandler(this);
		pa.parse(new InputSource(new StringReader(fixedXml)));
	}



}

public class AladdinOpenAPI {

	public static String getSerachUrl(String query) throws Exception {
		String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?";

		Map<String,String> hashMap = new LinkedHashMap<String,String>();
		hashMap.put("ttbkey", "ttbsusan24901006001");
		hashMap.put("Query", URLEncoder.encode(query, "UTF-8")); //관리자가 입력하는 검색어
		hashMap.put("QueryType", "Title"); //검색어 종류 - Keyword(제목+저자), Title(제목), Author(저자), Publisher(출판사)
		hashMap.put("MaxResults", "10");
		hashMap.put("start", "1");
		hashMap.put("SearchTarget", "All"); //검색대상 - Book, Foreign
		hashMap.put("output", "xml");
		hashMap.put("Version", "20131101");

		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = hashMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String val  = hashMap.get(key);
			sb.append(key).append("=").append(val);
			if (iter.hasNext()) {
				sb.append("&");
			}
		}

		return baseUrl + sb;
	}

	public static String getListUrl(String queryType) throws Exception {
		String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?";

		Map<String,String> hashMap = new LinkedHashMap<String,String>();
		hashMap.put("ttbkey", "ttbsusan24901006001");
		hashMap.put("QueryType", URLEncoder.encode(queryType, "UTF-8")); //입력가능 - ItemNewAll, ItemNewSpecial, ItemEditorChoice, Bestseller, BlogBest
		hashMap.put("SearchTarget", "Book"); //검색대상 - Book, Foreign
		hashMap.put("MaxResults", "10");
		hashMap.put("start", "1");
		hashMap.put("output", "xml");
		hashMap.put("Version", "20131101");

		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = hashMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String val  = hashMap.get(key);
			sb.append(key).append("=").append(val);
			if (iter.hasNext()) {
				sb.append("&");
			}
		}
		return baseUrl + sb;
	}





	public static void main(String[] args) throws Exception {
		String searchWord = "ItemNewAll"; //검색어 입력
		String searchUrl = getSerachUrl(searchWord);
		String listUrl = getListUrl(searchWord);
		AladdinOpenAPIHandler api = new AladdinOpenAPIHandler();
		//api.parseXml(searchUrl);

		api.parseXml(listUrl);
		if (api.Items.isEmpty()) {
			System.out.println("검색 결과가 없습니다.");
		} else {
			for (Item item : api.Items) {
				System.out.println("제목: " + item.Title);
				System.out.println("출판사: " + item.publisher);
				System.out.println("저자: " + item.author);
				System.out.println("설명: " + item.description);
				System.out.println("ISBN13: " + item.isbn13);
				System.out.println("정가: " + item.priceStandard);
				System.out.println("판매가: " + item.priceSales);
				System.out.println("커버 이미지: " + item.cover);
				System.out.println("상품 링크: " + item.Link);
				System.out.println("재고 상태: " + item.stockstatus);
				System.out.println("--------------------------------------------------");
			}

		}



	}
}