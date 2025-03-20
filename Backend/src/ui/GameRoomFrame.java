package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import contorller.SessionController;
import model.Session;
import model.User;
import ui.components.GameMapPanel;
import ui.components.ResourcePanel;
import ui.components.WaveInfoPanel;
import ui.components.common.PixelBackgroundPanel;
import ui.components.common.PixelButton;
import ui.components.common.UIConstants;

/**
 * ZOOM Defense 게임 룸 화면 구현 클래스
 */
public class GameRoomFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // 게임 컴포넌트
    private GameMapPanel gameMapPanel;
    private ResourcePanel resourcePanel;
    private WaveInfoPanel waveInfoPanel;
    private PixelButton btnPause;
    private PixelButton btnSave;
    private PixelButton btnExit;
    
    // 애니메이션 관련 변수
    private Timer gameTimer;
    
    // 창 상태 유지 관련 변수
    private static Rectangle frameBounds = null;
    private static boolean isMaximized = false;
    
    // 사용자 및 세션 정보
    private User loggedInUser;
    private Session gameSession;
    
    // 게임 상태 변수
    private boolean isPaused = false;
    
    /**
     * 새 게임 시작하는 생성자
     * @param user 로그인된 사용자 정보
     * @param session 게임 세션 정보
     */
    public GameRoomFrame(User user, Session session) {
        this.loggedInUser = user;
        this.gameSession = session;
        
        initialize();
    }
    
    /**
     * 저장된 게임을 불러오는 생성자
     * @param user 로그인된 사용자 정보
     * @param session 불러온 게임 세션 정보
     * @param bounds 이전 창의 경계값
     * @param maximized 이전 창의 최대화 상태
     */
    public GameRoomFrame(User user, Session session, Rectangle bounds, boolean maximized) {
        this.loggedInUser = user;
        this.gameSession = session;
        
        // 이전 창 크기 정보 적용
        if (bounds != null) {
            setBounds(bounds);
        }
        
        // 최대화 상태 적용
        if (maximized) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        
        initialize();
    }
    
    /**
     * 프레임 초기화
     */
    private void initialize() {
        setTitle("ZOOM Defense - " + loggedInUser.getNickname() + "님의 게임");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // 창 닫기 버튼 처리
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExit();
            }
        });
        
        // ESC 키로 일시정지
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
            }
        });
        
        setFocusable(true);
        
        // 창 크기가 설정되지 않은 경우 기본 크기 설정
        if (getBounds().width == 0 || getBounds().height == 0) {
            setSize(UIConstants.getScreenSize());
            setLocationRelativeTo(null);
        }
        
        setResizable(true);
        
        // 메인 패널 설정
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
        
        // 게임맵에 초기 자금과 생명력 설정
        if (gameMapPanel != null) {
            gameMapPanel.setMoney(gameSession.getMoney());
            gameMapPanel.setLife(gameSession.getLife());
        }
        
        // 게임 업데이트 타이머 설정
        setupGameTimer();
        
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
     * 메인 패널 생성
     */
    private JPanel createMainPanel() {
        // 기본 패널
        JPanel panel = new PixelBackgroundPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // 상단 패널 - 자원 및 웨이브 정보
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        // 자원 및 웨이브 정보 패널
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        infoPanel.setOpaque(false);
        
        // 자원 정보 패널 (생명력, 돈)
        resourcePanel = new ResourcePanel(gameSession.getLife(), gameSession.getMoney());
        resourcePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 웨이브 정보 패널
        waveInfoPanel = new WaveInfoPanel(gameSession.getWave());
        waveInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        infoPanel.add(resourcePanel);
        infoPanel.add(waveInfoPanel);
        
        // 게임 컨트롤 버튼 패널
        JPanel controlPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 일시정지 버튼
        btnPause = new PixelButton("일시정지", UIConstants.getSmallPixelFont());
        btnPause.setBackground(new Color(60, 60, 100));
        btnPause.setBorderColor(new Color(100, 100, 180));
        btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });
        
        // 저장 버튼
        btnSave = new PixelButton("저장", UIConstants.getSmallPixelFont());
        btnSave.setBackground(new Color(60, 100, 60));
        btnSave.setBorderColor(new Color(100, 180, 100));
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGame();
            }
        });
        
        // 나가기 버튼
        btnExit = new PixelButton("나가기", UIConstants.getSmallPixelFont());
        btnExit.setBackground(new Color(100, 60, 60));
        btnExit.setBorderColor(new Color(180, 100, 100));
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmExit();
            }
        });
        
        controlPanel.add(btnPause);
        controlPanel.add(btnSave);
        controlPanel.add(btnExit);
        
        topPanel.add(infoPanel, BorderLayout.CENTER);
        topPanel.add(controlPanel, BorderLayout.EAST);
        
        // 중앙 패널 - 게임 맵을 담을 패널
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());
        
        // 게임 맵 생성 및 설정
        gameMapPanel = new GameMapPanel();
        gameMapPanel.setBackground(new Color(50, 50, 50));
        gameMapPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        
        // 게임 맵 크기 조정
        gameMapPanel.adjustSize();
        
        // 게임 맵을 중앙 패널에 추가 (가운데 정렬)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        centerPanel.add(gameMapPanel, gbc);
        
        // GameMapPanel과 WaveInfoPanel 연결
        gameMapPanel.setWaveInfoPanel(waveInfoPanel);
        
        // 메인 패널에 배치
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * 창 크기에 따라 컴포넌트 크기 조정
     */
    private void adjustComponentSizes() {
        int width = getWidth();
        int height = getHeight();
        
        // 게임맵 크기는 이미 고정되어 있으므로 추가 조정 불필요
        
        // 자원 및 웨이브 정보 패널 크기 조정
        if (resourcePanel != null && waveInfoPanel != null) {
            int infoPanelHeight = Math.max(70, height / 10);
            resourcePanel.setPreferredSize(new Dimension(width / 2, infoPanelHeight));
            waveInfoPanel.setPreferredSize(new Dimension(width / 2, infoPanelHeight));
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * 게임 일시정지/재개 토글
     */
    private void togglePause() {
        isPaused = !isPaused;
        
        if (isPaused) {
            gameTimer.stop();
            btnPause.setText("계속하기");
        } else {
            gameTimer.start();
            btnPause.setText("일시정지");
        }
    }
    
    /**
     * 현재 게임 상태 저장
     */
    private void saveGame() {
        try {
            // 게임 일시정지
            if (!isPaused) {
                togglePause();
            }
            
            // 현재 게임 상태 업데이트
            gameSession.setLife(gameMapPanel.getLife());
            gameSession.setMoney(gameMapPanel.getMoney());
            gameSession.setScore(gameSession.getScore());
            gameSession.setWave(waveInfoPanel.getCurrentWave());
            
            // 세션 컨트롤러로 게임 상태 저장
            SessionController sessionController = new SessionController();
            boolean success = sessionController.saveGameState(gameSession);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "게임이 저장되었습니다.", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "게임 저장에 실패했습니다.", "저장 실패", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "게임 저장 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * 게임 나가기 확인
     */
    private void confirmExit() {
        // 게임 일시정지
        if (!isPaused) {
            togglePause();
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
                "게임을 저장하시겠습니까?", 
                "게임 나가기", 
                JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            // 저장 후 나가기
            saveGame();
            exitGame();
        } else if (option == JOptionPane.NO_OPTION) {
            // 저장하지 않고 나가기
            exitGame();
        } else {
            // 취소 - 게임 계속
            if (isPaused) {
                togglePause();
            }
        }
    }
    
    /**
     * 게임 나가기
     */
    private void exitGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // 현재 창의 상태 저장
        boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
        Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();
        
        // 게임 선택 화면으로 이동
        dispose();
        GameSelectionFrame selectionFrame = new GameSelectionFrame(loggedInUser, currentBounds, currentMaximized);
        selectionFrame.setVisible(true);
    }
    
    /**
     * 게임 업데이트 타이머 설정
     */
    private void setupGameTimer() {
        // 60 FPS (약 16.67ms 간격)
        int delay = 1000 / 60; 
        
        gameTimer = new Timer(delay, e -> {
            // 게임 맵 업데이트
            if (gameMapPanel != null) {
                gameMapPanel.update();
            }
            
            // 게임 정보 업데이트 (필요한 경우)
            // ...
        });
        
        // 타이머 시작
        gameTimer.start();
    }
} 