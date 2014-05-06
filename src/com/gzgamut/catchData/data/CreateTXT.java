package com.gzgamut.catchData.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * 创建文本文档txt
 * 
 * @author Administrator
 * 
 */
public class CreateTXT {

	/**
	 * 创建文本文档txt
	 * 
	 * @param resource
	 *            文本的内容
	 * @param path
	 *            文本存放的路径
	 */
	public void createFile(String resource, String path) {
		OutputStream file = null;
		BufferedWriter writer = null;
		try {
			file = new FileOutputStream(path);
			writer = new BufferedWriter(new OutputStreamWriter(file));
			writer.write(resource);
			writer.flush();
		} catch (Exception e) {
			System.out.println("生成文件时发生错误！");
		} finally {
			try {
				if (file != null) {
					file.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取TXT文件的内容
	 * 
	 * @param url
	 * @return
	 */
	public static String getTxtContent(String url) {

		File file = new File(url);

		InputStream inputStream = null;
		InputStreamReader inputReader = null;
		BufferedReader bufferReader = null;
		try {
			inputStream = new FileInputStream(file);
			inputReader = new InputStreamReader(inputStream);
			bufferReader = new BufferedReader(inputReader);
			// 读取一行
			String line = null;
			StringBuffer strBuffer = new StringBuffer();
			while ((line = bufferReader.readLine()) != null) {
				strBuffer.append(line + "\n");
			}
			String result = strBuffer.toString();
			return result;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		} finally {
			try {
				if (bufferReader != null) {
					bufferReader.close();
				}
				if (inputReader != null) {
					inputReader.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
