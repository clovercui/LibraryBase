package spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Test.PicOperator;

import common.CommonArg;

public class Spider {
	private ArrayList<String> WaitingList = new ArrayList<String>(); // �洢δ����URL
	private String charset; 				// ��վ����
	private boolean charsetFlag = false; 	// �ѻ����վ�����־
	private boolean getting; 				// �õ�����ҳ��URL�߳��Ƿ��������б�־��true��ʾ�������У�flase��ʾ�Ѿ����н���
	private int succeedNum = 0; 			// ��ȡ�ɹ���ͼƬ����
	private int failedNum = 0; 				// ��ȡʧ�ܵ�ͼƬ����
	private int sameNum = 0; 				// �ظ���ͼƬ����
	private int runningNum = 1; 			// �����������е��߳���
	private int count = 0;					// �Ѽ����ҳ��Url����
	private String strHomePage; 			// ��ҳ��ַ
	private int intThreadNum; 				// �߳���
	private String fileDirectory; 			// �����ļ���·��
	private boolean AgentFlag = false; 		// �����־
	private String IpAddress; 				// ����IP
	private String Port; 					// ����˿�
	private boolean TimeFlag = false; 		// �������ʱ���־
	private long MaxTime; 					// �����ʱ�䣬��λ���룬��Ϊ1����
	private boolean MaxUrlFlag = false; 	// ���ҳ������־
	private int MaxUrl; 					// ���Url��
	private String regDescription; 			// ���ͼƬ��������ʽ
	private String regPrice; 				// ��ȡ�۸�����ʽ
	private String regPicUrl; 				// ��ȡͼƬ��ַ����ʽ
	private String regPicPageUrl; 			// ��ȡͼƬҳ����ַ����ʽ
	private String regNextPageUrl; 			// ��һҳ����ҳ��ַ
	private String regContentPageUrl; 		// ������ҳ��ַ����ʽ
	private long begin;						// ���濪ʼ��ʱ��
	/**
	 * ��ȡ�����ļ�
	 * @return
	 */
	private boolean readConfigFile() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("file\\spiderConfig.txt")));
			// ��ʼҳ��ַ
			strHomePage = br.readLine();
			// ����ͼƬ�ļ��е�ַ
			fileDirectory = br.readLine();
			new File(fileDirectory).mkdir();
			// ������ҳ��ַ����ʽ
			regContentPageUrl = br.readLine();
			// ��һҳ����ҳ��ַ
			regNextPageUrl = br.readLine();
			// ͼƬҳ����ַ����ʽ
			regPicPageUrl = br.readLine();
			// ͼƬ��ַ����ʽ
			regPicUrl = br.readLine();
			// �۸�����ʽ
			regPrice = br.readLine();
			// ��������ʽ
			regDescription = br.readLine();
			// ��������߳���
			try {
				intThreadNum = Integer.parseInt(br.readLine());
			} catch (Exception e) {
				intThreadNum = 10;
			}
			// �������URL����־
			try {
				MaxUrl = Integer.parseInt(br.readLine());
				MaxUrlFlag = true;
			} catch (Exception e) {
				MaxUrlFlag = false;
			}
			// �����ʱ���־
			try {
				MaxTime = Integer.parseInt(br.readLine()) * 6000;
				TimeFlag = true;
			} catch (Exception e) {
				TimeFlag = false;
			}
			// ���ô����־
			try {
				IpAddress = br.readLine();
				Port = br.readLine();
				AgentFlag = true;
			} catch (Exception e) {
				AgentFlag = false;
			}
			br.close();
			return true;
		} catch (Exception e) {
			System.out.println("��ȡ�����ļ�ʧ��");
			return false;
		}
	}

	/**
	 * �õ�Url��ַ������
	 * @param urlString URL��ַ
	 * @return ��ҳ�������
	 */
	private String getUrlContent(String urlString) {
		if (!charsetFlag) {
			if (!getCharset(urlString)) {
				charset = "utf-8";
			}
			charsetFlag = true;
		}
		try {
			URL url = new URL(urlString);
			URLConnection conn;
			BufferedReader bReader;
			String rLine;
			StringBuffer stringBuffer = new StringBuffer();

			// �õ�Դ��
			conn = url.openConnection();
			conn.setDoOutput(true);
			bReader = new BufferedReader(new InputStreamReader(
					url.openStream(), charset));
			while ((rLine = bReader.readLine()) != null) {
				stringBuffer.append(rLine + "\r\n");
			}

			if (bReader != null) {
				bReader.close();
			}
			return stringBuffer.toString();
		} catch (Exception e) {
			System.out.println("�õ�Urlҳ�����ݳ���");
			return null;
		}
	}

	/**
	 * ����URL��ȡͼƬ
	 * @param urlString URL��ַ
	 * @param filePath �����ļ�·��
	 * @return true��ʾ�ɹ���false��ʾʧ��
	 */
	public boolean GetPicture(String urlString, String filePath) {
		try {
			URL url = new URL(urlString);
			URLConnection conn = (URLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			File file = new File(filePath);
			FileOutputStream out = new FileOutputStream(file);
			int i = 0;
			while ((i = is.read()) != -1) {
				out.write(i);
			}
			is.close();
			out.close();
			return true;
		} catch (Exception e) {
			System.out.println("GetPicture failed!");
			return false;
		}
	}

	/**
	 * �õ���ҳ����
	 * @param urlString URL��ַ
	 * @return true��ʾ�ɹ���false��ʾʧ��
	 */
	private boolean getCharset(String urlString) {
		try {
			URL url = new URL(urlString);
			URLConnection conn;
			conn = url.openConnection();
			conn.setDoOutput(true);
			BufferedReader bReader;
			String rLine;
			bReader = new BufferedReader(
					new InputStreamReader(url.openStream()));
			while ((rLine = bReader.readLine()) != null) {
				Matcher m = Pattern.compile("charset.+?\".+?\"").matcher(rLine);
				if (m.find()) {
					charset = m.group(0).substring(
							m.group(0).indexOf('\"') + 1,
							m.group(0).length() - 1);
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			System.out.println("�õ��������");
			return false;
		}
	}

	/**
	 * �ӵȴ�������ȡ��һ��
	 * @return ȡ������URL��ַ
	 */
	private synchronized String getWaitingUrl() {
		if (WaitingList.isEmpty()) {
			return null;
		}
		String tmpAUrl = WaitingList.get(0);
		WaitingList.remove(0);
		return tmpAUrl;
	}

	/**
	 * ��������ʽ�õ���Ҫ������
	 * @param regString ����ʽ
	 * @param content ����
	 * @return ƥ�������
	 */
	private String getResult(String regString, String content) {
		int endpos = regString.indexOf(".+?"); // ��Ҫ�滻������
		int seppos = regString.indexOf('|'); // ��������ʽ���滻���ʽ
		String firstPart = regString.substring(0, endpos); // ��Ѱ".+?"֮ǰ���ַ���
		int startpos = firstPart.lastIndexOf("+?");
		if (startpos != -1) { // �������"+?"����ȡ"+?"��".+?"֮����ַ���
			firstPart = firstPart.substring(startpos + 2);
		}
		String lastPart;
		if (seppos != -1) {
			lastPart = regString.substring(endpos + 3, seppos);
		} else {
			lastPart = regString.substring(endpos + 3);
		}
		int stoppos = lastPart.indexOf("[\\s\\S]+?");
		if (stoppos != -1){
			lastPart = lastPart.substring(0, stoppos);
		}
		String result = content.substring(content.indexOf(firstPart)
				+ firstPart.length(), content.lastIndexOf(lastPart));
		if (seppos != -1) {
			result = regString.substring(seppos + 1).replace("(*)", result);
		}
		return result;
	}

	/**
	 * �õ��ָ���ǰ������ʽ
	 * @param regString ������������ʽ
	 * @return ����ʽ
	 */
	private String getReg(String regString) {
		int last;
		if (regString.indexOf('|') != -1) {
			last = regString.indexOf('|');
		} else {
			last = regString.length();
		}
		return regString.substring(0, last);
	}

	/**
	 * ���������ڣ����û��ṩ������վ�㿪ʼ������������ҳ�����ץȡ
	 */
	public void startSpider() {
		// ��¼��ʼʱ��
		begin = System.currentTimeMillis();
		CommonArg.setIndex(begin);
		// ��ȡ�����ļ�
		if (!readConfigFile()) {
			return;
		}
		// ���ô���
		if (AgentFlag) {
			System.setProperty("proxySet", "true");
			System.setProperty("http.proxyHost", IpAddress);
			System.setProperty("http.proxyPort", Port);
		}

		// �õ���������ҳ���url
		getting = true;
		new Thread(new GetWaitingList()).start();

		// ���̵߳��ô������
		for (int i = 0; i < intThreadNum; i++) {
			new Thread(new ProcessWaitingList()).start();
		}

		// �ж����߳���ֹ����
		while (true) {
			if (CommonArg.isSpiderStopFlag() ||WaitingList.isEmpty() && runningNum == 1) {
				System.out.println("Succeed:" + succeedNum);
				System.out.println("Failed:" + failedNum);
				System.out.println("Same:" + sameNum);
				System.out.println("Time:"
						+ (System.currentTimeMillis() - begin) / 1000 + "s");
				System.out.println("Finished!");
				CommonArg.setSpiderFlag(false);
				break;
			}
			if (CommonArg.isSpiderStopFlag() || (TimeFlag && System.currentTimeMillis() - begin > MaxTime
					) ) {
				WaitingList.clear();
				TimeFlag = false;
			}
		}
	}

	/**
	 * �����µ���ҳ����ȡ���к��е�������Ϣ��������һҳ����ַ
	 * @param strUrl һ��Url��ַ
	 * @return ��һҳ��һ��Url��ַ
	 */
	public synchronized String readNextPage(String strUrl) {
		boolean pageFlag = false;
		Pattern p;
		Matcher m;

		// �õ�����Ƭ��ҳ��URL
		String urlContent = getUrlContent(strUrl);
		String tmpStr = urlContent;

		p = Pattern
				.compile(getReg(regContentPageUrl), Pattern.CASE_INSENSITIVE);
		m = p.matcher(tmpStr);
		boolean blnp = m.find();
		while (blnp == true) {
			String url = getResult(regContentPageUrl, m.group(0));
			if (MaxUrlFlag && count >= MaxUrl) {
				return null;
			}
			WaitingList.add(url);
			count++;
			tmpStr = tmpStr.substring(m.end(), tmpStr.length());
			m = p.matcher(tmpStr);
			blnp = m.find();
			pageFlag = true;
		}

		if (!pageFlag) {
			return null;
		}
		// �ٴ�����һҳ�����
		p = Pattern.compile(getReg(regNextPageUrl), Pattern.CASE_INSENSITIVE);
		m = p.matcher(urlContent);
		if (m.find()) {
			String url = getResult(regNextPageUrl, m.group(0));
			return url;
		}
		return null;
	}

	/**
	 * �����µ���ҳ����ȡ���к��е�������Ϣ
	 * @param strUrl Url��ַ
	 * @param myOperator ͼƬ��������
	 * @return true��ʾ����ɹ���false��ʾ����ʧ��
	 */
	public boolean readContentPage(String strUrl, PicOperator myOperator) {
		String imageURLString = "null";
		String descriptionString = "null";
		String priceString = "null";
		String tmpStr;
		Pattern p;
		Matcher m;

		String urlContent = getUrlContent(strUrl);
		if (urlContent == null) {
			return false;
		}
		// �õ�����
		p = Pattern.compile(regDescription, Pattern.CASE_INSENSITIVE);
		m = p.matcher(urlContent);
		if (m.find()) {
			descriptionString = getResult(regDescription, m.group(0));
		}

		// �õ��۸�
		p = Pattern.compile(regPrice, Pattern.CASE_INSENSITIVE);
		m = p.matcher(urlContent);
		if (m.find()) {
			priceString = getResult(regPrice, m.group(0));
		}

		String picName;
		if (regPicPageUrl.equals("true")) {
			tmpStr = urlContent;
		} else {
			p = Pattern
					.compile(getReg(regPicPageUrl), Pattern.CASE_INSENSITIVE);
			m = p.matcher(urlContent);
			if (m.find()) {
				String url = getResult(regPicPageUrl, m.group(0));
				tmpStr = getUrlContent(url);
			} else {
				System.out.println("ͼƬ��ҳҳ��Url����");
				return false;
			}
		}
		// �õ�ͼƬURL
		p = Pattern.compile(getReg(regPicUrl), Pattern.CASE_INSENSITIVE);
		m = p.matcher(tmpStr);
		boolean blnp = m.find();
		while (blnp == true) {
			imageURLString = getResult(regPicUrl, m.group(0));
			long timestamp = CommonArg.getIndex();
			picName = timestamp
					+ imageURLString.substring(imageURLString.lastIndexOf('.'));
			// ����õ�ͼƬʧ��
			if (!GetPicture(imageURLString, fileDirectory + picName)) {
				failedNum++;
			}
			// ����õ�ͼƬ�ɹ�
			else {
				String imagePath = fileDirectory + picName;
				String message = descriptionString + "@@" + priceString + "@@"
						+ strUrl;
				if (succeedNum == 200){
					System.out.println((System.currentTimeMillis()-begin)+":"+succeedNum);
				}
				succeedNum++;
			}
			tmpStr = tmpStr.substring(m.end());
			m = p.matcher(tmpStr);
			blnp = m.find();
		}
		return true;
	}

	/**
	 * ����ȴ����е��߳�
	 * @author zhangzhizhi
	 *
	 */
	class ProcessWaitingList implements Runnable {
		public void run() {
			runningNum++;
			PicOperator myOperator = new PicOperator();
			while (true) {
				if (WaitingList.isEmpty()) {
					if (!getting || CommonArg.isSpiderStopFlag()) {
						break;
					} else {
						try {
							Thread.sleep(200);
							continue;
						} catch (InterruptedException e) {
						}
					}
				}
				String url = getWaitingUrl();
				if (url == null) {
					continue;
				}
				readContentPage(url, myOperator);
			}
			runningNum--;
		}
	}


	/**
	 * ��ȡҳ��URL�߳�
	 * @author zhangzhizhi
	 *
	 */
	class GetWaitingList implements Runnable {
		public void run() {
			String nextUrlString = strHomePage;
			while (true) {
				nextUrlString = readNextPage(nextUrlString);
				if (nextUrlString == null || CommonArg.isSpiderStopFlag()) {
					break;
				}
				if (WaitingList.size() > 100) {
					try {
						Thread.sleep(1000);
						continue;
					} catch (InterruptedException e) {
					}
				}
			}
			getting = false;
		}
	}
	public static void main(String[] args) {
		new Spider().startSpider();
	}
}
