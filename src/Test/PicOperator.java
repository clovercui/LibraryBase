package Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import common.db_Operator;

public class PicOperator {
//	private db_Operator dbcon;
	private String dbName;
	private String dbUsr;
	private String dbPwd;

	// ���캯�����������ݿ�
	public PicOperator() {
		try {
			FileInputStream fis = new FileInputStream("serverconfig.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fis, "utf-8"));
			br.readLine();
			dbName = br.readLine();
			dbUsr = br.readLine();
			dbPwd = br.readLine();
			fis.close();
			br.close();
		} catch (Exception e) {
			System.out.println("�����ļ�����");
		}
		
	}

	// ʵ��ͼƬ��Ԥ�����ָ����ȡ����
	public double[][] picPreDeal(String imagePath, String imageBinary) {
		double[][] result = new double[3][88];
		for (int i = 0; i < 3; i++){
			for (int j = 0;j < 88; j++){
				result[i][j] = 1.1;
			}
		}
		return result;
	}

	// ����·������Ϣ������ʵ�����ݿ�ĵ���
	public boolean picImportMsg(String imagePath, String imageBinary,
			String msgString, double[][] feature) {
		String sql_str;
		String regex = "@@";
		String[] msg = { "null", "0", "null", "null" };
		String msgSplit[] = msgString.split(regex);
		String picname;
		ResultSet rs; // ��¼��
		int imageId;
		int binaryId;
		try {
			picname = imagePath.substring(imagePath.lastIndexOf('\\') + 1,
					imagePath.length());
			System.arraycopy(msgSplit, 0, msg, 0, msgSplit.length);
			sql_str = "insert into ImageInfo(Name,Path,UploadTime,Introduction,"
					+ "Price,Url,Supplier,ClassifyFlag,"
					+ "InfoCheckFlag,AutoSegFlag,HandSegFlag,FeatureFlag,IndexFlag) "
					+ "values ('"
					+ picname
					+ "','"
					+ imagePath
					+ "',CONVERT(varchar(100), GETDATE(), 20),'"
					+ msg[0]
					+ "',"
					+ msg[1]
					+ ",'"
					+ msg[2]
					+ "','"
					+ msg[3]
					+ "',0,0,1,0,1,0)";
			// ����ͼƬ��Ϣ��
			db_Operator dbcon = new db_Operator("sqlserver", dbName, dbUsr, dbPwd);
			if (!dbcon.db_Execute(sql_str)) {
				System.out.println("����ͼƬ��SQL������");
				return false;
			}
			dbcon.db_Close();
		} catch (Exception e) {
			System.out.println("����ͼƬ��������Ϣ����");
			return false;
		}
//		// ����ֵͼ��Ϣ��ͼƬ��ϢId�������ݿ��ֵͼ ��
		try {
			db_Operator dbcon = new db_Operator("sqlserver", dbName, dbUsr, dbPwd);
			rs = dbcon.db_Query("select IDENT_CURRENT('ImageInfo')");
			rs.next();
			imageId = rs.getInt(1);
			rs.close();
			sql_str = "insert into BinaryImage(ImageId,Path) values ("
					+ imageId + ",'" + imageBinary + "')";
			if (!dbcon.db_Execute(sql_str)) {
				System.out.println("�����ֵͼ��Ϣ��SQL������");
				dbcon.db_Execute("delete ImageInfo where Id =" + imageId);
				return false;
			}
			dbcon.db_Close();
		} catch (Exception e) {
			System.out.println("�����ֵͼ��������Ϣ����");
			return false;
		}

		try {
			db_Operator dbcon = new db_Operator("sqlserver", dbName, dbUsr, dbPwd);
			rs = dbcon.db_Query("select IDENT_CURRENT('BinaryImage')");
			rs.next();
			binaryId = rs.getInt(1);
			rs.close();
			// �ֱ������������ݿ�
			if (!dbcon
					.InsertFeature(feature[0], feature[0].length, 2, binaryId)) {
				System.out.println("��������ֵ��������ɫ����ʧ��");
				dbcon.db_Execute("delete FeatureData where BiImageId = "
						+ binaryId);
				return true;
			}
			if (!dbcon
					.InsertFeature(feature[1], feature[1].length, 1, binaryId)) {
				System.out.println("��������ֵ��������״����ʧ��");
				dbcon.db_Execute("delete FeatureData where BiImageId = "
						+ binaryId);
				return true;
			}
			if (!dbcon
					.InsertFeature(feature[2], feature[2].length, 3, binaryId)) {
				System.out.println("��������ֵ��������������ʧ��");
				dbcon.db_Execute("delete FeatureData where BiImageId = "
						+ binaryId);
				return true;
			}
			dbcon.db_Execute("update ImageInfo set FeatureFlag = 1 where Id = "
					+ binaryId);
		} catch (Exception e) {
			System.out.println("��������ֵ��������������");
			return true;
		}
		return true;
	}

	// ͼƬ���룬����1��ʾͼƬ�ָ��������2��ʾ���ظ�ͼƬ������3��ʾ��ͼƬ����Ь�࣬����4��ʾ������Ϣʧ�ܣ�����0��ʾ������ȷ
	public int picImport(String imagePath, String msgString) {
		int pos = imagePath.lastIndexOf('.');
		String binaryPath = imagePath.substring(0, pos) + "_b"
				+ imagePath.substring(pos);
		double[][] feature = picPreDeal(imagePath, binaryPath);

		// ���е���
		if (!picImportMsg(imagePath, binaryPath, msgString, feature)) {
			new File(imagePath).delete();
			new File(binaryPath).delete();
			return 4;
		}
		return 0;
	}

}
