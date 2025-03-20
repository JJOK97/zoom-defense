package ui;

import java.awt.BorderLayout;
import java.awt.Color;
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

import contorller.UserController;
import ui.components.common.UIConstants;
import ui.components.common.PixelBackgroundPanel;
import ui.components.common.PixelButton;
import ui.components.common.PixelLabel;
import ui.components.common.PixelPasswordField;
import ui.components.common.PixelTextField;

/**
 * 회원가입 화면을 구현한 UI 클래스 (창 상태 유지 기능 추가)
 */
public class SignUpFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // 컨트롤러
    private UserController userController;
    
    // 회원가입 패널 컴포넌트
    private PixelTextField txtRegisterId;
    private PixelPasswordField txtRegisterPassword;
    private PixelPasswordField txtConfirmPassword;
    private PixelTextField txtNickname;
    
    // 메인 패널
    private JPanel mainPanel;
    
    // 버튼
    private PixelButton btnRegister;
    private PixelButton btnGoBack;
    
    // 애니메이션 관련 변수
    private Timer animationTimer;
    private int titleBounce = 0;
    private boolean bounceUp = false;
    private int colorPhase = 0;
    private PixelLabel lblLogo;
    private JLabel lblSubtitle;
    
    // 창 상태 유지 관련 변수
    private static Rectangle frameBounds = null;
    private static boolean isMaximized = false;
    
    /**
     * 기본 생성자
     */
    public SignUpFrame() {
        this(null, false);
    }
    
    /**
     * 창 상태를 유지하는 생성자
     * 
     * @param bounds 이전 창의 경계값
     * @param maximized 이전 창의 최대화 상태
     */
    public SignUpFrame(Rectangle bounds, boolean maximized) {
        userController = new UserController();
        
        setTitle("ZOOM Defense - 회원가입");
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
        mainPanel = createMainPanel();
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
        
        // 타이틀 폰트 크기 조정
        if (lblLogo != null) {
            lblLogo.setFont(UIConstants.getScaledTitleFont(width));
        }
        
        // 서브 타이틀 폰트 크기 조정
        if (lblSubtitle != null) {
            lblSubtitle.setFont(UIConstants.getScaledPixelFont(width));
        }
        
        // 버튼 크기 조정
        if (btnRegister != null && btnGoBack != null) {
            int buttonWidth = Math.max(150, width / 6);
            int buttonHeight = Math.max(40, height / 15);
            btnRegister.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            btnGoBack.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        }
        
        // InputBox 크기 조정
        if (txtRegisterId != null && txtRegisterPassword != null && 
            txtConfirmPassword != null && txtNickname != null) {
            
            int textFieldWidth = Math.max(200, width / 5);
            int textFieldHeight = Math.max(25, height / 30);
            
            Dimension textFieldSize = new Dimension(textFieldWidth, textFieldHeight);
            txtRegisterId.setPreferredSize(textFieldSize);
            txtRegisterPassword.setPreferredSize(textFieldSize);
            txtConfirmPassword.setPreferredSize(textFieldSize);
            txtNickname.setPreferredSize(textFieldSize);
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
        
        // 상단 여백 설정
        int topMargin = (int)(getHeight() * 0.25);
        titlePanel.setBorder(new EmptyBorder(topMargin, 0, 0, 0));
        
        lblLogo = new PixelLabel("ZOOM DEFENSE", SwingConstants.CENTER);
        lblLogo.setFont(UIConstants.getTitleFont());
        lblLogo.setForeground(UIConstants.GOLD_COLOR);
        
        lblSubtitle = new PixelLabel("회원가입", SwingConstants.CENTER);
        lblSubtitle.setFont(UIConstants.getPixelFont());
        lblSubtitle.setForeground(UIConstants.WHITE_COLOR);
        lblSubtitle.setBorder(new EmptyBorder(25, 0, 0, 0)); // 서브타이틀 상단 여백
        
        titlePanel.add(lblLogo, BorderLayout.CENTER);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);
        
        // 중앙 패널 - 전체 콘텐츠를 담는 메인 컨테이너
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout()); // GridBagLayout으로 변경하여 중앙 정렬
        centerPanel.setOpaque(false);
        
        // 컨텐츠 패널 (입력 필드 + 버튼)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // 입력 폼 패널
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(0, 0, 30, 0)); // 아래 버튼과의 간격 설정
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3); // MainGameFrame과 동일한 간격
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 아이디 입력
        JLabel lblId = new PixelLabel("아이디:", SwingConstants.RIGHT);
        lblId.setFont(UIConstants.getPixelFont());
        lblId.setForeground(UIConstants.WHITE_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblId, gbc);
        
        int textFieldWidth = Math.max(200, getWidth() / 5);
        int textFieldHeight = Math.max(25, getHeight() / 20);
        
        txtRegisterId = new PixelTextField(30, UIConstants.getPixelFont());
        txtRegisterId.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(txtRegisterId, gbc);
        
        // 비밀번호 입력
        JLabel lblPassword = new PixelLabel("비밀번호:", SwingConstants.RIGHT);
        lblPassword.setFont(UIConstants.getPixelFont());
        lblPassword.setForeground(UIConstants.WHITE_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblPassword, gbc);
        
        txtRegisterPassword = new PixelPasswordField(30, UIConstants.getPixelFont());
        txtRegisterPassword.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(txtRegisterPassword, gbc);
        
        // 비밀번호 확인
        JLabel lblConfirmPassword = new PixelLabel("비밀번호 확인:", SwingConstants.RIGHT);
        lblConfirmPassword.setFont(UIConstants.getPixelFont());
        lblConfirmPassword.setForeground(UIConstants.WHITE_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblConfirmPassword, gbc);
        
        txtConfirmPassword = new PixelPasswordField(30, UIConstants.getPixelFont());
        txtConfirmPassword.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(txtConfirmPassword, gbc);
        
        // 닉네임 입력
        JLabel lblNickname = new PixelLabel("닉네임:", SwingConstants.RIGHT);
        lblNickname.setFont(UIConstants.getPixelFont());
        lblNickname.setForeground(UIConstants.WHITE_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblNickname, gbc);
        
        txtNickname = new PixelTextField(30, UIConstants.getPixelFont());
        txtNickname.setPreferredSize(new Dimension(textFieldWidth, textFieldHeight));
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(txtNickname, gbc);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        
        int buttonWidth = Math.max(150, getWidth() / 6);
        int buttonHeight = Math.max(40, getHeight() / 15);
        
        btnRegister = new PixelButton("회원가입", UIConstants.getPixelFont());
        btnRegister.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        
        btnGoBack = new PixelButton("돌아가기", UIConstants.getPixelFont());
        btnGoBack.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        
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
                    JOptionPane.showMessageDialog(SignUpFrame.this, 
                            "모든 필드를 입력해주세요.", 
                            "회원가입 오류", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(SignUpFrame.this, 
                            "비밀번호가 일치하지 않습니다.", 
                            "회원가입 오류", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 중복 확인 (중복 확인 버튼은 제거했지만 회원가입 시 자동으로 체크)
                boolean isDuplicated = userController.checkDuplicateId(loginId);
                if (isDuplicated) {
                    JOptionPane.showMessageDialog(SignUpFrame.this, 
                            "이미 사용 중인 아이디입니다.", 
                            "회원가입 오류", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 회원가입 처리
                boolean success = userController.processRegister(loginId, password, nickname);
                
                if (success) {
                    JOptionPane.showMessageDialog(SignUpFrame.this, 
                            "회원가입이 완료되었습니다. 로그인해주세요.", 
                            "회원가입 성공", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // 입력 필드 초기화
                    txtRegisterId.setText("");
                    txtRegisterPassword.setText("");
                    txtConfirmPassword.setText("");
                    txtNickname.setText("");
                    
                    // 현재 창의 상태 저장
                    boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
                    Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();
                    
                    // 메인 화면으로 돌아가기 (창 상태 유지)
                    dispose();
                    MainGameFrame mainFrame = new MainGameFrame(currentBounds, currentMaximized);
                    mainFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(SignUpFrame.this, 
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
                // 현재 창의 상태 저장
                boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
                Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();
                
                // 메인 화면으로 돌아가기 (창 상태 유지)
                dispose();
                MainGameFrame mainFrame = new MainGameFrame(currentBounds, currentMaximized);
                mainFrame.setVisible(true);
            }
        });
        
        // 버튼 패널에 버튼 추가 - MainGameFrame과 동일한 방식으로
        buttonPanel.add(btnGoBack);
        buttonPanel.add(btnRegister);
        
        // 콘텐츠 패널에 로그인 패널과 버튼 패널 추가
        contentPanel.add(formPanel, BorderLayout.CENTER);
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
                
                // 타이틀 애니메이션 업데이트 - MainGameFrame과 동일한 방식 사용
                if (lblLogo instanceof PixelLabel) {
                    try {
                        // updateAnimation 메소드가 존재한다면 호출
                        ((PixelLabel)lblLogo).updateAnimation(titleBounce, colorPhase);
                    } catch (Exception ex) {
                        // updateAnimation 메소드가 없는 경우 대체 애니메이션 로직 사용
                        float hue = colorPhase / 360.0f;
                        Color color = Color.getHSBColor(0.14f, 0.9f - (0.2f * (float)Math.sin(hue * Math.PI)), 1.0f);
                        
                        lblLogo.setForeground(color);
                        lblLogo.setBounds(
                            lblLogo.getX(),
                            lblLogo.getY() + titleBounce,
                            lblLogo.getWidth(),
                            lblLogo.getHeight()
                        );
                        lblLogo.repaint();
                    }
                }
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
                new SignUpFrame().setVisible(true);
            }
        });
    }
}