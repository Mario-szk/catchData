package com.gzgamut.catchData.data;

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.gzgamut.catchData.util.GetListByFile;
import com.gzgamut.catchData.util.StringHelper;

public class MainFile {

	private Logger logger = Logger.getLogger(GetPDF.class);

	/**
	 * 执行文件下载的操作
	 */
	public void downloadFile() {

		GetPDF p = new GetPDF();
		SwitchFile sf = new SwitchFile();
		List<String> symbols = GetListByFile.getSymbols();

		// 第1步开始的时间
		long startTime1 = new Date().getTime();
		logger.info("第1步：下载所有的PDF文件。");
		p.getPDF(symbols);
		logger.info("第1步：下载所有的PDF文件完成！");
		logger.warn("第1步总共耗费了" + StringHelper.timer(startTime1));

		// 第2步开始的时间
		long startTime2 = new Date().getTime();
		logger.info("第2步：把PDF文档的内容转换为txt格式存储。");
		sf.analyzeAllFile(symbols);
		logger.info("第2步：把PDF文档的内容转换为txt格式存储完成！");
		logger.warn("第2步总共耗费了" + StringHelper.timer(startTime2));

		// 第3步开始的时间
		long startTime3 = new Date().getTime();
		logger.info("第3步：从TXT中提取出研发费用和广告费用，存储在excel表格当中。");
		GetData.collectData();
		logger.info("第3步：从TXT中提取出研发费用和广告费用，存储在excel表格当中完成！");
		logger.warn("第3步总共耗费了" + StringHelper.timer(startTime3));

		logger.warn("一共耗费了" + StringHelper.timer(startTime1));
	}

	public static void main(String[] args) {
		MainFile download = new MainFile();
		download.downloadFile();
	}
}
