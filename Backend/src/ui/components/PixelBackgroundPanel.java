package ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * 픽셀 느낌의 배경 패널 컴포넌트
 */
public class PixelBackgroundPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    /**
     * 생성자
     */
    public PixelBackgroundPanel() {
        setBackground(new Color(42, 54, 59)); // 어두운 배경
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // 안티앨리어싱 끄기 (픽셀 느낌을 위해)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        // 배경 그리드 그리기 (픽셀 느낌 강화)
        g2d.setColor(new Color(50, 62, 68));
        for (int x = 0; x < getWidth(); x += 20) {
            for (int y = 0; y < getHeight(); y += 20) {
                g2d.drawRect(x, y, 20, 20);
            }
        }
        
        // 여기에 추가적인 배경 그래픽 요소 추가 가능
    }
} 