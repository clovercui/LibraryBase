package webRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class yanhui2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String url = "http://biz.finance.sina.com.cn/suggest/lookup_n.php?q=600018&country=cn";
			String cookie = "";
			String data = "test!!!";

			HttpURLConnection urlConn = (HttpURLConnection) (new URL(url)
					.openConnection());
			urlConn.addRequestProperty("Cookie", cookie);
			urlConn.setRequestMethod("GET");
			urlConn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
			urlConn.setFollowRedirects(true);
			urlConn.setDoOutput(true); // ��Ҫ�������д����
			urlConn.setDoInput(true); //
			urlConn.setUseCaches(false); // ��÷��������µ���Ϣ
			urlConn.setAllowUserInteraction(false);
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			urlConn.setRequestProperty("Accept-Language", "zh-cn");
			// urlConn.setRequestProperty("Content-Length", ""+data.length());
			urlConn.setRequestProperty("Cache-Control", "no-cache");
			urlConn.setRequestProperty("Pragma", "no-cache");
			urlConn.setRequestProperty("Host", "biz.finance.sina.com.cn");
			urlConn.setRequestProperty("Accept",
					"text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
			urlConn.setRequestProperty("Connection", "keep-alive");

			cookie = urlConn.getHeaderField("Set-Cookie");
			// BufferedReader br=new BufferedReader(new
			// InputStreamReader(urlConn.getInputStream(),"utf-8"));
			BufferedInputStream br = new BufferedInputStream(
					urlConn.getInputStream());

			FileOutputStream out = new FileOutputStream(new File("C:\\out.html"));

			int chByte = br.read();
			while (chByte != -1) {
				out.write(chByte);
				chByte = br.read();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
