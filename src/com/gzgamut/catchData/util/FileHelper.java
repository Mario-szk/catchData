package com.gzgamut.catchData.util;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class FileHelper {

	private static Logger logger = Logger.getLogger(FileHelper.class);
	public static int count = 1;

	/**
	 * 读取该目录下所有的文件并返回一个所有绝对地址的Map
	 * 
	 * @param filepath
	 * @return Map<String, String>
	 */
	public static Map<String, String> readfile(String filepath) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		File file = new File(filepath);
		if (!file.isDirectory()) {
			logger.info("文件 " + count++);
			logger.info("name=" + file.getName());
			logger.info("absolutepath=" + file.getAbsolutePath());
			map.put(file.getName(), file.getAbsolutePath());
		} else if (file.isDirectory()) {
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++) {
				map.putAll(readfile(filepath + "/" + filelist[i]));
			}
		}
		return map;
	}

	/**
	 * 判断文件夹目录下是否存在文件
	 * 
	 * @param filepath
	 * @return
	 */
	public static boolean hasFiles(String filepath) {
		File file = new File(filepath);
		File[] fileList = file.listFiles();
		if (fileList.length == 0) {
			return false;
		} else {
			return true;
		}
	}
}
