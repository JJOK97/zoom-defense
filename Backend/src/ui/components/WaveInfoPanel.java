package ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import ui.components.common.PixelBackgroundPanel;
import ui.components.common.PixelButton;
import ui.components.common.UIConstants;

/**
 * 현재 웨이브 정보와 진행 상황을 표시하는 패널
 */
public class WaveInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// UI 컴포넌트
	private JLabel lblWaveTitle;
	private JLabel lblWaveNumber;
	private JLabel lblProgress;
	private JProgressBar progressBar;

	// 웨이브 정보
	private int currentWave = 1;

	// 다음 웨이브 관련 변수
	private ActionListener nextWaveListener;
	private boolean nextWaveEnabled = true;

	/**
	 * 기본 생성자
	 */
	public WaveInfoPanel() {
		initialize();
	}

	/**
	 * 웨이브 번호를 지정하는 생성자
	 * 
	 * @param waveNumber 시작 웨이브 번호
	 */
	public WaveInfoPanel(int waveNumber) {
		this.currentWave = waveNumber;
		initialize();
	}

	/**
	 * 패널 초기화
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		setBackground(new Color(40, 40, 50));
		setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 2));

		// 타이틀 패널
		JPanel titlePanel = new PixelBackgroundPanel();
		titlePanel.setLayout(new BorderLayout());
		titlePanel.setBorder(new EmptyBorder(5, 10, 5, 10));

		lblWaveTitle = new JLabel("웨이브 정보", SwingConstants.LEFT);
		lblWaveTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
		lblWaveTitle.setForeground(new Color(220, 220, 255));

		titlePanel.add(lblWaveTitle, BorderLayout.WEST);

		// 웨이브 번호 라벨
		lblWaveNumber = new JLabel("웨이브 " + currentWave + "/20", SwingConstants.RIGHT);
		lblWaveNumber.setFont(new Font("SansSerif", Font.BOLD, 14));
		lblWaveNumber.setForeground(new Color(200, 200, 255));

		titlePanel.add(lblWaveNumber, BorderLayout.EAST);

		// 정보 패널
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2, 1, 0, 5));
		infoPanel.setOpaque(false);
		infoPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

		// 진행 상황 라벨
		lblProgress = new JLabel("적 0/0 처치", SwingConstants.LEFT);
		lblProgress.setFont(new Font("SansSerif", Font.PLAIN, 12));
		lblProgress.setForeground(new Color(200, 200, 255));

		// 진행 상황 프로그레스 바
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setForeground(new Color(100, 100, 180));
		progressBar.setBackground(new Color(50, 50, 70));
		progressBar.setFont(new Font("Courier New", Font.BOLD, 12));

		infoPanel.add(lblProgress);
		infoPanel.add(progressBar);

		// 전체 패널에 배치
		add(titlePanel, BorderLayout.NORTH);
		add(infoPanel, BorderLayout.CENTER);

		// 웨이브 정보 업데이트
		updateWaveInfo();
	}

	/**
	 * 웨이브 정보 업데이트
	 */
	private void updateWaveInfo() {
		lblWaveNumber.setText("웨이브 " + currentWave + "/20");
	}

	/**
	 * 현재 웨이브 번호 반환
	 */
	public int getCurrentWave() {
		return currentWave;
	}

	/**
	 * 웨이브 번호 업데이트
	 */
	public void updateWaveNumber(int waveNumber) {
		this.currentWave = waveNumber;
		lblWaveNumber.setText("웨이브 " + currentWave + "/20");
	}

	/**
	 * 웨이브 진행 상태 업데이트
	 */
	public void updateWaveProgress(int killed, int total) {
		lblProgress.setText("적 " + killed + "/" + total + " 처치");

		// 웨이브 시작 시에는 프로그레스 바를 100%로 설정하고, 적을 처치할수록 감소시킵니다.
		int remainingPercent = total > 0 ? 100 - ((killed * 100) / total) : 0;
		progressBar.setValue(remainingPercent);
		progressBar.setString(killed + "/" + total);
		
		// 남은 적의 비율에 따라 색상 변경
		if (remainingPercent > 66) {
			// 적이 많이 남았을 때 (66% 이상) - 빨간색
			progressBar.setForeground(new Color(200, 50, 50));
		} else if (remainingPercent > 33) {
			// 적이 중간쯤 남았을 때 (33% ~ 66%) - 노란색
			progressBar.setForeground(new Color(220, 180, 50));
		} else {
			// 적이 거의 다 처치되었을 때 (33% 이하) - 초록색
			progressBar.setForeground(new Color(50, 180, 50));
		}
	}

}