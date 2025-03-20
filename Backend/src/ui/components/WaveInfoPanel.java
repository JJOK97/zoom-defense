package ui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import ui.components.common.PixelLabel;
import ui.components.common.UIConstants;

/**
 * 게임 웨이브 정보를 표시하는 패널
 */
public class WaveInfoPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // 웨이브 관련 컴포넌트
    private JLabel lblWaveTitle;
    private JLabel lblWaveValue;
    
    // 적 처치 관련 컴포넌트
    private JLabel lblEnemyTitle;
    private JProgressBar progressEnemyKill;
    private JLabel lblEnemyValue;
    
    // 현재 웨이브 정보
    private int currentWave;
    private int enemiesKilled = 0;
    private int totalEnemies = 10; // 웨이브별 적 수는 실제 구현에서 계산될 것
    
    /**
     * 생성자
     * @param initialWave 초기 웨이브
     */
    public WaveInfoPanel(int initialWave) {
        this.currentWave = initialWave;
        this.totalEnemies = 10 + (initialWave * 5); // 예시: 웨이브가 증가할수록 적 수 증가
        
        initialize();
    }
    
    /**
     * 패널 초기화
     */
    private void initialize() {
        setLayout(new GridLayout(2, 1, 0, 5));
        setOpaque(false);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200, 120), 1),
                "웨이브 정보",
                SwingConstants.LEFT,
                SwingConstants.TOP,
                UIConstants.getPixelFont(),
                Color.WHITE));
        
        // 웨이브 패널
        JPanel wavePanel = new JPanel();
        wavePanel.setLayout(new GridLayout(1, 2, 5, 0));
        wavePanel.setOpaque(false);
        
        lblWaveTitle = new PixelLabel("현재 웨이브:", SwingConstants.LEFT);
        lblWaveTitle.setForeground(Color.WHITE);
        lblWaveTitle.setFont(UIConstants.getPixelFont());
        
        lblWaveValue = new PixelLabel(currentWave + "", SwingConstants.RIGHT);
        lblWaveValue.setForeground(new Color(100, 180, 255));
        lblWaveValue.setFont(UIConstants.getPixelFont());
        
        wavePanel.add(lblWaveTitle);
        wavePanel.add(lblWaveValue);
        
        // 적 처치 패널
        JPanel enemyPanel = new JPanel();
        enemyPanel.setLayout(new GridLayout(1, 3, 5, 0));
        enemyPanel.setOpaque(false);
        
        lblEnemyTitle = new PixelLabel("처치 상황:", SwingConstants.LEFT);
        lblEnemyTitle.setForeground(Color.WHITE);
        lblEnemyTitle.setFont(UIConstants.getPixelFont());
        
        progressEnemyKill = new JProgressBar(0, totalEnemies);
        progressEnemyKill.setValue(enemiesKilled);
        progressEnemyKill.setStringPainted(false);
        progressEnemyKill.setForeground(new Color(50, 205, 50));
        progressEnemyKill.setBackground(new Color(80, 80, 80));
        
        lblEnemyValue = new PixelLabel(enemiesKilled + " / " + totalEnemies, SwingConstants.RIGHT);
        lblEnemyValue.setForeground(new Color(200, 255, 200));
        lblEnemyValue.setFont(UIConstants.getPixelFont());
        
        enemyPanel.add(lblEnemyTitle);
        enemyPanel.add(progressEnemyKill);
        enemyPanel.add(lblEnemyValue);
        
        // 전체 패널에 추가
        add(wavePanel);
        add(enemyPanel);
    }
    
    /**
     * 웨이브 정보 업데이트
     * @param wave 현재 웨이브
     */
    public void updateWaveInfo(int wave) {
        if (this.currentWave != wave) {
            this.currentWave = wave;
            lblWaveValue.setText(wave + "");
            
            // 보스 웨이브 여부에 따라 색상 변경
            if (wave % 10 == 0) {  // 보스 웨이브 (10, 20, 30, ...)
                lblWaveValue.setForeground(new Color(255, 50, 50));
            } else if (wave % 5 == 0) {  // 준 보스 웨이브 (5, 15, 25, ...)
                lblWaveValue.setForeground(new Color(255, 150, 50));
            } else {
                lblWaveValue.setForeground(new Color(100, 180, 255));
            }
            
            // 새 웨이브에 맞는 적 수로 업데이트
            this.totalEnemies = 10 + (wave * 5);
            this.enemiesKilled = 0;
            progressEnemyKill.setMaximum(totalEnemies);
            progressEnemyKill.setValue(enemiesKilled);
            lblEnemyValue.setText(enemiesKilled + " / " + totalEnemies);
        }
    }
    
    /**
     * 적 처치 정보 업데이트
     * @param killed 처치한 적 수
     * @param total 총 적 수
     */
    public void updateEnemyKilled(int killed, int total) {
        this.enemiesKilled = killed;
        this.totalEnemies = total;
        
        progressEnemyKill.setMaximum(total);
        progressEnemyKill.setValue(killed);
        lblEnemyValue.setText(killed + " / " + total);
        
        // 처치 진행도에 따라 색상 변경
        double ratio = (double) killed / total;
        if (ratio > 0.8) {
            progressEnemyKill.setForeground(new Color(50, 255, 50));
            lblEnemyValue.setForeground(new Color(50, 255, 50));
        } else if (ratio > 0.5) {
            progressEnemyKill.setForeground(new Color(150, 255, 50));
            lblEnemyValue.setForeground(new Color(150, 255, 50));
        } else {
            progressEnemyKill.setForeground(new Color(50, 205, 50));
            lblEnemyValue.setForeground(new Color(200, 255, 200));
        }
    }
    
    /**
     * 패널 크기 조정
     * @param width 창 너비
     * @param height 창 높이
     */
    public void adjustSize(int width, int height) {
        // 폰트 크기 조정
        Font adjustedFont = UIConstants.getScaledPixelFont(width);
        lblWaveTitle.setFont(adjustedFont);
        lblWaveValue.setFont(adjustedFont);
        lblEnemyTitle.setFont(adjustedFont);
        lblEnemyValue.setFont(adjustedFont);
        
        // 패널 높이는 화면 높이의 10%
        int panelHeight = Math.max(70, height / 10);
        setPreferredSize(new Dimension(width / 2, panelHeight));
        
        revalidate();
        repaint();
    }
} 