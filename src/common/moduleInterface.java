package common;

/**
 * moduleInterfaceʵ�ֵ���JNI�ķ���
 * @author zhangzhizhi
 *
 */
public class moduleInterface {
	static {
		System.loadLibrary("jnimoduleInterface");
	}

	/**
	 * ��ɫ������ȡ
	 * @param imagePath ԭͼ·��
	 * @param imageBinary ��ֵͼ·��
	 * @param result ����ֵ����
	 * @param dimension ά��
	 * @return 0��ʾ��ȡ�ɹ���������ʾʧ��
	 */
	public native int IFE_Color(String imagePath, String imageBinary,
			double[] result, int[] dimension);
	/**
	 * 
	 * @param imgNum ��ɫ����������
	 * @param imgColorDim  ��ɫ������ά��
	 * @param imgColorFeat  ��ɫ����
	 * @param domiColorLabel ���ص���ɫ�����
	 * @param domiColorNum ������������
	 * @return 0��ʾ�ɹ���������ʾʧ��
	 */
     public native boolean ISC_DominantColor(int imgNum,int imgColorDim,double[]imgColorFeat,
    		int[]domiColorLabel,int domiColorNum);
	/**
	 * ��״������ȡ
	 * @param imagePath ԭͼ·��
	 * @param imageBinary ��ֵͼ·��
	 * @param result ����ֵ����
	 * @param dimension ά��
	 * @return 0��ʾ��ȡ�ɹ���������ʾʧ��
	 */
	public native int IFE_Shape(String imagePath, String imageBinary,
			double[] result, int[] dimension);

	/**
	 * ����������ȡ
	 * @param imagePath ԭͼ·��
	 * @param imageBinary ��ֵͼ·��
	 * @param result ����ֵ����
	 * @param dimension ά��
	 * @return 0��ʾ��ȡ�ɹ���������ʾʧ��
	 */
	public native int IFE_Texture(String imagePath, String imageBinary,
			double[] result, int[] dimension);

	/**
	 * ��������
	 * @param dbtype ���ݿ�����
	 * @param dbname ���ݿ���
	 * @param username ���ݿ��û���
	 * @param password ����
	 * @param featId Ҫ���������ṹ���������������ݿ��е�Id
	 * @param indId ���������ṹ�����ݿ��ж�Ӧ����������id
	 * @param DBFeature Ҫ�����������������ݵ�����
	 * @param DBDataId Ҫ�������������������ݿ���Id����˳����    DBFeatureһһ��Ӧ
	 * @param sampleNum ���ݵĸ���
	 * @param dim ���ݵ�ά��
	 * @param gaussianLow ��˹�ɷָ������ޣ�ȱʡֵΪ1
	 * @param gaussianHigh ��˹�ɷָ������ޣ�ȱʡֵΪ30
	 * @param maxTreeLevel ���νṹ�������ޣ�ȱʡֵΪ5
	 * @param minNode �ڵ������ݸ��������ޣ�ȱʡֵΪ30
	 * @param maxPercentage �ӽڵ������������ռ���ڵ�ٷֱȵ����ޣ�ȱʡֵΪ0.5
	 * @param minPercentage �ӽڵ������������ռ���ڵ�ٷֱȵ�����,ȱʡֵΪ0.01
	 * @param covmin covminֵ
	 * @return -1��ʾRun_Properly, -2��ʾThe_Input_Para_Is_Illegal��-4��ʾCan_Not_Open_The_Database
	 */
	public native int IIndex_Construct(String dbtype,String dbname, String username,
			String password, int featId, int indId, int cateId,double[][] DBFeature,
			long[] DBDataId, int sampleNum, int dim, int gaussianLow,
			int gaussianHigh, int maxTreeLevel, int minNode,
			double maxPercentage, double minPercentage, double covmin);

	/**
	 * ����ǰ�ĳ�ʼ������featureTypeId�����Ӧ�ĸ����������������ṹ�����ڴ棻����indidΪ���������ṹ�����ݿ��ж�Ӧ����������id
	 * @param dbtype ���ݿ�����
	 * @param dbname ���ݿ���
	 * @param username ���ݿ��û���
	 * @param password ����
	 * @param featureTypeId Ҫ���������ṹ�ĸ�������Id
	 * @param featureTypeNum ����Id�ĸ���
	 * @param indexid ���������ṹ�����ݿ��ж�Ӧ����������id
	 * @return �������еĽ�����룬��-1��ʾRun_Properly, -2��ʾThe_Input_Para_Is_Illegal��-4��ʾCan_Not_Open_The_Database��
	 */
	public native int IIndex_InitialIndeForest(String dbtype,String dbname, String username,
			String password, int[] featureTypeId, int featureTypeNum,
			int indexid, int[] cateId, int cateIdNum);

	/**
	 * ���ڶ��������������ṹ���м���
	 * @param userFeature �����������ͼƬ����ĸ������������飬�����������ݴ���Ϊһ��double���飬���ӵ�˳����IIndex_InitialIndeForest������ featureTypeId��˳����ͬ
	 * @param featureTypeNum ��������ĸ���
	 * @param dim ��������ά��
	 * @param resultPicID �������������ͼƬid������
	 * @return ������ֵ���ڵ���0�����ʾ������������ݵĸ�����������ֵС��0�����ʾ������룬��-1��ʾRun_Properly, -2��ʾThe_Input_Para_Is_Illegal��-4��ʾCan_Not_Open_The_Database
	 */
	public native int IIndex_Search(double[] userFeature, int featureTypeNum,
			int[] dim, long[] resultPicID,int cateId);

	/**
	 * ����ɼ������ͷ������ṹ���ڴ�ռ�
	 * @param featureTypeNum ��������ĸ���
	 * @return �������еĽ�����룬��-1��ʾRun_Properly, -2��ʾThe_Input_Para_Is_Illegal��-4��ʾCan_Not_Open_The_Database��
	 */
	public native int IIndex_FreeMemory(int featureTypeNum);
}
