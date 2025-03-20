package ui.components.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.imageio.ImageIO;
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
    private Color borderColor = Color.BLACK;
    private boolean isPressed = false;
    private boolean isHovered = false;
    private int pixelBorderSize = 1;
    private String iconName = "";
    private Image iconImage = null;
    
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
    
    /**
     * 테두리 색상 설정
     * @param color 테두리 색상
     */
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
    
    /**
     * 픽셀 테두리 크기 설정
     * @param size 테두리 크기
     */
    public void setPixelBorder(int size) {
        this.pixelBorderSize = size;
        repaint();
    }
    
    /**
     * 아이콘 이름 설정 및 이미지 로드
     * @param name 아이콘 이름
     */
    public void setIconName(String name) {
        this.iconName = name;
        
        // 이미지 로드 시도
        if (name != null && !name.isEmpty()) {
            try {
                String projectPath = System.getProperty("user.dir");
                String iconPath = projectPath + "/icons/" + name + ".png";
                File file = new File(iconPath);
                if (file.exists()) {
                    iconImage = ImageIO.read(file);
                }
            } catch (Exception e) {
                iconImage = null;
            }
        } else {
            iconImage = null;
        }
        
        repaint();
    }
    
    /**
     * 배경색 설정
     * @param color 배경색
     */
    @Override
    public void setBackground(Color color) {
        this.normalColor = color;
        this.hoverColor = new Color(
            Math.min(color.getRed() + 30, 255),
            Math.min(color.getGreen() + 30, 255),
            Math.min(color.getBlue() + 30, 255)
        );
        this.pressedColor = new Color(
            Math.max(color.getRed() - 30, 0),
            Math.max(color.getGreen() - 30, 0),
            Math.max(color.getBlue() - 30, 0)
        );
        repaint();
    }
    
    /**
     * 텍스트 색상 설정
     * @param color 텍스트 색상
     */
    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        this.textColor = color;
        repaint();
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
        g2d.setColor(borderColor);
        for (int i = 0; i < pixelBorderSize; i++) {
            g2d.drawRect(i, i, getWidth() - 1 - 2*i, getHeight() - 1 - 2*i);
        }
        
        // 버튼 내부 테두리 (픽셀 느낌)
        g2d.setColor(isPressed ? Color.GRAY : Color.LIGHT_GRAY);
        g2d.drawLine(pixelBorderSize, pixelBorderSize, 
                    getWidth() - 1 - pixelBorderSize, pixelBorderSize);
        g2d.drawLine(pixelBorderSize, pixelBorderSize, 
                    pixelBorderSize, getHeight() - 1 - pixelBorderSize);
        
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawLine(getWidth() - 1 - pixelBorderSize, pixelBorderSize, 
                    getWidth() - 1 - pixelBorderSize, getHeight() - 1 - pixelBorderSize);
        g2d.drawLine(pixelBorderSize, getHeight() - 1 - pixelBorderSize, 
                    getWidth() - 1 - pixelBorderSize, getHeight() - 1 - pixelBorderSize);
        
        // 아이콘 그리기 (로드된 이미지만 표시)
        if (iconImage != null) {
            // 아이콘 크기와 위치 - 크기는 최대한 크게
            int iconSize = Math.min(getHeight() - 8, 64);
            int iconY = (getHeight() - iconSize) / 2;
            int iconX = 10;
            
            // 이미지 아이콘 그리기
            g2d.drawImage(iconImage, iconX, iconY, iconSize, iconSize, this);
        }
        
        // 텍스트 그리기 (그림자 효과로 픽셀 느낌 강화)
        String text = getText();
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        
        // 아이콘이 있으면 텍스트 위치 조정
        if (iconImage != null) {
            x += 32;  // 아이콘을 고려한 여백
        }
        
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