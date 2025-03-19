package ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

/**
 * 픽셀 느낌의 버튼 컴포넌트
 */
public class PixelButton extends JButton {
    private static final long serialVersionUID = 1L;
    
    private Color normalColor = new Color(76, 76, 76);
    private Color hoverColor = new Color(100, 100, 100);
    private Color pressedColor = new Color(50, 50, 50);
    private Color textColor = new Color(255, 255, 255);
    private boolean isPressed = false;
    private boolean isHovered = false;
    
    /**
     * 생성자
     * @param text 버튼 텍스트
     */
    public PixelButton(String text) {
        super(text);
        setupStyle();
    }
    
    /**
     * 생성자
     * @param text 버튼 텍스트
     * @param font 버튼 폰트
     */
    public PixelButton(String text, Font font) {
        super(text);
        setFont(font);
        setupStyle();
    }
    
    /**
     * 스타일 설정
     */
    private void setupStyle() {
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setForeground(textColor);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // 안티앨리어싱 끄기 (픽셀 느낌을 위해)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        // 버튼 배경
        if (isPressed) {
            g2d.setColor(pressedColor);
            g2d.fillRect(2, 2, getWidth() - 4, getHeight() - 4);
        } else if (isHovered) {
            g2d.setColor(hoverColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2d.setColor(normalColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        
        // 버튼 테두리 (픽셀 느낌)
        g2d.setColor(isPressed ? Color.DARK_GRAY : Color.BLACK);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        
        // 버튼 내부 테두리 (픽셀 느낌)
        g2d.setColor(isPressed ? Color.GRAY : Color.LIGHT_GRAY);
        g2d.drawLine(1, 1, getWidth() - 2, 1);
        g2d.drawLine(1, 1, 1, getHeight() - 2);
        
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawLine(getWidth() - 2, 1, getWidth() - 2, getHeight() - 2);
        g2d.drawLine(1, getHeight() - 2, getWidth() - 2, getHeight() - 2);
        
        // 텍스트 그리기 (그림자 효과로 픽셀 느낌 강화)
        String text = getText();
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        
        if (isPressed) {
            x += 1;
            y += 1;
        }
        
        // 텍스트 그림자
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, x + 1, y + 1);
        
        // 텍스트
        g2d.setColor(textColor);
        g2d.drawString(text, x, y);
    }
} 