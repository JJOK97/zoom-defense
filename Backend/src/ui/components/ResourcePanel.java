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
 * 게임 자원(생명력, 돈) 정보를 표시하는 패널
 */
public class ResourcePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // 생명력 관련 컴포넌트
    private JLabel lblLifeTitle;
    private JProgressBar progressLife;
    private JLabel lblLifeValue;
    
    // 돈 관련 컴포넌트
    private JLabel lblMoneyTitle;
    private JLabel lblMoneyValue;
    
    // 현재 자원 값
    private int currentLife;
    private int currentMoney;
    
    /**
     * 생성자
     * @param initialLife 초기 생명력
     * @param initialMoney 초기 자금
     */
    public ResourcePanel(int initialLife, int initialMoney) {
        this.currentLife = initialLife;
        this.currentMoney = initialMoney;
        
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
                "자원 정보",
                SwingConstants.LEFT,
                SwingConstants.TOP,
                UIConstants.getPixelFont(),
                Color.WHITE));
        
        // 생명력 패널
        JPanel lifePanel = new JPanel();
        lifePanel.setLayout(new GridLayout(1, 3, 5, 0));
        lifePanel.setOpaque(false);
        
        lblLifeTitle = new PixelLabel("생명력:", SwingConstants.LEFT);
        lblLifeTitle.setForeground(Color.WHITE);
        lblLifeTitle.setFont(UIConstants.getPixelFont());
        
        progressLife = new JProgressBar(0, 100);
        progressLife.setValue(currentLife);
        progressLife.setStringPainted(false);
        progressLife.setForeground(new Color(255, 50, 50));
        progressLife.setBackground(new Color(80, 80, 80));
        
        lblLifeValue = new PixelLabel(currentLife + " / 100", SwingConstants.RIGHT);
        lblLifeValue.setForeground(new Color(255, 100, 100));
        lblLifeValue.setFont(UIConstants.getPixelFont());
        
        lifePanel.add(lblLifeTitle);
        lifePanel.add(progressLife);
        lifePanel.add(lblLifeValue);
        
        // 돈 패널
        JPanel moneyPanel = new JPanel();
        moneyPanel.setLayout(new GridLayout(1, 2, 5, 0));
        moneyPanel.setOpaque(false);
        
        lblMoneyTitle = new PixelLabel("자금:", SwingConstants.LEFT);
        lblMoneyTitle.setForeground(Color.WHITE);
        lblMoneyTitle.setFont(UIConstants.getPixelFont());
        
        lblMoneyValue = new PixelLabel("$" + currentMoney, SwingConstants.RIGHT);
        lblMoneyValue.setForeground(new Color(255, 215, 0));
        lblMoneyValue.setFont(UIConstants.getPixelFont());
        
        moneyPanel.add(lblMoneyTitle);
        moneyPanel.add(lblMoneyValue);
        
        // 전체 패널에 추가
        add(lifePanel);
        add(moneyPanel);
    }
    
    /**
     * 자원 정보 업데이트
     * @param life 현재 생명력
     * @param money 현재 자금
     */
    public void updateResources(int life, int money) {
        // 생명력 업데이트
        if (this.currentLife != life) {
            this.currentLife = life;
            progressLife.setValue(life);
            lblLifeValue.setText(life + " / 100");
            
            // 생명력이 30% 이하면 색상 변경
            if (life <= 30) {
                progressLife.setForeground(new Color(255, 0, 0));
                lblLifeValue.setForeground(new Color(255, 0, 0));
            } else if (life <= 60) {
                progressLife.setForeground(new Color(255, 150, 50));
                lblLifeValue.setForeground(new Color(255, 150, 50));
            } else {
                progressLife.setForeground(new Color(255, 50, 50));
                lblLifeValue.setForeground(new Color(255, 100, 100));
            }
        }
        
        // 자금 업데이트
        if (this.currentMoney != money) {
            this.currentMoney = money;
            lblMoneyValue.setText("$" + money);
            
            // 자금이 많으면 색상 변경
            if (money >= 1000) {
                lblMoneyValue.setForeground(new Color(180, 255, 100));
            } else if (money >= 500) {
                lblMoneyValue.setForeground(new Color(220, 240, 50));
            } else if (money >= 200) {
                lblMoneyValue.setForeground(new Color(255, 215, 0));
            } else {
                lblMoneyValue.setForeground(new Color(200, 200, 200));
            }
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
        lblLifeTitle.setFont(adjustedFont);
        lblLifeValue.setFont(adjustedFont);
        lblMoneyTitle.setFont(adjustedFont);
        lblMoneyValue.setFont(adjustedFont);
        
        // 패널 높이는 화면 높이의 10%
        int panelHeight = Math.max(70, height / 10);
        setPreferredSize(new Dimension(width / 2, panelHeight));
        
        revalidate();
        repaint();
    }
} 