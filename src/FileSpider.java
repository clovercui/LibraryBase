import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * �����ļ�
 * ��1�У���������
 * ��2�У���ʼ��ַ����n(int)
 * ��3��-��n+2�У���ʼ��ַ������www.sort=&page=(*)|1|3��(*)Ϊͨ�������1��3
 * ��n+3�У�������m(int)
 * ��n+4��-��m+n+3�У�����02jpg_@@og:image" content=".+?"@@01Intro@@property="og:title" content=".+?"��
 * �����ϣ���һλΪ0��ʾ����ƥ�䣬Ϊ1Ϊѭ��ƥ�䣬�ڶ�λΪ0��ʾ��һ���Url��Ϊ1��ʾ��������Ϣ��Ϊ2��ʾ�ļ����أ�Ϊ3��ʾ��ҳ��
 * �����ϣ���Ϊ�ļ������ڵ�3λ��ʼΪ��׺����'_'������������@@Ϊƥ�����ͣ�ż����@@Ϊƥ�����
 * �����ϣ�ƥ������У�'|'ǰΪ����ʽ����������ֻ��һ��".+?"�������ַ�����ƥ����"[\s\S]+?"����ʾ��Ҫ��ȡ����Ϣ��'|'Ϊƴ�ӱ��ʽ������(*)Ϊ��ȡ����Ϣ
 * @author zhangzhizhi
 *
 */
public class FileSpider {
	private ArrayList<String> WaitingList = new ArrayList<String>(); // �洢δ����URL
	private int totalLevel = 2;
	private String[] rule;
	private String taskSign;
	private boolean[] addFileFlag;
	private static long index = 0;
	/**
	 * @return the index
	 */
	public static synchronized long getIndex() {
		index++;
		return index;
	}

	private String charset = "utf-8"; // ��վ����
	private boolean charsetFlag = false; // �ѻ����վ�����־
	private int runningNum = 1; // �����������е��߳���
	private int intThreadNum = 10; // �߳���
	private String fileDirectory = "myspider"; // �����ļ���·��
	private long begin; // ���濪ʼ��ʱ��
	private String file = "now.txt";

