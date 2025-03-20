package ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

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
    private PixelButton btnNextWave;
    
    // 웨이브 정보
    private int currentWave = 1;
    
    /**
     * 기본 생성자
     */
    public WaveInfoPanel() {
        initialize();
    }
    
    /**
     * 웨이브 번호를 지정하는 생성자
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
        
        // 컨트롤 패널
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // 다음 웨이브 버튼
        btnNextWave = new PixelButton("다음 웨이브", new Font("SansSerif", Font.BOLD, 12));
        btnNextWave.setBackground(new Color(80, 80, 150));
        btnNextWave.setBorderColor(new Color(120, 120, 200));
        btnNextWave.setPreferredSize(new Dimension(120, 30));
        
        controlPanel.add(btnNextWave);
        
        // 전체 패널에 배치
        add(titlePanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        // 웨이브 정보 업데이트
        updateWaveInfo();
    }
    
    /**
     * 웨이브 정보 업데이트
     */
    private void updateWaveInfo() {
        lblWaveNumber.setText("웨이브 " + currentWave + "/20");
        
        // 다음 웨이브 버튼 활성화/비활성화 상태 설정
        btnNextWave.setEnabled(currentWave < 20);
    }
    
    /**
     * 현재 웨이브 번호 반환
     */
    public int getCurrentWave() {
        return currentWave;
    }
} 