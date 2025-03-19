package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import contorller.SessionController;
import model.Session;

/**
 * 게임 세션 테스트 화면을 구현한 UI 클래스
 */
public class SessionTest extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // 카드 레이아웃 패널 상수
    private static final String GAME_PANEL = "GAME_PANEL";
    
    // 컨트롤러
    private SessionController sessionController;
    
    // 게임 세션 관련 UI 컴포넌트
    private JTextField txtUserId;
    private JTextField txtInitialMoney;
    private JTextField txtInitialLife;
    
    // 메인 패널 (카드 레이아웃)
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    /**
     * 생성자
     */
    public SessionTest() {
        sessionController = new SessionController();
        
        setTitle("ZOOM Defense - 게임 세션 생성");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 카드 레이아웃 설정
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // 게임 세션 생성 패널 생성
        cardPanel.add(createGamePanel(), GAME_PANEL);
        
        // 프레임에 카드 패널 추가
        add(cardPanel, BorderLayout.CENTER);
        
        // 초기 화면을 게임 세션 생성 화면으로 설정
        cardLayout.show(cardPanel, GAME_PANEL);
    }
    
    /**
     * 게임 세션 생성 패널 생성
     */
    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // 로고 패널
        JPanel logoPanel = new JPanel();
        JLabel lblLogo = new JLabel("ZOOM Defense - 게임 시작");
        lblLogo.setFont(lblLogo.getFont().deriveFont(24.0f));
        logoPanel.add(lblLogo);
        
        // 입력 필드 패널
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("사용자 ID:"));
        txtUserId = new JTextField();
        inputPanel.add(txtUserId);
        
        inputPanel.add(new JLabel("초기 자금:"));
        txtInitialMoney = new JTextField("1000");
        inputPanel.add(txtInitialMoney);
        
        inputPanel.add(new JLabel("초기 생명력:"));
        txtInitialLife = new JTextField("100");
        inputPanel.add(txtInitialLife);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnCreateSession = new JButton("게임 세션 시작");
        
        // 게임 세션 생성 버튼 이벤트
        btnCreateSession.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 입력된 값으로 세션 생성
                    int userId = Integer.parseInt(txtUserId.getText());
                    int initialMoney = Integer.parseInt(txtInitialMoney.getText());
                    int initialLife = Integer.parseInt(txtInitialLife.getText());
                    
                    // 세션 생성
                    int sessionId = sessionController.createSession(userId, initialMoney, initialLife);
                    
                    // 세션 ID 출력
                    JOptionPane.showMessageDialog(SessionTest.this, 
                            "게임 세션이 생성되었습니다. 세션 ID: " + sessionId, 
                            "세션 생성 성공", 
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SessionTest.this, 
                            "잘못된 입력입니다. 숫자만 입력해주세요.", 
                            "입력 오류", 
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(btnCreateSession);
        
        // 각 패널을 메인 패널에 배치
        panel.add(logoPanel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * 애플리케이션 실행
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SessionTest().setVisible(true);
            }
        });
    }
}
