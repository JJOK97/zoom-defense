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

import contorller.UserController;
import model.User;

/**
 * 회원가입 화면을 구현한 UI 클래스
 */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // 카드 레이아웃 패널 상수
    private static final String LOGIN_PANEL = "LOGIN_PANEL";
    private static final String REGISTER_PANEL = "REGISTER_PANEL";
    
    // 컨트롤러
    private UserController userController;
    
    // 로그인 패널 컴포넌트
    private JTextField txtLoginId;
    private JPasswordField txtLoginPassword;
    
    // 회원가입 패널 컴포넌트
    private JTextField txtRegisterId;
    private JPasswordField txtRegisterPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtNickname;
    
    // 메인 패널 (카드 레이아웃)
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    /**
     * 생성자
     */
    public LoginFrame() {
        userController = new UserController();
        
        setTitle("ZOOM Defense - 회원가입");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 카드 레이아웃 설정
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // 로그인 패널 생성
        cardPanel.add(createLoginPanel(), LOGIN_PANEL);
        
        // 회원가입 패널 생성
        cardPanel.add(createRegisterPanel(), REGISTER_PANEL);
        
        // 프레임에 카드 패널 추가
        add(cardPanel, BorderLayout.CENTER);
        
        // 초기 화면을 회원가입 화면으로 설정
        cardLayout.show(cardPanel, REGISTER_PANEL);
    }
    
    /**
     * 로그인 패널 생성
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // 로고 패널
        JPanel logoPanel = new JPanel();
        JLabel lblLogo = new JLabel("ZOOM Defense");
        lblLogo.setFont(lblLogo.getFont().deriveFont(24.0f));
        logoPanel.add(lblLogo);
        
        // 입력 필드 패널
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("아이디:"));
        txtLoginId = new JTextField();
        inputPanel.add(txtLoginId);
        inputPanel.add(new JLabel("비밀번호:"));
        txtLoginPassword = new JPasswordField();
        inputPanel.add(txtLoginPassword);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnLogin = new JButton("로그인");
        JButton btnGoRegister = new JButton("회원가입");
        
        // 로그인 버튼 이벤트 (로그인 기능은 아직 구현되지 않음)
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this, 
                        "로그인 기능은 아직 구현되지 않았습니다.", 
                        "알림", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // 회원가입 화면으로 이동 버튼 이벤트
        btnGoRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, REGISTER_PANEL);
                setTitle("ZOOM Defense - 회원가입");
            }
        });
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnGoRegister);
        
        // 각 패널을 메인 패널에 배치
        panel.add(logoPanel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * 회원가입 패널 생성
     */
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // 로고 패널
        JPanel logoPanel = new JPanel();
        JLabel lblLogo = new JLabel("ZOOM Defense 회원가입");
        lblLogo.setFont(lblLogo.getFont().deriveFont(20.0f));
        logoPanel.add(lblLogo);
        
        // 입력 필드 패널
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(new JLabel("아이디:"));
        txtRegisterId = new JTextField();
        inputPanel.add(txtRegisterId);
        
        inputPanel.add(new JLabel("비밀번호:"));
        txtRegisterPassword = new JPasswordField();
        inputPanel.add(txtRegisterPassword);
        
        inputPanel.add(new JLabel("비밀번호 확인:"));
        txtConfirmPassword = new JPasswordField();
        inputPanel.add(txtConfirmPassword);
        
        inputPanel.add(new JLabel("닉네임:"));
        txtNickname = new JTextField();
        inputPanel.add(txtNickname);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnCheckId = new JButton("중복확인");
        JButton btnRegister = new JButton("회원가입");
        JButton btnGoBack = new JButton("돌아가기");
        
        // 아이디 중복 확인 버튼 이벤트
        btnCheckId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String loginId = txtRegisterId.getText();
                
                if (loginId.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                            "아이디를 입력해주세요.", 
                            "중복 확인", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                boolean isDuplicated = userController.checkDuplicateId(loginId);
                
                if (isDuplicated) {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                            "이미 사용 중인 아이디입니다.", 
                            "중복 확인", 
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                            "사용 가능한 아이디입니다.", 
                            "중복 확인", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        // 회원가입 버튼 이벤트
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String loginId = txtRegisterId.getText();
                String password = new String(txtRegisterPassword.getPassword());
                String confirmPassword = new String(txtConfirmPassword.getPassword());
                String nickname = txtNickname.getText();
                
                // 유효성 검사
                if (loginId.trim().isEmpty() || password.trim().isEmpty() || 
                        confirmPassword.trim().isEmpty() || nickname.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                            "모든 필드를 입력해주세요.", 
                            "회원가입 오류", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                            "비밀번호가 일치하지 않습니다.", 
                            "회원가입 오류", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 중복 확인
                boolean isDuplicated = userController.checkDuplicateId(loginId);
                if (isDuplicated) {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                            "이미 사용 중인 아이디입니다.", 
                            "회원가입 오류", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 회원가입 처리
                boolean success = userController.processRegister(loginId, password, nickname);
                
                if (success) {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                            "회원가입이 완료되었습니다. 로그인해주세요.", 
                            "회원가입 성공", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // 입력 필드 초기화
                    txtRegisterId.setText("");
                    txtRegisterPassword.setText("");
                    txtConfirmPassword.setText("");
                    txtNickname.setText("");
                    
                    // 로그인 화면으로 이동
                    cardLayout.show(cardPanel, LOGIN_PANEL);
                    setTitle("ZOOM Defense - 로그인");
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, 
                            "회원가입 중 오류가 발생했습니다.", 
                            "회원가입 실패", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // 로그인 화면으로 돌아가기 버튼 이벤트
        btnGoBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, LOGIN_PANEL);
                setTitle("ZOOM Defense - 로그인");
            }
        });
        
        buttonPanel.add(btnCheckId);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnGoBack);
        
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
                new LoginFrame().setVisible(true);
            }
        });
    }
} 