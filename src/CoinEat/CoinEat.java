package CoinEat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class CoinEat extends JFrame {
	
	//버퍼 이미지 객체와 화면의 이미지를 얻어올 그래픽 객체 생성
	private Image bufferImage;
	private Graphics screenGraphic;
	
	private Clip clip;
	
	//배경,플레이어,코인사진
	private Image backgroundImage = new ImageIcon("src/images/forest.jpg").getImage();
	private Image player = new ImageIcon("src/images/dog.png").getImage();
	private Image coin = new ImageIcon("src/images/meat.png").getImage();
	
	//플레이어의 좌표
	private int playerX, playerY;
	private int playerWidth = player.getWidth(null); //후에 플레이어와 코인의 충돌 여부 판단하기 위해 각 이미지의 크기도 변수 담아놓기, getWidth()와 getHeight()를 이용해 이미지의 가로, 세로 길이 구할 수 있음
	private int playerHeight = player.getHeight(null);
	
	//코인의 좌표
	private int coinX, coinY;
	private int coinWidth = coin.getWidth(null);
	private int coinHeight = coin.getHeight(null);
	
	private int score;
	private int time;
	
	private boolean up, down, left, right;
	
	
	//창 만들기
	public CoinEat() {
		setTitle("강아지 먹이");
		setVisible(true);
		setSize(700, 700);
		setLocationRelativeTo(null); //setLocationRelativeTo의 경우 괄호안에 null을 넣으면 실행 시 창이 화면 가운데에 뜸
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //창 닫을시 프로세스 종료
		addKeyListener(new KeyAdapter() {
			
			//키를 눌렀을 때 실행 할 메소드
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_W:
					up = true;
					break;
				case KeyEvent.VK_S:
					down = true;
					break;
				case KeyEvent.VK_A:
					left = true;
					break;
				case KeyEvent.VK_D:
					right = true;
					break;
				}
					
			}
			
			//키를 뗐을 때 실행 할 메소드
			public void keyReleased(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_W:
					up = false;
					break;
				case KeyEvent.VK_S:
					down = false;
					break;
				case KeyEvent.VK_A:
					left = false;
					break;
				case KeyEvent.VK_D:
					right = false;
					break;
				}
					
			}
		});
		Init();
		while(true) {
			//대기시간 없이 계속 반복하면 무리가 갈 수도 있으므로 대기시간 주기
			try {
				Thread.sleep(10);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			keyProcess();
			crashCheck();
		}
	}
	
	//게임을 시작할 때 초기화해줄 메소드
	public void Init() {
		score = 0;
		playerX = (700-playerWidth)/2;
		playerY = (700-playerHeight)/2;
		
		coinX = (int)(Math.random()*(701-playerWidth)); //(int)(Math.random()*(창의 크기+1-플레이어의 길이)
		coinY = (int)(Math.random()*(701-playerHeight-30))+30; //프레임 틀의 길이를 생각해 30을 빼줌
		
		playSound("src/audio/Dog_and_Pony_Show.wav", true); //background song
	}
	
	//플레이어와 코인이 닿았을 때 점수 획득 구현
	public void crashCheck() {
		if(playerX+playerWidth > coinX && coinX+coinWidth > playerX && 
				playerY + playerHeight > coinY && coinY + coinHeight > playerY) {
			 score += 100;
			 playSound("src/audio/TTing.wav", false); //무한반복X false
			 coinX = (int)(Math.random()*(701-playerWidth));
			 coinY = (int)(Math.random()*(701-playerHeight-30))+30;
		}
	}
	
	//오디오 재생 및 무한 반복 여부 설정 메소드
	public void playSound(String pathName, boolean isLoop) {
		try {
			clip = AudioSystem.getClip();
			File audioFile = new File(pathName);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			clip.open(audioStream);
			clip.start();
			if (isLoop)
				clip.loop(Clip.LOOP_CONTINUOUSLY); //오디오 무한반복
		} catch(LineUnavailableException e) {
			e.printStackTrace();
		} catch(UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//플레이어를 이동시킬 메소드
	public void keyProcess() {
		if(up && playerY -3 > 30) playerY -= 3;
		if(down && playerY + playerHeight +3 < 700) playerY += 3;
		if(left && playerX -3 > 0) playerX -= 3;
		if(right && playerX + playerWidth +3 < 700) playerX += 3;
	}
	
	//화면 깜박임 최소화하기
	public void  paint(Graphics g) {
		bufferImage = createImage(700,700);
		screenGraphic = bufferImage.getGraphics();
		screenDraw(screenGraphic);
		g.drawImage(bufferImage,0, 0, null);
	}
	
	//출력
	public void screenDraw(Graphics g) {
		g.drawImage(backgroundImage, 0, 0, null); //g.drawImage(이미지, 그릴x좌표, 그릴 y좌표, null)입력
		g.drawImage(coin, coinX, coinY, null);
		g.drawImage(player, playerX, playerY, null);
		g.drawImage(coin, coinX, coinY, null);
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.BOLD, 40));
		g.drawString("SCORE : "+score, 420, 80);
		this.repaint();
	}
	
	//생성자 호출
	public static void main(String[] args) {
		new CoinEat();
	}
}
