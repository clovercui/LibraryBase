package myUISegment;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class PicSegment {
	private int height;			//ԭͼ�߶�
	private int width;			//ԭͼ���
	private BufferedImage sourceImg;	//ԭͼ��ͼ����
	private int rgb;			//���ص��RGBֵ
	private int point[][];		//��ʾÿ�α��������Ľ����0��ʾǰ������ɫ����1��ʾ��������ɫ��
	private int visit[][];		//���ʱ�־��0��ʾδ���ʣ�1��ʾ�ѷ���
	private int draw[][];		//���Ĵ�������0��ʾǰ������ɫ����1��ʾ��������ɫ��
	private double gray[][];	//����ÿ����ĻҶ�ֵ
	private double DOOR = 15;	//��ֵ��С
//	private int ready[][];		//����������
//	private int listlength = 0;	//����������
	public ArrayList<Point> resultList = new ArrayList<Point>();

	public PicSegment() {

	}

	/**
	 * ���캯����������ɫ�������ֵ
	 * @param door ��ֵ
	 */
	public PicSegment(double door) {
		DOOR = door;
	}

	/**
	 * ��ĳ����������й��ѱ������ҵ���ɫ����ĵ�
	 * @param xx ��ĺ�����
	 * @param yy ���������
	 * @param door ��ֵ
	 * @return �����ĵ������
	 */
	private int travel(int xx, int yy, double door) {
		double min = gray[xx][yy] - door;
		double max = gray[xx][yy] + door;
		int list[][] = new int[width * height][2];
		int count = -1;
		list[++count][0] = xx;
		list[count][1] = yy;
		point[xx][yy] = 1;
		for (int i = 0; i <= count; i++) {
			int x = list[i][0];
			int y = list[i][1];
			if (x - 1 >= 0) {
				if (visit[x - 1][y] == 0 && gray[x - 1][y] > min
						&& gray[x - 1][y] < max) {
					list[++count][0] = x - 1;
					list[count][1] = y;
					point[x - 1][y] = 1;
				}
				visit[x - 1][y] = 1;
			}
			if (x + 1 < width) {
				if (visit[x + 1][y] == 0 && gray[x + 1][y] > min
						&& gray[x + 1][y] < max) {
					list[++count][0] = x + 1;
					list[count][1] = y;
					point[x + 1][y] = 1;
				}
				visit[x + 1][y] = 1;
			}
			if (y - 1 >= 0) {
				if (visit[x][y - 1] == 0 && gray[x][y - 1] > min
						&& gray[x][y - 1] < max) {
					list[++count][0] = x;
					list[count][1] = y - 1;
					point[x][y - 1] = 1;
				}
				visit[x][y - 1] = 1;
			}
			if (y + 1 < height) {
				if (visit[x][y + 1] == 0 && gray[x][y + 1] > min
						&& gray[x][y + 1] < max) {
					list[++count][0] = x;
					list[count][1] = y + 1;
					point[x][y + 1] = 1;
				}
				visit[x][y + 1] = 1;
			}
		}
		return count;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * ѡ�������ͨ�����������㷨
	 * @param xx ��ĺ�����
	 * @param yy ���������
	 * @return �����ĵ������
	 */
	private int maxArea(int xx, int yy) {
		int list[][] = new int[width * height][2];
		int count = -1;
		resultList.clear();
		list[++count][0] = xx;
		list[count][1] = yy;
		for (int i = 0; i <= count; i++) {
			int x = list[i][0];
			int y = list[i][1];
			draw[x][y] = point[x][y];
			if (x - 1 >= 0) {
				if (visit[x - 1][y] == 0 && point[x - 1][y] == 0) {
					list[++count][0] = x - 1;
					list[count][1] = y;
				}
				visit[x - 1][y] = 1;
			}
			if (x + 1 < width) {
				if (visit[x + 1][y] == 0 && point[x + 1][y] == 0) {
					list[++count][0] = x + 1;
					list[count][1] = y;
				}
				visit[x + 1][y] = 1;
			}
			if (y - 1 >= 0) {
				if (visit[x][y - 1] == 0 && point[x][y - 1] == 0) {
					list[++count][0] = x;
					list[count][1] = y - 1;
				}
				visit[x][y - 1] = 1;
			}
			if (y + 1 < height) {
				if (visit[x][y + 1] == 0 && point[x][y + 1] == 0) {
					list[++count][0] = x;
					list[count][1] = y + 1;
				}
				visit[x][y + 1] = 1;
			}
			if (x-1<0 || x+1>=width ||y-1<0 || y+1>=height ){
				resultList.add(new Point(x, y));
			}
			else if (point[x-1][y]==1||point[x+1][y]==1||point[x][y-1]==1||point[x][y+1]==1){
				resultList.add(new Point(x, y));
			}
		}
		return count;
	}

	/**
	 * �ָ�������
	 * @param imagePath ԭͼ·��
	 * @param binaryPath ��ֵͼ·�� 
	 * @return true��ʾ�ָ�ɹ���false��ʾ�ָ�ʧ��
	 */
	public ArrayList<Point> startSegment(String imagePath, String binaryPath,ArrayList<Point> pointsList) {
		int i, j;
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(imagePath);
			sourceImg = javax.imageio.ImageIO.read(fileInputStream);

			width = sourceImg.getWidth();
			height = sourceImg.getHeight();
			BufferedImage newImh = new BufferedImage(width, height, 1);
			point = new int[width][height];
			visit = new int[width][height];
			draw = new int[width][height];
			gray = new double[width][height];
			for (i = 0; i < width; i++) {
				for (j = 0; j < height; j++) {
					rgb = sourceImg.getRGB(i, j);
					gray[i][j] = ((rgb >> 16) & 0xff) * 0.3
							+ ((rgb >> 8) & 0xff) * 0.59 + (rgb & 0xff) * 0.11;
				}
			}
			// ȫΪǰ��
			for (i = 0; i < width; i++) {
				for (j = 0; j < height; j++) {
					point[i][j] = 0;
					draw[i][j] = 1;
					visit[i][j] = 0;
				}
			}

			int x = 0, y = 0, flag;
			while (true) {
				flag = 0;
				for (i = 0; i < pointsList.size(); i++) {
					if (point[pointsList.get(i).x][pointsList.get(i).y] == 0) {
						x = pointsList.get(i).x;
						y = pointsList.get(i).y;
						flag = 1;
						break;
					}
				}
				if (flag == 0) {
					break;
				}
				for (i = 0; i < width; i++) {
					for (j = 0; j < height; j++) {
						visit[i][j] = point[i][j];
					}
				}
				travel(x, y, DOOR);
			}

			//��ʾ���
			for (i = 0; i < width; i++) {
				for (j = 0; j < height; j++) {
					draw[i][j] = 1;
					visit[i][j] = 0;
				}
			}
			int num = maxArea(width / 2, height / 2);
			if (num * 1. / (height * width) < 0.1) {
				for (i = 0; i < width; i++) {
					for (j = 0; j < height; j++) {
						draw[i][j] = 1;
						visit[i][j] = 0;
					}
				}
				num = maxArea(width / 2, height / 4);
				if (num * 1. / (height * width) < 0.1) {
					for (i = 0; i < width; i++) {
						for (j = 0; j < height; j++) {
							draw[i][j] = 1;
							visit[i][j] = 0;
						}
					}
					num = maxArea(width / 2, (height * 3) / 4);
					if (num * 1. / (height * width) < 0.1) {
						for (i = 0; i < width; i++) {
							for (j = 0; j < height; j++) {
								draw[i][j] = 1;
								visit[i][j] = 0;
							}
						}
						num = maxArea(width / 4, height / 2);
						if (num * 1. / (height * width) < 0.1) {
							for (i = 0; i < width; i++) {
								for (j = 0; j < height; j++) {
									draw[i][j] = 1;
									visit[i][j] = 0;
								}
							}
							num = maxArea(width * 3 / 4, height * 3 / 4);
						}
					}
				}
			}
			for (i = 0; i < width; i++) {
				for (j = 0; j < height; j++) {
					if (draw[i][j] == 1) {
						newImh.setRGB(i, j, 0xFFFFFF);
					} else {
						newImh.setRGB(i, j, 0x0);
					}
				}
			}
			ImageIO.write(newImh, "JPEG", new File(binaryPath));
			fileInputStream.close();
		} catch (Exception e) {
			System.out.println("�ָ�ԭͼʧ��");
		}
		return resultList;
	}

	public static void main(String[] args) {
//		new PicSegment().startSegment("C:\\1.jpg", "C:\\1_b.jpg");
	}
}
