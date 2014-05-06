package com.gzgamut.catchData.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 合并集合
 * 
 * @author Administrator
 * 
 */
public class MergeList {
	
	/**
	 * 合并两个List<String>
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static List<String> mergeList(List<String> list1, List<String> list2) {
		List<String> list = new ArrayList<String>();
		list.addAll(list1);
		for(String url2: list2){
			int flag = 0;
			for(String url1: list1){
				if(url1.equals(url2)){
					flag = 1;
				}
			}
			if(flag == 0){
				list.add(url2);
			}
		}
		return list;
	}
}
