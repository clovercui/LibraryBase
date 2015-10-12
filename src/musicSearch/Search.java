package musicSearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * �����ļ� ��1�У��������� ��2�У���ʼ��ַ����n(int)
 * ��3��-��n+2�У���ʼ��ַ������www.sort=&page=(*)|1|3��(*)Ϊͨ�������1��3 ��n+3�У�������m(int)
 * ��n+4��-��m+n+3�У�����02jpg_@@og:image" content=".+?"@@01Intro@@property="og:title
 * " content=".+?"��
 * �����ϣ���һλΪ0��ʾ����ƥ�䣬Ϊ1Ϊѭ��ƥ�䣬�ڶ�λΪ0��ʾ��һ���Url��Ϊ1��ʾ��������Ϣ��Ϊ2��ʾ�ļ����أ�Ϊ3��ʾ��ҳ��
 * �����ϣ���Ϊ�ļ������ڵ�3λ��ʼΪ��׺����'_'������������@@Ϊƥ�����ͣ�ż����@@Ϊƥ�����
 * �����ϣ�ƥ������У�'|'ǰΪ����ʽ����������ֻ��һ��".+?"
 * �������ַ�����ƥ����"[\s\S]+?"����ʾ��Ҫ��ȡ����Ϣ��'|'Ϊƴ�ӱ��ʽ������(*)Ϊ��ȡ����Ϣ
 * 
 * @author zhangzhizhi
 * 
 */
public class Search {
	private String charset = "utf-8"; // ��վ����
	private boolean charsetFlag = false; // �ѻ����վ�����־
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
				if (tmp.contains("gb2312")) {
					charset = "gb2312";
				} else if (tmp.contains("utf-8")) {
					charset = "utf-8";
				} else if (tmp.contains("gbk")) {
					charset = "gbk";
				} else {
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
		if (stoppos != -1) {
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
	public boolean searchIrc(String filePath) {
		String dir = filePath.substring(0,filePath.lastIndexOf('.'));
		String name = filePath.substring(filePath.lastIndexOf('\\')+1, filePath.lastIndexOf('.'));
		String url = "";
		try {
			url = "http://www.cnlyric.com/search.php?k="+URLEncoder.encode(name.replaceAll("��.+?��", "").replaceAll("\\(.+?\\)", ""), "GB2312")+"&t=s";
		} catch (UnsupportedEncodingException e) {
			return false;
		}
		String reg = "<a href=\".+?\" class=\"ld\"|http://www.cnlyric.com/(*)";
		String urlContent = getUrlContent(url);
		Pattern p = Pattern.compile(getReg(reg));
		Matcher m = p.matcher(urlContent);
		if (m.find()){
			if (!GetPicture(getResult(reg, m.group(0)), dir+".lrc")){
				return false;
			}
			else {
				return true;
			}
		}
		else {
			return false;
		}
	}

	public static void main(String[] args) {
		new Search().searchIrc("C:\\Users\\zhangzhizhi\\Music\\BEYOND - �������.mp3");
	}
}
