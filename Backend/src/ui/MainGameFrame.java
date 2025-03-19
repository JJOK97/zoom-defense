package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import ui.common.UIConstants;
import ui.components.PixelBackgroundPanel;
import ui.components.PixelButton;
import ui.components.PixelLabel;
import ui.components.PixelPasswordField;
import ui.components.PixelTextField;

/**
 * 게임 시작 화면을 구현한 UI 클래스 (창 상태 유지 기능 추가)
 */
public class MainGameFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // 컴포넌트
    private PixelTextField txtLoginId;
    private PixelPasswordField txtPassword;
    private PixelButton btnLogin;
    private PixelButton btnRegister;
    private PixelLabel lblTitle;
    
    // 애니메이션 관련 변수
    private Timer animationTimer;
    private int titleBounce = 0;
    private boolean bounceUp = false;
    private int colorPhase = 0;
    
    // 창 상태 유지 관련 변수
    private static Rectangle frameBounds = null;
    private static boolean isMaximized = false;
    
    /**
     * 기본 생성자
     */
    public MainGameFrame() {
        this(null, false);
    }
    
    /**
     * 창 상태를 유지하는 생성자
     * 
     * @param bounds 이전 창의 경계값
     * @param maximized 이전 창의 최대화 상태
     */
    public MainGameFrame(Rectangle bounds, boolean maximized) {
        
        setTitle("ZOOM Defense");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 이전 창 크기 정보가 없는 경우 기본 크기 설정
        if (bounds == null) {
            setSize(UIConstants.getScreenSize());
            setLocationRelativeTo(null);
        } else {
            // 이전 창의 경계값 설정
            setBounds(bounds);
        }
        
        // 최대화 상태 적용
        if (maximized) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        
        setResizable(true);
        
        // 메인 패널 설정
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
        
        // 애니메이션 타이머 시작
        startAnimationTimer();
        
        // 창 크기 변경 시 컴포넌트 크기 조정
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponentSizes();
                
                // 창 상태 저장
                isMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
                if (!isMaximized) {
                    frameBounds = getBounds();
                }
            }
        });
    }
    
    /**
     * 창 크기에 따라 컴포넌트 크기 조정
     */
    private void adjustComponentSizes() {
        int width = getWidth();
        int height = getHeight();
        
        // 폰트 크기 조정
        if (lblTitle != null) {
            lblTitle.setFont(UIConstants.getScaledTitleFont(width));
        }
        
        // 버튼 크기 조정
        if (btnLogin != null && btnRegister != null) {
            int buttonWidth = Math.max(150, width / 6);
            int buttonHeight = Math.max(40, height / 15);
            btnLogin.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            btnRegister.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * 메인 패널 생성
     */
    private JPanel createMainPanel() {
        JPanel panel = new PixelBackgroundPanel();
        panel.setLayout(new BorderLayout());
        
        // 게임 타이틀 패널
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        // 상단 여백 설정 - 화면 높이의 10%로 감소
        int topMargin = (int)(getHeight() * 0.25);
        titlePanel.setBorder(new EmptyBorder(topMargin, 0, 0, 0));
        
        lblTitle = new PixelLabel("ZOOM DEFENSE", SwingConstants.CENTER);
        lblTitle.setFont(UIConstants.getTitleFont());
        lblTitle.setForeground(UIConstants.GOLD_COLOR);
        
        JLabel lblSubtitle = new PixelLabel("ZOOM 전파를 방해하는 적을 섬멸하라", SwingConstants.CENTER);
        lblSubtitle.setFont(UIConstants.getPixelFont());
        lblSubtitle.setForeground(UIConstants.WHITE_COLOR);
        lblSubtitle.setBorder(new EmptyBorder(25, 0, 0, 0)); // 서브타이틀 상단 여백 감소
        
        titlePanel.add(lblTitle, BorderLayout.CENTER);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);
        
        // 중앙 패널 - 전체 콘텐츠를 담는 메인 컨테이너
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout()); // GridBagLayout으로 변경하여 중앙 정렬
        centerPanel.setOpaque(false);
        
        // 컨텐츠 패널 (로그인 + 버튼)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // 로그인 패널
        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(new EmptyBorder(0, 0, 50, 0)); // 로그인 패널과 버튼 사이 간격 줄임
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3); // 각 컴포넌트 간 간격 줄임
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 로그인 레이블
        JLabel lblId = new PixelLabel("아이디:", SwingConstants.RIGHT);
        lblId.setFont(UIConstants.getPixelFont());
        lblId.setForeground(UIConstants.WHITE_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(lblId, gbc);
        
        // 아이디 입력 필드
        txtLoginId = new PixelTextField(30, UIConstants.getPixelFont());
        gbc.gridx = 1;
        gbc.gridy = 0;
        loginPanel.add(txtLoginId, gbc);
        
        // 비밀번호 레이블
        JLabel lblPassword = new PixelLabel("비밀번호:", SwingConstants.RIGHT);
        lblPassword.setFont(UIConstants.getPixelFont());
        lblPassword.setForeground(UIConstants.WHITE_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(lblPassword, gbc);
        
        // 비밀번호 입력 필드
        txtPassword = new PixelPasswordField(30, UIConstants.getPixelFont());
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(txtPassword, gbc);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        
        // 로그인 버튼
        btnLogin = new PixelButton("로그인", UIConstants.getPixelFont());
        int buttonWidth = (int)(getWidth() * 0.15);
        int buttonHeight = (int)(getHeight() * 0.06); // 버튼 높이 약간 감소
        btnLogin.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String loginId = txtLoginId.getText();
                String password = new String(txtPassword.getPassword());
                
                // 입력 검증
                if (loginId.trim().isEmpty() || password.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(MainGameFrame.this, 
                            "아이디와 비밀번호를 입력해주세요.", 
                            "로그인 오류", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 임시 로그인 처리 (실제 컨트롤러 구현 전 하드코딩)
                boolean loginSuccess = false;
                
                // 하드코딩된 계정 정보로 로그인 처리
                if (loginId.equals("JSO") && password.equals("1234")) {
                    loginSuccess = true;
                }
                
                if (loginSuccess) {
                    // 현재 창의 상태 저장
                    boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
                    Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();
                    
                    // 게임 선택 화면으로 이동 (창 상태 유지)
                    dispose();
                    GameSelectionFrame gameSelectionFrame = new GameSelectionFrame(loginId, currentBounds, currentMaximized);
                    gameSelectionFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(MainGameFrame.this, 
                            "아이디 또는 비밀번호가 올바르지 않습니다.", 
                            "로그인 실패", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // 회원가입 버튼
        btnRegister = new PixelButton("회원가입", UIConstants.getPixelFont());
        btnRegister.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 현재 창의 상태 저장
                boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
                Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();
                
                // 회원가입 화면으로 이동 (창 상태 유지)
                SignUpFrame signUpFrame = new SignUpFrame(currentBounds, currentMaximized);
                signUpFrame.setVisible(true);
                setVisible(false); // 현재 창은 숨기기
            }
        });
        
        // 버튼 순서 변경 - 로그인, 회원가입 순서로
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnLogin);
        
        // 콘텐츠 패널에 로그인 패널과 버튼 패널 추가
        contentPanel.add(loginPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 중앙 패널에 컨텐츠 패널 추가 (가운데 정렬)
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.gridy = 0;
        centerGbc.weightx = 1.0;
        centerGbc.weighty = 1.0;
        centerGbc.fill = GridBagConstraints.NONE; // 채우지 않고 크기 그대로 유지
        centerGbc.anchor = GridBagConstraints.CENTER; // 가운데 정렬
        centerPanel.add(contentPanel, centerGbc);
        
        // 메인 패널에 배치
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 애니메이션 타이머 시작
     */
    private void startAnimationTimer() {
        animationTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 타이틀 바운스 애니메이션
                if (bounceUp) {
                    titleBounce -= 1;
                    if (titleBounce <= -5) {
                        bounceUp = false;
                    }
                } else {
                    titleBounce += 1;
                    if (titleBounce >= 5) {
                        bounceUp = true;
                    }
                }
                
                // 색상 변화 애니메이션
                colorPhase = (colorPhase + 1) % 360;
                
                // 타이틀 애니메이션 업데이트
                ((PixelLabel)lblTitle).updateAnimation(titleBounce, colorPhase);
            }
        });
        animationTimer.start();
    }
    
    /**
     * 애플리케이션 실행
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainGameFrame().setVisible(true);
            }
        });
    }
}