	// ��ȡ�����ļ�
		private boolean readConfigFile() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file)));
				// ��ʼҳ��ַ
				taskSign = br.readLine();
				int M = Integer.parseInt(br.readLine());
				for (int i = 0; i < M; i++) {
					// ����List��
					String startPage = br.readLine();
					if (startPage.contains("(*)")){
						int min = Integer.parseInt(startPage.substring(startPage.indexOf('|')+1, startPage.lastIndexOf('|')));
						int max = Integer.parseInt(startPage.substring(startPage.lastIndexOf('|')+1));
						for (int j = min; j <= max; j++){
							String tmp =  startPage.substring(0,startPage.indexOf('|'));
							tmp = tmp.replace("(*)",""+j);
							WaitingList.add(tmp);
						}
					}
					else {
						WaitingList.add(startPage);
					}
				}
				// ����ͼƬ�ļ��е�ַ
				totalLevel = Integer.parseInt(br.readLine());
				rule = new String[totalLevel];
				addFileFlag = new boolean[totalLevel];
				for (int i = 0; i < totalLevel; i++){
					addFileFlag[i] = true;
					rule[i] = br.readLine();
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
	 * 
	 * @param urlString
	 *            URL��ַ
	 * @return ��ҳ�������
	 */
	private String getUrlContent(String urlString) {
		if (!charsetFlag) {
			getCharset(urlString);
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
	 * 
	 * @param urlString
	 *            URL��ַ
	 * @param filePath
	 *            �����ļ�·��
	 * @return true��ʾ�ɹ���false��ʾʧ��
	 */
	public boolean GetPicture(String urlString, String filePath) {
		try {
			URL url = new URL(urlString);
			URLConnection conn = (URLConnection) url.openConnection();
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(2000);
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
	 * 
	 * @param urlString
	 *            URL��ַ
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
				String tmp = rLine.toLowerCase();
				if (tmp.contains("gb2312")){
					charset = "gb2312";
				}
				else if (tmp.contains("utf-8")){
					charset = "utf-8";
				}
				else if (tmp.contains("gbk")){
					charset = "gbk";
				}
				else {
					continue;
				}
				break;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * �ӵȴ�������ȡ��һ��
	 * 
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
	 * 
	 * @param regString
	 *            ����ʽ
	 * @param content
	 *            ����
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
		String result = content.substring(content.lastIndexOf(firstPart)
				+ firstPart.length(), content.lastIndexOf(lastPart));
		if (seppos != -1) {
			result = regString.substring(seppos + 1).replace("(*)", result);
		}
		return result;
	}

	/**
	 * �õ��ָ���ǰ������ʽ
	 * 
	 * @param regString
	 *            ������������ʽ
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
		readConfigFile();

		setIndex(System.currentTimeMillis());
		System.setProperty("proxySet", "true");
		System.setProperty("http.proxyHost", "10.108.12.6");
		System.setProperty("http.proxyPort", "901");
		// ���̵߳��ô������
		for (int i = 0; i < intThreadNum; i++) {
			new Thread(new ProcessWaitingList()).start();
		}

		// �ж����߳���ֹ����
		while (true) {
			if (WaitingList.isEmpty()
					&& runningNum == 1) {
				System.out.println("Time:"
						+ (System.currentTimeMillis() - begin) / 1000 + "s");
				System.out.println("Finished!");
				break;
			}
		}
	}

	// ����Ϣд��txt�ļ�
	private synchronized boolean add2File(String s, String pathString) {
		try {
			// ���������ļ�Ŀ¼
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(
					pathString), charset);
			w.write(s);
			w.flush();
			w.close();
			return true;
		} catch (Exception e) {
			System.out.println("������Ϣ�ļ�ʧ��!");
			return false;
		}
	}

	private void dealFun(String url, int level, long formerId) {
		if (level >= totalLevel) {
			return;
		}
		String urlContent = getUrlContent(url);
		if (addFileFlag[level]){
			System.out.println(charset);
			add2File(url+urlContent, taskSign+"_"+level+".html");
			addFileFlag[level] = false;
		}
		
		String regex = "@@";
		String msgSplit[] = rule[level].split(regex);
		int num = msgSplit.length;

		long id = getIndex();
		int index = 0;

		String result[] = new String[num / 2];
		for (int i = 0; i < num / 2; i++) {
			result[i] = "";
		}
		for (int i = 0; i < num; i += 2) {
			String realReg = getReg(msgSplit[i + 1]);
			String tmpStr = urlContent;
			Pattern p = Pattern.compile(realReg);
			Matcher m = p.matcher(tmpStr);
			boolean blnp = m.find();
			if (msgSplit[i].charAt(0) == '0') {
				if (blnp) {
					String urlResutl = getResult(msgSplit[i + 1], m.group(0));
					if (msgSplit[i].charAt(1) == '0') {
						dealFun(urlResutl, level + 1, id);
						result[i / 2] = urlResutl;
					} else if (msgSplit[i].charAt(1) == '1') {
						result[i / 2] = urlResutl;
					} else if (msgSplit[i].charAt(1) == '2') {
						int pos = urlResutl.lastIndexOf('.');
						String postfix;
						if (pos < 0 || urlResutl.length() - pos > 5){
							postfix = "."+ msgSplit[i].substring(2,msgSplit[i].indexOf('_'));
						}
						else {
							postfix = urlResutl.substring(pos);
						}
						String filePath = fileDirectory
								+ "\\"
								+ id
								+ "_"
								+ (index++)
								+ postfix;
						GetPicture(urlResutl, filePath);
						result[i / 2] = filePath;
					}
					 else if (msgSplit[i].charAt(1) == '3') {
						 	dealFun(urlResutl, level, id);
							result[i / 2] = urlResutl;
						}
				}
			} else {
				while (blnp) {
					String urlResutl = getResult(msgSplit[i + 1], m.group(0));
					if (msgSplit[i].charAt(1) == '0') {
						dealFun(urlResutl, level + 1, id);
						result[i / 2] += urlResutl + "\r\n";
					} else if (msgSplit[i].charAt(1) == '1') {
						result[i / 2] += urlResutl + "\r\n";
					} else if (msgSplit[i].charAt(1) == '2') {
						int pos = urlResutl.lastIndexOf('.');
						String postfix;
						if (pos < 0 || urlResutl.length() - pos > 5){
							postfix = "."+ msgSplit[i].substring(2,msgSplit[i].indexOf('_'));
						}
						else {
							postfix = urlResutl.substring(pos);
						}
						String filePath = fileDirectory
								+ "\\"
								+ id
								+ "_"
								+ (index++)
								+ postfix;
						GetPicture(urlResutl, filePath);
						result[i / 2] += filePath + "\r\n";
					}
					tmpStr = tmpStr.substring(m.end(), tmpStr.length());
					m = p.matcher(tmpStr);
					blnp = m.find();
				}
			}
		}
	}

	/**
	 * @param index the index to set
	 */
	public static synchronized void setIndex(long index) {
		FileSpider.index = index;
	}

	/**
	 * ����ȴ����е��߳�
	 * 
	 * @author zhangzhizhi
	 * 
	 */
	class ProcessWaitingList implements Runnable {
		public void run() {
			runningNum++;
			while (true) {
				if (WaitingList.isEmpty()) {
					break;
				}
				String url = getWaitingUrl();
				dealFun(url, 0, 0);
			}
			runningNum--;
		}
	}

	public static void main(String[] args) {
		new FileSpider().startSpider();
	}
}
