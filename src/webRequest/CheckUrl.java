package webRequest;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class CheckUrl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.setProperty("proxySet", "true");
			System.setProperty("http.proxyHost", "10.108.13.240");
			System.setProperty("http.proxyPort", "8080");
			
			String urlString = "http://www.renrendai.com/lend/lendPage.action";
			String charset = "utf-8";
			String filePath = "C:\\test.html";
			URL url = new URL(urlString);
			URLConnection conn;
			BufferedReader bReader;
			String rLine;
			StringBuffer stringBuffer = new StringBuffer();

			// �õ�Դ��
			conn = url.openConnection();
			conn.setDoOutput(true);
			
//			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "8859_1");     
//	        out.write("wd=hello"); //��ҳ�洫�����ݡ�post�Ĺؼ����ڣ�      
//	        out.flush();     
//	        out.close();
			
			
			bReader = new BufferedReader(new InputStreamReader(
					url.openStream(), charset));
			while ((rLine = bReader.readLine()) != null) {
				stringBuffer.append(rLine + "\r\n");
			}

			if (bReader != null) {
				bReader.close();
			}
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(
					filePath), charset);
			w.write(stringBuffer.toString());
			w.flush();
			w.close();
		} catch (Exception e) {
			System.out.println("�õ�Urlҳ�����ݳ���");
		}
	}

}
