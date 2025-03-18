import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ui.LoginFrame;

/**
 * ZOOM Defense 게임의 메인 애플리케이션 클래스
 */
public class ZoomDefenseApp {
    
    /**
     * 프로그램의 진입점
     * @param args 명령행 인수
     */
    public static void main(String[] args) {
        try {
            // 시스템 룩앤필 적용
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("룩앤필 설정 실패: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 회원가입 화면 표시
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                
                System.out.println("ZOOM Defense 회원가입 화면이 시작되었습니다.");
            }
        });
    }
} 