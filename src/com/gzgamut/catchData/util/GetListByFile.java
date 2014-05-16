package com.gzgamut.catchData.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取文件列表
 * 
 * @author Administrator
 * 
 */
public class GetListByFile {

	/**
	 * 从symbols.txt中读取股票的编号
	 * 
	 * @return
	 */
	public static List<String> getSymbols() {
		List<String> list = new ArrayList<String>();
		File file = new File("src/file/symbols.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String result = null;
			// 限制股票代码在本次实验中的数量
			int count = 1;
			while ((result = reader.readLine()) != null) {
				if (!result.equals("")) {
					list.add(result);
					count++;
				}
				// if (count > 3) {
				// break;
				// }
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 获取与某股票代码相关的所有文件
	 * 
	 * @param symbol
	 * @return
	 */
	public static List<String> getFile(String symbol) {
		List<String> list = new ArrayList<String>();
		File file = new File(Config.path + "/" + symbol);
		if (file.exists() && file.isDirectory()) {
			File[] cFile = file.listFiles();
			for (File subFile : cFile) {
				String subFileAbsolutePath = subFile.getAbsolutePath();
				if (!subFile.isDirectory()
						&& subFileAbsolutePath.indexOf("PDF") > 0) {
					list.add(subFileAbsolutePath);
				}
			}
		}
		return list;
	}
}
