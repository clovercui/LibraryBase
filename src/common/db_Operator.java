package common;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class db_Operator {
	private Connection con = null;
	private Statement st = null;
	private String url = null;
	private String name = "sa";
	private String password = "20091743";

	public db_Operator(){
		String dbtype = "sqlserver";
		String dbName = "Shoes20130708";
		if (dbtype.equals("sqlserver")) {
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException e) {
				System.out.println("classNotFound");
			}
			url = "jdbc:sqlserver://localhost:1433;DatabaseName=" + dbName;
		} else if (dbtype.equals("mysql")) {
			try {
				Class.forName("org.gjt.mm.mysql.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println("classNotFound");
			}
			url = "jdbc:mysql://localhost:3306/" + dbName;
		} else {
			System.out.println("wrong db type");
		}
		try {
			con = DriverManager.getConnection(url, name, password);
			st = con.createStatement();

		} catch (Exception e) {
			System.out.println("connectionIsFaile");
		}

	}
	// �������ݿ�
	public db_Operator(String dbtype, String dbName, String usrName, String pwd) {
		name = usrName;
		password = pwd;
		if (dbtype.equals("sqlserver")) {
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException e) {
				System.out.println("classNotFound");
			}
			url = "jdbc:sqlserver://localhost:1433;DatabaseName=" + dbName;
		} else if (dbtype.equals("mysql")) {
			try {
				Class.forName("org.gjt.mm.mysql.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println("classNotFound");
			}
			url = "jdbc:mysql://localhost:3306/" + dbName;
		} else {
			System.out.println("wrong db type");
		}
		try {
			con = DriverManager.getConnection(url, name, password);
			st = con.createStatement();

		} catch (Exception e) {
			System.out.println("connectionIsFaile");
		}

	}
	// �������ݿ�
		public db_Operator(String ipAddress, String dbtype, String dbName, String usrName, String pwd) {
			name = usrName;
			password = pwd;
			if (dbtype.equals("sqlserver")) {
				try {
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				} catch (ClassNotFoundException e) {
					System.out.println("classNotFound");
				}
				url = "jdbc:sqlserver://"+ipAddress+":1433;DatabaseName=" + dbName;
			} else if (dbtype.equals("mysql")) {
				try {
					Class.forName("org.gjt.mm.mysql.Driver");
				} catch (ClassNotFoundException e) {
					System.out.println("classNotFound");
				}
				url = "jdbc:mysql://"+ipAddress+":3306/" + dbName;
			} else {
				System.out.println("wrong db type");
			}
			try {
				con = DriverManager.getConnection(url, name, password);
				st = con.createStatement();

			} catch (Exception e) {
				System.out.println("connectionIsFaile");
			}

		}
	// ִ��sql���
	public boolean db_Execute(String sql) {
		try {

			st.executeUpdate(sql);
			return true;
		} catch (Exception err) {
			System.out.println("���롢�޸Ļ�ɾ����������sql���");
			return false;

		}
	}

	// ��ѯ
	public ResultSet db_Query(String sql) {
		try {
			return st.executeQuery(sql);
		} catch (Exception err) {
			System.out.println("��ѯ��������sql���");
			return null;
		}

	}

	// �ر�
	public void db_Close() {
		try {
			if (st != null)
				st.close();
			if (con != null)
				con.close();
		} catch (SQLException e) {
			System.out.println("���ݿ�ر�ʧ��");
		}

	}

	// ��������
	public boolean InsertFeature(double[] Feature, int FeatureDim,
			int FeatureTypeId, int BiImageId) {
		try {
			String sql = "insert into FeatureData values ("+FeatureTypeId+ "," + BiImageId
					+ ",?)";
			PreparedStatement pstmt = con.prepareStatement(sql);

			byte[] writeBuffer = new byte[FeatureDim << 3];
			for (int i = 0; i < FeatureDim; i++) {
				long v = Double.doubleToLongBits(Feature[i]);
				writeBuffer[8 * i + 7] = (byte) (v >>> 56);
				writeBuffer[8 * i + 6] = (byte) (v >>> 48);
				writeBuffer[8 * i + 5] = (byte) (v >>> 40);
				writeBuffer[8 * i + 4] = (byte) (v >>> 32);
				writeBuffer[8 * i + 3] = (byte) (v >>> 24);
				writeBuffer[8 * i + 2] = (byte) (v >>> 16);
				writeBuffer[8 * i + 1] = (byte) (v >>> 8);
				writeBuffer[8 * i + 0] = (byte) (v >>> 0);
			}
			InputStream inputStream = new ByteArrayInputStream(writeBuffer);
			pstmt.setBinaryStream(1, inputStream, inputStream.available());
			pstmt.executeUpdate();
			inputStream.close();
			pstmt.close();
			return true;
		} catch (Exception err) {
			System.out.println("������������");
			return false;

		}
	}
	public boolean GetFeature(double[][] Feature, long[] DBDataId,
			int FeatureDim, int FeatureTypeId,int cateId) {
		try {
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery("select FeatureData,BiImageId from FeatureData where FeatureTypeId ="
					+ FeatureTypeId+" and BiImageId IN (select BiImageId from Binary_Category where CategoryId = "+cateId+")");
			int num = 0;
			while (rs.next()) {
				byte[] b = rs.getBytes(1);
				DBDataId[num] = rs.getLong(2);
				long l;
				for (int i = 0; i < b.length;) {
					l = b[i++];
					l &= 0xff;
					l |= ((long) b[i++] << 8);
					l &= 0xffffl;
					l |= ((long) b[i++] << 16);
					l &= 0xffffffl;
					l |= ((long) b[i++] << 24);
					l &= 0xffffffffl;
					l |= ((long) b[i++] << 32);
					l &= 0xffffffffffl;
					l |= ((long) b[i++] << 40);
					l &= 0xffffffffffffl;
					l |= ((long) b[i++] << 48);
					l &= 0xffffffffffffffl;
					l |= ((long) b[i++] << 56);
					l &= 0xffffffffffffffffl;
					Feature[num][(i - 1) >> 3] = Double
							.longBitsToDouble(l);
				}
				num++;
			}
			rs.close();
			statement.close();
			return true;
		} catch (Exception e) {
			System.out.println("��ȡ��������");
			e.printStackTrace();
			return false;
		}
	}
	// ��������
	public boolean InsertBinary(int ImageId,String path) {
		try {
			String sql = "insert into BinaryImage(ImageId,Path) values ("+ ImageId + ",'" + path + "')";
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.executeUpdate();
			pstmt.close();
			return true;
		} catch (Exception err) {
			System.out.println("������������");
			return false;

		}
	}
	/**
	 * ɾ������������ݱ��¼
	 * @param ��
	 * @return true��ʾ�ɹ���false��ʾʧ��
	 */
	public boolean DeleteIndexNodeData(){
		try {
			Statement statement = con.createStatement();
			statement.execute("delete IndexNodeData");
			statement.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * ɾ������Ҷ���_��ֵͼƬ���¼
	 * @param ��
	 * @return true��ʾ�ɹ���false��ʾʧ��
	 */
	public boolean DeleteIndexLeafNode_BiImage(){
		try {
			Statement statement = con.createStatement();
			statement.execute("delete IndexLeafNode_BiImage");
			statement.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public void close() {
		try {
			if (con != null)
				con.close();
		} catch (SQLException e) {
		}
	}
}
