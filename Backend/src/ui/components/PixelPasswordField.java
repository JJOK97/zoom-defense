package ui.components;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPasswordField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * 픽셀 느낌의 패스워드 필드 컴포넌트
 */
public class PixelPasswordField extends JPasswordField {
    private static final long serialVersionUID = 1L;
    
    /**
     * 생성자
     * @param columns 열 수
     */
    public PixelPasswordField(int columns) {
        super(columns);
        setupStyle();
    }
    
    /**
     * 생성자
     * @param columns 열 수
     * @param font 폰트
     */
    public PixelPasswordField(int columns, Font font) {
        super(columns);
        setFont(font);
        setupStyle();
    }
    
    /**
     * 스타일 설정
     */
    private void setupStyle() {
        setBackground(new Color(18, 18, 18));
        setForeground(new Color(0, 255, 0)); // 고전 녹색 텍스트
        setCaretColor(new Color(0, 255, 0));
        
        Border lineBorder = BorderFactory.createLineBorder(new Color(76, 76, 76), 2);
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
        setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
    }
} 