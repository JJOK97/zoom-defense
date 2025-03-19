package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import contorller.UserController;
import ui.components.PixelBackgroundPanel;
import ui.components.PixelButton;
import ui.components.PixelLabel;
import ui.components.PixelPasswordField;
import ui.components.PixelTextField;

/**
 * 게임 시작 화면을 구현한 UI 클래스
 */
public class MainGameFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // 컨트롤러
    private UserController userController;
    
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
    
    // 픽셀 폰트
    private Font pixelFont;
    private Font smallPixelFont;
    private Font titleFont;
    
    // 화면 비율 관련 상수
    private static final double SCREEN_WIDTH_PERCENT = 0.8;  // 화면 너비의 75%
    private static final double SCREEN_HEIGHT_PERCENT = 0.8;  // 화면 높이의 80%
    
    /**
     * 생성자
     */
    public MainGameFrame() {
        userController = new UserController();
        
        setTitle("ZOOM Defense");
        initializeFrameSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // 픽셀 폰트 로드 시도
        loadPixelFont();
        
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
            }
        });
    }
    
    /**
     * 화면 크기를 화면 비율에 맞게 초기화
     */
    private void initializeFrameSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * SCREEN_WIDTH_PERCENT);
        int height = (int)(screenSize.height * SCREEN_HEIGHT_PERCENT);
        setSize(width, height);
    }
    
    /**
     * 창 크기에 따라 컴포넌트 크기 조정
     */
    private void adjustComponentSizes() {
        int width = getWidth();
        int height = getHeight();
        
        // 폰트 크기 조정
        int titleSize = Math.max(30, width / 25);
        int normalSize = Math.max(16, width / 40);
        int smallSize = Math.max(14, width / 50);
        
        // 타이틀 폰트 크기 조정
        if (lblTitle != null) {
            try {
                // 여기서는 이미 로드된 폰트 객체를 사용하되 크기만 조정
                if (titleFont != null && titleFont.getName() != "맑은 고딕") {
                    Font newTitleFont = titleFont.deriveFont(Font.BOLD, titleSize);
                    lblTitle.setFont(newTitleFont);
                } else {
                    lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, titleSize));
                }
            } catch (Exception e) {
                lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, titleSize));
            }
        }
        
        // 버튼 크기 조정
        if (btnLogin != null && btnRegister != null) {
            int buttonWidth = Math.max(150, width / 6);
            int buttonHeight = Math.max(40, height / 15);
            btnLogin.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            btnRegister.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        }
        
        // 텍스트 필드 크기는 GridBag을 통해 자동으로 조정되므로 별도 설정 불필요
        
        revalidate();
        repaint();
    }
    
    /**
     * 픽셀 폰트 로드 메서드
     */
    private void loadPixelFont() {
        // 기본 폰트 초기화 - 한글 지원 폰트 사용
        pixelFont = new Font("맑은 고딕", Font.BOLD, 16);
        smallPixelFont = new Font("맑은 고딕", Font.PLAIN, 14);
        titleFont = new Font("맑은 고딕", Font.BOLD, 40);
        
        try {
            // 영문용 픽셀 폰트 로드 시도
            File engFontFile = new File("src/ui/fonts/pixel.ttf");
            
            if (engFontFile.exists()) {
                try {
                    Font pixelFontEnglish = Font.createFont(Font.TRUETYPE_FONT, engFontFile);
                    pixelFontEnglish = pixelFontEnglish.deriveFont(Font.BOLD, 50f);
                    titleFont = pixelFontEnglish;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 다른 경로 시도
                engFontFile = new File("Backend/src/ui/fonts/pixel.ttf");
                
                if (engFontFile.exists()) {
                    try {
                        Font pixelFontEnglish = Font.createFont(Font.TRUETYPE_FONT, engFontFile);
                        pixelFontEnglish = pixelFontEnglish.deriveFont(Font.BOLD, 50f);
                        titleFont = pixelFontEnglish;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            
            // 한글용 픽셀 폰트 로드 시도
            File korFontFile = new File("src/ui/fonts/pixel_kr.ttf");
            
            if (korFontFile.exists()) {
                try {
                    Font pixelFontKorean = Font.createFont(Font.TRUETYPE_FONT, korFontFile);
                    pixelFont = pixelFontKorean.deriveFont(Font.BOLD, 18f);
                    smallPixelFont = pixelFontKorean.deriveFont(Font.PLAIN, 16f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 다른 경로 시도
                korFontFile = new File("Backend/src/ui/fonts/pixel_kr.ttf");
                
                if (korFontFile.exists()) {
                    try {
                        Font pixelFontKorean = Font.createFont(Font.TRUETYPE_FONT, korFontFile);
                        pixelFont = pixelFontKorean.deriveFont(Font.BOLD, 18f);
                        smallPixelFont = pixelFontKorean.deriveFont(Font.PLAIN, 16f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        lblTitle.setFont(titleFont);  // 픽셀 폰트 적용
        lblTitle.setForeground(new Color(255, 215, 0)); // 골드 색상
        
        JLabel lblSubtitle = new PixelLabel("ZOOM 전파를 방해하는 적을 섬멸하라", SwingConstants.CENTER);
        lblSubtitle.setFont(pixelFont);  // 한글 지원 폰트 사용
        lblSubtitle.setForeground(Color.WHITE);
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
        lblId.setFont(pixelFont);
        lblId.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(lblId, gbc);
        
        // 아이디 입력 필드
        txtLoginId = new PixelTextField(40, pixelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        loginPanel.add(txtLoginId, gbc);
        
        // 비밀번호 레이블
        JLabel lblPassword = new PixelLabel("비밀번호:", SwingConstants.RIGHT);
        lblPassword.setFont(pixelFont);
        lblPassword.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(lblPassword, gbc);
        
        // 비밀번호 입력 필드
        txtPassword = new PixelPasswordField(40, pixelFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(txtPassword, gbc);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        
        // 로그인 버튼
        btnLogin = new PixelButton("로그인", pixelFont);
        int buttonWidth = (int)(getWidth() * 0.15);
        int buttonHeight = (int)(getHeight() * 0.06); // 버튼 높이 약간 감소
        btnLogin.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 로그인 기능 구현 예정
                JOptionPane.showMessageDialog(MainGameFrame.this, 
                        "로그인 기능은 아직 구현되지 않았습니다.", 
                        "알림", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // 회원가입 버튼
        btnRegister = new PixelButton("회원가입", pixelFont);
        btnRegister.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 회원가입 화면으로 이동 - 새 창이 아닌 현재 창에서 화면 전환
                dispose(); // 현재 창 닫기
                LoginFrame loginFrame = new LoginFrame(true); // true 파라미터로 회원가입 모드 활성화
                loginFrame.setVisible(true);
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