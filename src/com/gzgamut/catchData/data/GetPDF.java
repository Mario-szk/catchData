package com.gzgamut.catchData.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import com.gzgamut.catchData.util.Config;
import com.gzgamut.catchData.util.GetURL;
import com.gzgamut.catchData.util.MergeList;

/**
 * 获取网络上pdf信息
 * 
 * @author Administrator
 * 
 */
public class GetPDF {

	private Logger logger = Logger.getLogger(GetPDF.class);

	/**
	 * 根据股票的代码下载相应的PDF文件
	 * 
	 * @param symbols
	 */
	public void getPDF(List<String> symbols) {
		logger.info("第1.1步：获取所有股票代码PDF文档列表的url请求信息");
		Map<String, List<String>> map = GetURL.getUrl(symbols);

		logger.info("第1.2步：获取所有股票代码PDF文档列表的url请求信息并存入url.txt中");
		writeAllToUrlTxt(map);

		logger.info("第1.3步：根据相应的Url.txt文件下载所有的PDF文件。");
		downloadAllPdf();
	}

	/**
	 * 获取某页面所有PDF文档URL信息
	 * 
	 * @param resource
	 *            PDF文档列表的url请求地址
	 * @return
	 */
	private List<String> getPDFUrl(String resource) {

		List<String> list = new ArrayList<String>();
		URL url = null;
		try {
			url = new URL(resource);
		} catch (MalformedURLException e1) {
			logger.info(e1);
		}
		HttpURLConnection conn = null;
		Parser pas = null;
		NodeList tList = null;
		NodeFilter tFilter = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			conn.setConnectTimeout(100 * 1000);

			// 如果连接成功，则对html页面中的PDF列表进行分析和信息提取
			if (conn.getResponseMessage().compareTo("OK") == 0) {
				pas = new Parser(conn);
				tFilter = new NodeClassFilter(TableTag.class);
				tList = pas.extractAllNodesThatMatch(tFilter);
				TableTag table = (TableTag) tList.elementAt(4);
				TableRow[] rows = table.getRows();
				for (TableRow row : rows) {
					TableColumn[] cls = row.getColumns();
					if (!(cls[0].getChild(0).toPlainTextString().indexOf("摘要") > 0)
							&& !(cls[0].getChild(0).toPlainTextString()
									.indexOf("（已取消）") > 0)
							&& !(cls[0].getChild(0).toPlainTextString()
									.indexOf("补充") > 0)
							&& !(cls[0].getChild(0).toPlainTextString()
									.indexOf("英文版") > 0)) {
						String td = cls[0].getStringText();
						if (td.indexOf("\"") != -1) {
							String pdfUrl = Config.index
									+ td.substring(td.indexOf("\"") + 1,
											td.lastIndexOf("?"));
							list.add(pdfUrl);
						}
					}
				}
			}
			// conn.disconnect();
		} catch (ParserException e) {
			logger.info(e);
		} catch (IOException e) {
			logger.info(e);
			logger.warn("超时，重试。");
			return getPDFUrl(resource);
		}
		return list;
	}

	/**
	 * 下载所有的PDF文件
	 */
	public void downloadAllPdf() {
		DownloadPDF download = new DownloadPDF();
		String root = Config.path;
		File rootFile = new File(root);
		if (rootFile.exists() && rootFile.isDirectory()) {
			File[] cFile = rootFile.listFiles();
			for (File subFile : cFile) {
				if (subFile.isDirectory()) {
					FileInputStream fis = null;
					BufferedReader br = null;
					try {
						fis = new FileInputStream(subFile.getAbsolutePath()
								+ "/" + subFile.getName() + "Url.txt");
						br = new BufferedReader(new InputStreamReader(fis,
								"gb2312"));
						String tempbf;
						while ((tempbf = br.readLine()) != null) {
							File tempFile = new File(tempbf);
							String fileName = tempFile.getName();
							File urlsfile = new File(subFile.getAbsolutePath()
									+ "/" + fileName);
							String urlsfileAbsolutePath = urlsfile
									.getAbsolutePath();
							// 如果该PDF文档已经存在，则跳过下载的过程。
							if (urlsfile.exists()) {
								logger.info("文件" + urlsfileAbsolutePath
										+ "已经存在，跳过。");
								continue;
							} else {
								logger.info("文件" + urlsfileAbsolutePath
										+ "不存在，开始制作。");
								download.download2(tempbf,
										urlsfile.getAbsolutePath());
								logger.info("文件" + urlsfileAbsolutePath
										+ "制作完成！");
							}
						}
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (fis != null) {
								fis.close();
							}
							if (br != null) {
								br.close();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * 根据map把所有股票代码PDF文档的所有url信息分别写入各自的url.txt当中
	 * 
	 * @param map
	 */
	private void writeAllToUrlTxt(Map<String, List<String>> map) {
		if (map != null) {
			Set<String> keySet = map.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String key = it.next();
				String urlTxt = Config.path + "/" + key + "/" + key + "Url.txt";
				File file = new File(urlTxt);
				String fileAbsolutePath = file.getAbsolutePath();
				if (file.exists()) {
					logger.info("文件" + fileAbsolutePath + "已经存在，跳过。");
					continue;
				} else {
					logger.info("文件" + fileAbsolutePath + "不存在，开始制作。");
					List<String> url = map.get(key);
					List<String> list0 = new ArrayList<String>();
					for (String resouce : url) {
						List<String> list = getPDFUrl(resouce);
						list0 = MergeList.mergeList(list0, list);
					}
					writeToUrlTxt(key, list0);
					logger.info("文件" + fileAbsolutePath + "制作完成！");
				}
			}
		}
	}

	/**
	 * 把某个股票代码PDF文档的所有url信息写入到url.txt当中
	 * 
	 * @param symbol
	 *            为股票代码
	 * @param urlList
	 *            为url信息List
	 */
	private void writeToUrlTxt(String symbol, List<String> urlList) {

		if (urlList.size() > 0) {
			String filePath = Config.path + "/" + symbol;
			String urlTxt = Config.path + "/" + symbol + "/" + symbol
					+ "Url.txt";
			File file = new File(filePath);
			// 检查是否存在该目录。如果不存在，则创建文件夹。
			if (!file.exists()) {
				file.mkdirs();
			}
			FileOutputStream urlFile = null;
			BufferedWriter writer = null;
			try {
				urlFile = new FileOutputStream(urlTxt, true);
				writer = new BufferedWriter(new OutputStreamWriter(urlFile));
				for (String url : urlList) {
					writer.write(url);
					writer.newLine();
				}
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (urlFile != null) {
						urlFile.close();
					}
					if (writer != null) {
						writer.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
