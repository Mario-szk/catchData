package com.gzgamut.catchData.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 根据股票代码生成网页请求的url
 * 
 * @author Administrator
 * 
 */
public class GetURL {

	/**
	 * 以股票代码为key组织与该股票代码相关的PDF文档列表的url请求信息
	 * 
	 * @param stockCodes
	 * @return
	 */
	public static Map<String, List<String>> getUrl(List<String> stockCodes) {
		Map<String, List<String>> map = new TreeMap<String, List<String>>();
		for (String stockCode : stockCodes) {
			if (stockCode != null) {
				stockCode = stockCode.substring(0, stockCode.lastIndexOf("."));
				List<String> list = new ArrayList<String>();
				String parameter = "&stockCode=" + stockCode + "&pageNo=";
				for (int i = 1; i <= 2; i++) {
					String url = Config.httpUrl + parameter + i;
					list.add(url);
				}
				map.put(stockCode, list);
			}
		}
		return map;
	}
}
