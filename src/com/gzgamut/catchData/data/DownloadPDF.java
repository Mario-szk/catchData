package com.gzgamut.catchData.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;
import com.gzgamut.catchData.util.Config;

/**
 * 下载pdf文件
 * 
 * @author Administrator
 * 
 */
public class DownloadPDF {

	private Logger logger = Logger.getLogger(DownloadPDF.class);

	/**
	 * 初始化检查目录下是否存在文件夹，如果没有，则创建文件夹
	 */
	public DownloadPDF() {
		File file = new File(Config.path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 根据股票代码及相应的URL下载PDF文档，保存为txt格式
	 * 
	 * @param symbol
	 * @param str
	 */
	public void download(String symbol, String str) {

		File file = new File(str);
		String fileName = file.getName();
		logger.info("begin download file " + fileName);

		String filePath = Config.path + "/" + symbol + "/" + fileName;
		String index = Config.path + "/" + symbol + "/" + symbol + ".txt";
		file = new File(Config.path + "/" + symbol);
		if (!file.exists()) {
			file.mkdirs();
		}
		URL url = null;
		try {
			url = new URL(str);
		} catch (MalformedURLException e) {
			logger.info(e);
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			InputStream inStream = conn.getInputStream();
			FileOutputStream fs = new FileOutputStream(filePath);
			FileOutputStream indexFile = new FileOutputStream(index, true);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					indexFile));
			writer.write(fileName);
			writer.newLine();
			writer.flush();
			byte[] buffer = new byte[1204];
			int byteread = 0;
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();
			indexFile.close();
			writer.close();
		} catch (IOException e) {
			logger.info(e);
		}
		logger.info("end download file " + fileName);
	}

	public void download2(String urlString, String path) {

		File file = new File(urlString);
		String fileName = file.getName();
		logger.info("开始下载文件：" + fileName);

		String filePath = path;
		HttpURLConnection conn = null;
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			URL url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			inStream = conn.getInputStream();
			fs = new FileOutputStream(filePath);
			byte[] buffer = new byte[1024];
			int byteread = 0;
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
		} catch (IOException e) {
			logger.info(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (fs != null) {
					fs.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("结束下载文件：" + fileName);
	}
}
