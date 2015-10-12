package picView;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import myUISegment.Deal;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyImageViewer {

	private JFrame frame;
	private int i = 1;
	private double factor = 1;
	private DrawPannel panel_1;
	private int screenWidth;
	private int screenHeight;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyImageViewer window = new MyImageViewer();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MyImageViewer() {
		predeal();
		initialize();
		deal();
	}
	private void predeal(){
		screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds((screenWidth-450)/2, (screenHeight-300)/2,450 , 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel_1 = new DrawPannel();
		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);
		frame.addKeyListener(new MykKeyAdapter());
	}
	
	class MykKeyAdapter extends KeyAdapter{
		public void keyPressed(KeyEvent arg0) {
			//�Ҽ�ͷ
			if (arg0.getKeyCode() == 39){
				i++;
				factor = 1;
				panel_1.setImagePath("C:\\"+i+".jpg");
			}
			//���ͷ
			else if (arg0.getKeyCode() == 37){
				i--;
				factor = 1;
				panel_1.setImagePath("C:\\"+i+".jpg");
			}
			//�ϼ�ͷ
			else if (arg0.getKeyCode() == 38){
				factor *= 2;
				panel_1.setFlag(1);
			}
			//�¼�ͷ
			else if (arg0.getKeyCode() == 40){
				factor /= 2;
				panel_1.setFlag(1);
			}
		}
	}
	
	private void deal() {
		
	}

	class DrawPannel extends JPanel{
		private Image image = null;
		//0��ʾԭͼ��1��ʾ�Ŵ���С��
		private int flag = 0;
		public DrawPannel(){
			try {
				image = ImageIO.read(new FileInputStream("C:\\1.jpg"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void setImagePath(String imgPath) {   
	        // �÷������Ƽ�ʹ�ã��÷����������أ�ͼ�񲢲����ص��ڴ棬����ͼ��Ŀ�͸�ʱ�᷵��-1��   
	        // image = Toolkit.getDefaultToolkit().getImage(imgPath);   
	        try {   
	            // �÷����Ὣͼ����ص��ڴ棬�Ӷ��õ�ͼ�����ϸ��Ϣ��   
	            image = ImageIO.read(new FileInputStream(imgPath));
	        } catch (Exception e) {   
	            System.out.println("ͼƬ������");   
	        }
	        repaint();
	    }
		public void setFlag(int flag) {   
	        this.flag = flag;
	        repaint();
	    }
		public void paintComponent(Graphics g1) {  
			if (null == image) {   
	            return;   
	        }
			int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);
	        
			super.paintComponent(g1);
	        Graphics g = (Graphics) g1;   
	        if (flag == 0){
	        	
	        }
	        else if (flag == 1){
	        	imageWidth = (int)(imageWidth*factor);
	        	imageHeight = (int)(imageHeight*factor);
			}
	        
	        if (imageWidth > 450 || imageHeight > 300){
		        int x = (screenWidth-imageWidth)/2;
		        int y = (screenHeight-imageHeight)/2;
		        if (x <= 0 && y <= 0){
		        	frame.setBounds(0, 0,screenWidth , screenHeight);
		        }
		        else if (x <= 0 && y > 0){
		        	frame.setBounds(0, y,screenWidth , imageHeight);
		        }
		        else if (x > 0 && y <= 0){
		        	frame.setBounds(x, 0,imageWidth , screenHeight);
		        }
		        else {
		        	frame.setBounds(x, y,imageWidth , imageHeight);
				}
	        }
	        g.drawImage(image, 0, 0, imageWidth , imageHeight,this);
	    } 
	}
}
