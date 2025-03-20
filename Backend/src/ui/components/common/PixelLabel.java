package ui.components.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * 픽셀 느낌의 라벨 컴포넌트
 */
public class PixelLabel extends JLabel {
    private static final long serialVersionUID = 1L;
    
    private int titleBounce = 0;
    private int colorPhase = 0;
    private boolean isTitle = false;
    
    /**
     * 생성자
     * @param text 라벨 텍스트
     * @param alignment 정렬 방식
     */
    public PixelLabel(String text, int alignment) {
        super(text, alignment);
        setVerticalAlignment(SwingConstants.CENTER);
        isTitle = text.equals("ZOOM DEFENSE");
    }
    
    /**
     * 애니메이션 상태 업데이트
     * @param bounce 바운스 값
     * @param phase 색상 단계
     */
    public void updateAnimation(int bounce, int phase) {
        if (isTitle) {
            this.titleBounce = bounce;
            this.colorPhase = phase;
            repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // 텍스트가 타이틀인 경우 바운스 애니메이션 적용
        if (isTitle) {
            g2d.translate(0, titleBounce);
            
            // 타이틀에 색상 변화 효과 적용
            float hue = (colorPhase / 360f) % 1f;
            Color titleColor = Color.getHSBColor(hue, 0.8f, 1.0f);
            setForeground(titleColor);
        }
        
        // 안티앨리어싱 끄기 (픽셀 느낌을 위해)
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
        super.paintComponent(g2d);
    }
} 