package ui.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

/**
 * UI 관련 상수와 유틸리티 메서드를 제공하는 클래스
 */
public class UIConstants {
    // 화면 비율 관련 상수
    public static final double SCREEN_WIDTH_PERCENT = 0.8;  // 화면 너비의 80%
    public static final double SCREEN_HEIGHT_PERCENT = 0.8;  // 화면 높이의 80%
    
    // 색상 상수
    public static final Color GOLD_COLOR = new Color(255, 215, 0);
    public static final Color WHITE_COLOR = Color.WHITE;
    public static final Color BLACK_COLOR = Color.BLACK;
    
    // 폰트 관련 상수
    private static Font pixelFont;
    private static Font smallPixelFont;
    private static Font titleFont;
    
    // 초기화 블록
    static {
        initializeFonts();
    }
    
    /**
     * 폰트 초기화 메서드
     */
    private static void initializeFonts() {
        // 기본 폰트 초기화 - 한글 지원 폰트 사용
        // 나눔고딕, 맑은 고딕 등 한글 지원 폰트 중 시스템에 설치된 것 사용
        String[] koreanFonts = {"나눔고딕", "맑은 고딕", "굴림", "돋움"};
        
        // 기본 폰트 설정 (시스템에 있는 한글 폰트로)
        for (String fontName : koreanFonts) {
            try {
                Font testFont = new Font(fontName, Font.PLAIN, 12);
                if (testFont.canDisplayUpTo("한글테스트") == -1) {
                    // 한글을 표시할 수 있는 폰트를 찾았을 때
                    pixelFont = new Font(fontName, Font.BOLD, 20);
                    smallPixelFont = new Font(fontName, Font.PLAIN, 14);
                    titleFont = new Font(fontName, Font.BOLD, 40);
                    break;
                }
            } catch (Exception e) {
                // 폰트를 찾을 수 없으면 다음으로 넘어감
                continue;
            }
        }
        
        // 기본 폰트가 설정되지 않았으면 맑은 고딕으로 강제 설정
        if (pixelFont == null) {
            pixelFont = new Font("맑은 고딕", Font.BOLD, 20);
            smallPixelFont = new Font("맑은 고딕", Font.PLAIN, 14);
            titleFont = new Font("맑은 고딕", Font.BOLD, 40);
        }
        
        // 다양한 경로에서 폰트 파일 탐색
        String[] basePaths = {"", "src/", "Backend/src/", "../"};
        String[] fontDirs = {"ui/fonts/", "fonts/", "resources/fonts/"};
        
        // 영문용 폰트 로드 시도
        boolean englishFontLoaded = false;
        for (String basePath : basePaths) {
            for (String fontDir : fontDirs) {
                String path = basePath + fontDir + "pixel.ttf";
                File fontFile = new File(path);
                if (fontFile.exists()) {
                    try {
                        Font loadedFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                        titleFont = loadedFont.deriveFont(Font.BOLD, 50f);
                        englishFontLoaded = true;
                        break;
                    } catch (FontFormatException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (englishFontLoaded) break;
        }
        
        // 한글용 폰트 로드 시도
        boolean koreanFontLoaded = false;
        for (String basePath : basePaths) {
            for (String fontDir : fontDirs) {
                String path = basePath + fontDir + "pixel_kr.ttf";
                File fontFile = new File(path);
                if (fontFile.exists()) {
                    try {
                        Font loadedFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                        pixelFont = loadedFont.deriveFont(Font.BOLD, 20f);
                        smallPixelFont = loadedFont.deriveFont(Font.PLAIN, 20f);
                        koreanFontLoaded = true;
                        break;
                    } catch (FontFormatException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (koreanFontLoaded) break;
        }
    }
    
    /**
     * 제목용 폰트 반환
     */
    public static Font getTitleFont() {
        return titleFont;
    }
    
    /**
     * 일반 텍스트용 폰트 반환
     */
    public static Font getPixelFont() {
        return pixelFont;
    }
    
    /**
     * 작은 텍스트용 폰트 반환
     */
    public static Font getSmallPixelFont() {
        return smallPixelFont;
    }
    
    /**
     * 화면 크기 계산
     * @return 화면 크기
     */
    public static Dimension getScreenSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * SCREEN_WIDTH_PERCENT);
        int height = (int)(screenSize.height * SCREEN_HEIGHT_PERCENT);
        return new Dimension(width, height);
    }
    
    /**
     * 타이틀 폰트 크기를 화면 크기에 맞게 조정
     * @param width 화면 너비
     * @return 조정된 폰트
     */
    public static Font getScaledTitleFont(int width) {
        int titleSize = Math.max(30, width / 25);
        if (titleFont != null) {
            return titleFont.deriveFont(Font.BOLD, titleSize);
        } else {
            return new Font("맑은 고딕", Font.BOLD, titleSize);
        }
    }
    
    /**
     * 일반 폰트 크기를 화면 크기에 맞게 조정
     * @param width 화면 너비
     * @return 조정된 폰트
     */
    public static Font getScaledPixelFont(int width) {
        int normalSize = Math.max(16, width / 40);
        if (pixelFont != null) {
            return pixelFont.deriveFont(Font.BOLD, normalSize);
        } else {
            return new Font("맑은 고딕", Font.BOLD, normalSize);
        }
    }
} 