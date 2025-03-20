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

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import model.Session;
import model.User;
import ui.components.GameMapPanel;
import ui.components.ResourcePanel;
import ui.components.TowerSelectionPanel;
import ui.components.WaveInfoPanel;
import ui.components.common.PixelBackgroundPanel;
import ui.components.common.UIConstants;

/**
 * ZOOM Defense 게임 룸 화면 구현 클래스
 */
public class GameRoomFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // 게임 컴포넌트
    private GameMapPanel gameMapPanel;
    private ResourcePanel resourcePanel;
    private TowerSelectionPanel towerSelectionPanel;
    private WaveInfoPanel waveInfoPanel;
    
    // 애니메이션 관련 변수
    private Timer gameTimer;
    
    // 창 상태 유지 관련 변수
    private static Rectangle frameBounds = null;
    private static boolean isMaximized = false;
    
    // 사용자 및 세션 정보
    private User loggedInUser;
    private Session gameSession;
    
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 창 크기가 설정되지 않은 경우 기본 크기 설정
        if (getBounds().width == 0 || getBounds().height == 0) {
            setSize(UIConstants.getScreenSize());
            setLocationRelativeTo(null);
        }
        
        setResizable(true);
        
        // 메인 패널 설정
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
        
        // 게임 타이머 시작
        startGameTimer();
        
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
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.setOpaque(false);
        
        // 자원 정보 패널 (생명력, 돈)
        resourcePanel = new ResourcePanel(gameSession.getLife(), gameSession.getMoney());
        resourcePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 웨이브 정보 패널
        waveInfoPanel = new WaveInfoPanel(gameSession.getWave());
        waveInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        topPanel.add(resourcePanel);
        topPanel.add(waveInfoPanel);
        
        // 중앙 패널 - 게임 맵
        gameMapPanel = new GameMapPanel();
        gameMapPanel.setBackground(new Color(50, 50, 50));
        gameMapPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        
        // 우측 사이드 패널 - 타워 선택
        towerSelectionPanel = new TowerSelectionPanel();
        towerSelectionPanel.setBackground(new Color(40, 40, 40, 200));
        towerSelectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 메인 패널에 배치
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(gameMapPanel, BorderLayout.CENTER);
        panel.add(towerSelectionPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * 창 크기에 따라 컴포넌트 크기 조정
     */
    private void adjustComponentSizes() {
        int width = getWidth();
        int height = getHeight();
        
        // 게임 맵 크기 조정
        if (gameMapPanel != null) {
            // 게임 맵이 전체 화면의 70%를 차지하도록 설정
            gameMapPanel.adjustSize(width, height);
        }
        
        // 타워 선택 패널 크기 조정
        if (towerSelectionPanel != null) {
            // 타워 패널의 너비는 전체 화면의 20%
            int towerPanelWidth = Math.max(200, width / 5);
            towerSelectionPanel.setPreferredSize(new Dimension(towerPanelWidth, height));
            towerSelectionPanel.adjustSize(width, height);
        }
        
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
     * 게임 타이머 시작
     * - 게임 루프, 자원 관리, 적 이동 등을 관리
     */
    private void startGameTimer() {
        gameTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 게임 상태 업데이트
                updateGameState();
            }
        });
        gameTimer.start();
    }
    
    /**
     * 게임 상태 업데이트
     */
    private void updateGameState() {
        // 업데이트 수행
        if (gameMapPanel != null) {
            gameMapPanel.updateGameState();
        }
        
        // 자원 정보 업데이트
        if (resourcePanel != null) {
            resourcePanel.updateResources(gameSession.getLife(), gameSession.getMoney());
        }
        
        // 웨이브 정보 업데이트
        if (waveInfoPanel != null) {
            waveInfoPanel.updateWaveInfo(gameSession.getWave());
        }
    }
    
    /**
     * 게임 종료 처리
     */
    public void endGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // 현재 창의 상태 저장
        boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
        Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();
        
        // 게임 결과 화면으로 이동 또는 메인 메뉴로 돌아가기
        dispose();
        GameSelectionFrame selectionFrame = new GameSelectionFrame(loggedInUser);
        selectionFrame.setVisible(true);
    }
} 