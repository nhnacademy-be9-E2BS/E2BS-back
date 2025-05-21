package com.nhnacademy.back.product.product.park.API;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AladdinOpenAPI {
	String query;
	String queryType;
	public AladdinOpenAPI(String query, String queryType) {
		this.query = query;
		this.queryType = queryType;
	}

	public String getSerachUrl(String query, String queryType) throws Exception {
		String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?";

		Map<String,String> hashMap = new LinkedHashMap<String,String>();
		hashMap.put("ttbkey", "ttbsusan24901006001");
		hashMap.put("Query", URLEncoder.encode(query, "UTF-8")); //관리자가 입력하는 검색어
		hashMap.put("QueryType", queryType); //검색어 종류 - Keyword(제목+저자), Title(제목), Author(저자), Publisher(출판사)
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

	public List<Item> searchBooks() throws Exception {
		String searchUrl = getSerachUrl(this.query, this.queryType);
		AladdinOpenAPIHandler api = new AladdinOpenAPIHandler();
		api.parseXml(searchUrl);
		if (api.Items.isEmpty()) {
			return null;
		} else {
			List<Item> items = new ArrayList<Item>(api.Items);
			return items;
		}
	}

	/*
	public static void main(String[] args) throws Exception {

		AladdinOpenAPIHandler api = new AladdinOpenAPIHandler();
		String searchUrl = getSerachUrl("aladdin", "Keyword");
		api.parseXml(searchUrl);
		List<Item> items = searchBooks("aladdin", "Keyword");
		System.out.println(items.size());

		for (Item item : items) {
			System.out.println(item.author);
		}
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

	 */
}