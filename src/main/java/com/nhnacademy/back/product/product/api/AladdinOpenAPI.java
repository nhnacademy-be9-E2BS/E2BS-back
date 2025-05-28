package com.nhnacademy.back.product.product.api;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AladdinOpenAPI {
	String query;
	String queryType;

	static AladdinOpenAPIHandler api = new AladdinOpenAPIHandler();

	public AladdinOpenAPI(String query, String queryType) {
		this.query = query;
		this.queryType = queryType;
	}

	public AladdinOpenAPI(String queryType) {
		this.queryType = queryType;
	}

	public String getSerachUrl(String query, String queryType) throws Exception {
		String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?";

		Map<String,String> hashMap = new LinkedHashMap<String,String>();
		hashMap.put("ttbkey", "ttbsusan24901006001");
		hashMap.put("Query", URLEncoder.encode(query, "UTF-8")); //관리자가 입력하는 검색어
		hashMap.put("QueryType", queryType); //검색어 종류 - Keyword(제목+저자), Title(제목), Author(저자), Publisher(출판사)
		hashMap.put("MaxResults", "100");
		hashMap.put("start", "1");
		hashMap.put("cover", "Big");
		hashMap.put("SearchTarget", "All"); //검색대상 - Book, Foreign
		hashMap.put("output", "xml");
		hashMap.put("cover", "Big");
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

	public String getListUrl(String queryType) throws Exception {
		String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?";

		Map<String,String> hashMap = new LinkedHashMap<String,String>();
		hashMap.put("ttbkey", "ttbsusan24901006001");
		hashMap.put("QueryType", queryType); //검색어 종류 - ItemNewAll, ItemNewSpecial, ItemEditorChoice, Bestseller, BlogBest
		hashMap.put("MaxResults", "100");
		hashMap.put("start", "1");
		hashMap.put("SearchTarget", "Book");
		hashMap.put("cover", "Big");
		hashMap.put("output", "xml");
		hashMap.put("cover", "Big");
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
		api.parseXml(searchUrl);
		if (api.getItems().isEmpty()) {
			return null;
		} else {
			List<Item> items = new ArrayList<>(api.getItems());
			return items;
		}
	}

	public List<Item> getListBooks() throws Exception {
		String listUrl = getListUrl(this.queryType);
		api.parseXml(listUrl);
		if (api.getItems().isEmpty()) {
			return null;
		} else {
			List<Item> items = new ArrayList<>(api.getItems());
			return items;
		}

	}
}