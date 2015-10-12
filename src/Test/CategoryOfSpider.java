package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.db_Operator;

public class CategoryOfSpider {
	private String charset = "utf-8"; 				// ��վ����
	private String regDescription; 			// ���ͼƬ��������ʽ
	db_Operator myoperator2 = new db_Operator("sqlserver", "Shoes20130708", "sa",
			"20091743");
	private int man = 0;
	private int woman = 0;
	private int other = 0;

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
		String result = content.substring(content.lastIndexOf(firstPart)
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
		db_Operator myoperator = new db_Operator("sqlserver", "Shoes20130708", "sa",
				"20091743");
		try {
			ResultSet rs=myoperator.db_Query("select Id,Url from ImageInfo where CategoryName is null");
			int num = 0;
			while(rs.next()){
				long id = rs.getLong(1);
				String url = rs.getString(2);
				readContentPage(url, id);
				if (num%100==0){
					System.out.println(num);
				}
				num++;
			}
			myoperator.db_Close();
			myoperator2.db_Close();
			System.out.println("man:"+man);
			System.out.println("woman:"+woman);
			System.out.println("other:"+other);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String getUrlContent(String urlString) {
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
	 * �����µ���ҳ����ȡ���к��е�������Ϣ
	 * @param strUrl Url��ַ
	 * @param myOperator ͼƬ��������
	 * @return true��ʾ����ɹ���false��ʾ����ʧ��
	 */
	public boolean readContentPage(String strUrl,long id) {
		String categoryName = "null";
		Pattern p;
		Matcher m;

		String urlContent = getUrlContent(strUrl);
//		add2File(urlContent,"c:\\1.html");
		if (urlContent == null) {
			return false;
		}
		// �õ�����
		regDescription = "��Ь</a>&gt;";
		p = Pattern.compile(regDescription, Pattern.CASE_INSENSITIVE);
		m = p.matcher(urlContent);
		if (m.find()) {
			urlContent = urlContent.substring(m.end(), urlContent.length());
			regDescription = "'>.+?</a>&gt;";
			p = Pattern.compile(regDescription, Pattern.CASE_INSENSITIVE);
			m = p.matcher(urlContent);
			if (m.find()) {
				categoryName = getResult(regDescription, m.group(0));
//				System.out.println("��"+categoryName);
				myoperator2.db_Execute("update ImageInfo set CategoryName='��"+categoryName+"' where Id ="+id);
			}
			else {
//				System.out.println("��Ьδ�ҵ����� "+strUrl);
				myoperator2.db_Execute("update ImageInfo set CategoryName='��Ь' where Id ="+id);
			}
		}
		else{
			regDescription = "ŮЬ</a>&gt;";
			p = Pattern.compile(regDescription, Pattern.CASE_INSENSITIVE);
			m = p.matcher(urlContent);
			if (m.find()) {
				urlContent = urlContent.substring(m.end(), urlContent.length());
				regDescription = "'>.+?</a>&gt;";
				p = Pattern.compile(regDescription, Pattern.CASE_INSENSITIVE);
				m = p.matcher(urlContent);
				if (m.find()) {
					categoryName = getResult(regDescription, m.group(0));
//					System.out.println("Ů"+categoryName);
					myoperator2.db_Execute("update ImageInfo set CategoryName='Ů"+categoryName+"' where Id ="+id);
				}
				else {
//					System.out.println("ŮЬδ�ҵ����� "+strUrl);
					myoperator2.db_Execute("update ImageInfo set CategoryName='ŮЬ' where Id ="+id);
				}
			}
			else {
//				System.out.println("����ЬŮЬ "+strUrl);
				
				regDescription = "�˶�</a>&gt;";
				p = Pattern.compile(regDescription, Pattern.CASE_INSENSITIVE);
				m = p.matcher(urlContent);
				if (m.find()) {
					urlContent = urlContent.substring(m.end(), urlContent.length());
					regDescription = "'>.+?</a>&gt;";
					p = Pattern.compile(regDescription, Pattern.CASE_INSENSITIVE);
					m = p.matcher(urlContent);
					if (m.find()) {
						categoryName = getResult(regDescription, m.group(0));
//						System.out.println("Ů"+categoryName);
						myoperator2.db_Execute("update ImageInfo set CategoryName='�˶�"+categoryName+"' where Id ="+id);
					}
					else {
						myoperator2.db_Execute("update ImageInfo set CategoryName='�˶�Ь' where Id ="+id);
					}
				}
				else {
					other++;
				}
			}
		}
		return true;
	}
	
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

	public static void main(String[] args) {
		new CategoryOfSpider().startSpider();
	}
}
