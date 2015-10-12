package Test;

import java.io.File;
import java.sql.ResultSet;

import common.SuperPicSegment;
import common.db_Operator;
import common.moduleInterface;



public class featureTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File[] files;
		String rootPath = "C:\\Test\\1.1\\segment";
		String imagePath;
		files = new File(rootPath).listFiles();
		long time3 = System.currentTimeMillis();
		moduleInterface jniinterface = new moduleInterface();
		db_Operator dbcon = new db_Operator("sqlserver", "Spider", "sa", "20091743");
		// Java�ָ�
		for (int i = 0; i < files.length; i++) {
			imagePath = files[i].getAbsolutePath();
			if (imagePath.charAt(imagePath.length() - 5) != 'b') {
				int pos = imagePath.lastIndexOf('.');
				String imageBinary = imagePath.substring(0, pos) + "_b"
						+ imagePath.substring(pos);
				new SuperPicSegment().startSegment(imagePath, imageBinary);
				
				double[] Colorresult = new double[100];
				int[] Colordimension = new int[1];
				double[] Shaperesult = new double[100];
				int[] Shapedimension = new int[1];
				double[] Textureresult = new double[300];
				int[] Texturedimension = new int[1];
				double[][] feature = new double[3][];
				// ����������ȡģ��
				jniinterface.IFE_Color(imagePath, imageBinary, Colorresult,
						Colordimension);
				jniinterface.IFE_Shape(imagePath, imageBinary, Shaperesult,
						Shapedimension);
				jniinterface.IFE_Texture(imagePath, imageBinary, Textureresult,
						Texturedimension);
				feature[0] = new double[Colordimension[0]];
				System.arraycopy(Colorresult, 0, feature[0], 0, Colordimension[0]);
				feature[1] = new double[Shapedimension[0]];
				System.arraycopy(Shaperesult, 0, feature[1], 0, Shapedimension[0]);
				feature[2] = new double[Texturedimension[0]];
				System.arraycopy(Textureresult, 0, feature[2], 0, Texturedimension[0]);
				
				String sql_str;
				String msgString = "Ϊ����᳹��ʵ����ʮ�˴��񣬽�һ���ƽ��׶������������裬����׶�����������ʣ����´�ѧ��˼�����ι�����@@123.00@@http://cs.bit.edu.cn/news.php?nid=668";
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
					if (!dbcon.db_Execute(sql_str)) {
						System.out.println("����ͼƬ��SQL������");
						return;
					}
				} catch (Exception e) {
					System.out.println("����ͼƬ��������Ϣ����");
					return;
				}
				// ����ֵͼ��Ϣ��ͼƬ��ϢId�������ݿ��ֵͼ ��
				try {
					rs = dbcon.db_Query("select IDENT_CURRENT('ImageInfo')");
					rs.next();
					imageId = rs.getInt(1);
					rs.close();
					sql_str = "insert into BinaryImage(ImageId,Path) values ("
							+ imageId + ",'" + imageBinary + "')";
					if (!dbcon.db_Execute(sql_str)) {
						System.out.println("�����ֵͼ��Ϣ��SQL������");
						dbcon.db_Execute("delete ImageInfo where Id =" + imageId);
						return ;
					}
				} catch (Exception e) {
					System.out.println("�����ֵͼ��������Ϣ����");
					return ;
				}

				try {
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
						return ;
					}
					if (!dbcon
							.InsertFeature(feature[1], feature[1].length, 1, binaryId)) {
						System.out.println("��������ֵ��������״����ʧ��");
						dbcon.db_Execute("delete FeatureData where BiImageId = "
								+ binaryId);
						return ;
					}
					if (!dbcon
							.InsertFeature(feature[2], feature[2].length, 3, binaryId)) {
						System.out.println("��������ֵ��������������ʧ��");
						dbcon.db_Execute("delete FeatureData where BiImageId = "
								+ binaryId);
						return ;
					}
					dbcon.db_Execute("update ImageInfo set FeatureFlag = 1 where Id = "
							+ binaryId);
					
				} catch (Exception e) {
					System.out.println("��������ֵ��������������");
					return ;
				}
			}
		}
		long time4 = System.currentTimeMillis();
		System.out.println(time4-time3);
	}

}
