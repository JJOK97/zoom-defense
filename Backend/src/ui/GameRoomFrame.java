package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import dao.TowerDAO;
import model.Session;
import model.TowerPlacement;
import model.User;
import service.SessionService;
import service.SessionServiceImpl;
import ui.components.GameMapPanel;
import ui.components.ResourcePanel;
import ui.components.TowerSelectionPanel;
import ui.components.WaveInfoPanel;
import ui.components.common.PixelBackgroundPanel;
import ui.components.common.PixelButton;
import ui.components.common.UIConstants;
import contorller.RankingController;
import ui.RankingFrame;

/**
 * ZOOM Defense 게임 룸 화면 구현 클래스
 */
public class GameRoomFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // 게임 컴포넌트
    private GameMapPanel gameMapPanel;
    private ResourcePanel resourcePanel;
    private WaveInfoPanel waveInfoPanel;
    private TowerSelectionPanel towerSelectionPanel;
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
        
        // 타워 비용 초기화
        service.TowerServiceImpl.resetTowerCost();
        
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
        
        // UI 컴포넌트 생성 및 배치
        setupUI();
        
        // 게임맵에 초기 자금과 생명력 설정
        if (gameMapPanel != null) {
            gameMapPanel.setMoney(gameSession.getMoney());
            gameMapPanel.setLife(gameSession.getLife());
        }
        
        // 게임 업데이트 타이머 설정
        setupGameTimer();
        
        // 게임 시작 (첫 웨이브 시작)
        if (gameMapPanel != null) {
            gameMapPanel.startWave();
        }
        
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
        
        // 게임 상태 로드
        loadGame();
    }
    
    /**
     * UI 컴포넌트 설정
     */
    private void setupUI() {
        // 기본 패널
        JPanel mainPanel = new PixelBackgroundPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // === 상단 패널 - 자원 및 웨이브 정보 ===
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
        JPanel controlPanel = createControlPanel();
        
        topPanel.add(infoPanel, BorderLayout.CENTER);
        topPanel.add(controlPanel, BorderLayout.EAST);
        
        // === 중앙 패널 - 게임 맵 ===
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BorderLayout());
        
        // 게임 맵 패널
        gameMapPanel = createGameMapPanel();
        
        // 게임 맵을 중앙에 배치
        JPanel mapWrapperPanel = new JPanel(new GridBagLayout());
        mapWrapperPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        mapWrapperPanel.add(gameMapPanel, gbc);
        
        centerPanel.add(mapWrapperPanel, BorderLayout.CENTER);
        
        // === 타워 관련 컴포넌트 ===
        
        // 타워 선택 패널 생성
        towerSelectionPanel = new TowerSelectionPanel();
        towerSelectionPanel.setGameMapPanel(gameMapPanel);
        
        // 타워 버튼 패널 (하단에 배치)
        JPanel towerButtonPanel = createTowerButtonPanel();
        centerPanel.add(towerButtonPanel, BorderLayout.SOUTH);
        
        // GameMapPanel과 WaveInfoPanel 연결
        gameMapPanel.setWaveInfoPanel(waveInfoPanel);
        
        // 메인 패널에 배치
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 프레임에 패널 추가
        setContentPane(mainPanel);
    }
    
    /**
     * 게임 맵 패널 생성
     */
    private GameMapPanel createGameMapPanel() {
        GameMapPanel panel = new GameMapPanel();
        // 게임 세션에서 초기 설정 로드
        if (gameSession != null) {
            panel.setMoney(gameSession.getMoney());
            panel.setLife(gameSession.getLife());
            panel.setScore(gameSession.getScore());
            panel.setCurrentWave(gameSession.getWave());
            panel.setSessionId(gameSession.getSessionId()); // 세션 ID 설정 추가
        }
        panel.setBackground(new Color(50, 50, 50));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        
        // 게임 맵 크기 조정
        panel.adjustSize();
        
        return panel;
    }
    
    /**
     * 컨트롤 버튼 패널 생성
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        controlPanel.setOpaque(false);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 일시정지 버튼
        btnPause = new PixelButton("일시정지", UIConstants.getSmallPixelFont());
        btnPause.setBackground(new Color(60, 60, 100));
        btnPause.setBorderColor(new Color(100, 100, 180));
        btnPause.addActionListener(e -> togglePause());
        
        // 저장 버튼
        btnSave = new PixelButton("저장", UIConstants.getSmallPixelFont());
        btnSave.setBackground(new Color(60, 100, 60));
        btnSave.setBorderColor(new Color(100, 180, 100));
        btnSave.addActionListener(e -> saveGame());
        
        // 나가기 버튼
        btnExit = new PixelButton("나가기", UIConstants.getSmallPixelFont());
        btnExit.setBackground(new Color(100, 60, 60));
        btnExit.setBorderColor(new Color(180, 100, 100));
        btnExit.addActionListener(e -> confirmExit());
        
        controlPanel.add(btnPause);
        controlPanel.add(btnSave);
        controlPanel.add(btnExit);
        
        return controlPanel;
    }
    
    /**
     * 타워 버튼 패널 생성
     */
    private JPanel createTowerButtonPanel() {
        JPanel towerButtonPanel = new JPanel();
        towerButtonPanel.setOpaque(false);
        towerButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        towerButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        // 타워 생성 버튼 (크고 눈에 잘 띄게)
        PixelButton btnCreateTower = new PixelButton("기본 타워 설치");
        btnCreateTower.setFont(new Font("Dialog", Font.BOLD, 16));
        btnCreateTower.setPreferredSize(new Dimension(180, 50));
        btnCreateTower.setBackground(new Color(30, 150, 70));
        btnCreateTower.setBorderColor(new Color(20, 100, 50));
        btnCreateTower.setForeground(Color.WHITE);
        btnCreateTower.addActionListener(e -> {
            // 타워 선택 패널의 메서드 호출
            if (towerSelectionPanel != null) {
                towerSelectionPanel.createBasicTower();
            }
        });
        
        // 타워 업그레이드 버튼
        PixelButton btnUpgradeTower = new PixelButton("타워 업그레이드");
        btnUpgradeTower.setFont(new Font("Dialog", Font.BOLD, 16));
        btnUpgradeTower.setPreferredSize(new Dimension(180, 50));
        btnUpgradeTower.setBackground(new Color(200, 150, 50));
        btnUpgradeTower.setBorderColor(new Color(150, 100, 30));
        btnUpgradeTower.setForeground(Color.WHITE);
        btnUpgradeTower.addActionListener(e -> {
            // 타워 선택 패널의 메서드 호출
            if (towerSelectionPanel != null) {
                towerSelectionPanel.upgradeSelectedTower();
            }
        });
        
        // 패널에 버튼 추가
        towerButtonPanel.add(btnCreateTower);
        towerButtonPanel.add(btnUpgradeTower);
        
        return towerButtonPanel;
    }
    
    /**
     * 창 크기에 따라 컴포넌트 크기 조정
     */
    private void adjustComponentSizes() {
        int width = getWidth();
        int height = getHeight();
        
        // 게임맵 크기 동적 조정 추가
        if (gameMapPanel != null) {
            // 게임 맵 패널을 화면 크기에 맞게 조정
            int mapSize = Math.min(width - 40, height - 200); // 여백 고려
            gameMapPanel.adjustSize(mapSize, mapSize);
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
     * 게임 일시정지/재개 토글
     */
    private void togglePause() {
        isPaused = !isPaused;
        
        if (isPaused) {
            gameTimer.stop();
            btnPause.setText("계속하기");
            
            // GameMapPanel에도 일시정지 상태 전달
            if (gameMapPanel != null) {
                gameMapPanel.setPaused(true);
            }
        } else {
            gameTimer.start();
            btnPause.setText("일시정지");
            
            // GameMapPanel에도 일시정지 해제 상태 전달
            if (gameMapPanel != null) {
                gameMapPanel.setPaused(false);
            }
        }
    }
    
    /**
     * 게임 상태 저장
     */
    private void saveGame() {
        try {
            if (gameSession != null) {
                // 현재 게임 상태 정보 업데이트
                gameSession.setLife(gameMapPanel.getLife());
                gameSession.setMoney(gameMapPanel.getMoney());
                gameSession.setScore(gameMapPanel.getScore());
                gameSession.setWave(waveInfoPanel.getCurrentWave());
                
                // 타워 배치 정보 저장
                gameMapPanel.saveTowerPlacements(gameSession.getSessionId());
                
                // 세션 정보 저장
                SessionService sessionService = new SessionServiceImpl();
                boolean saved = sessionService.saveGameState(gameSession);
                
                if (saved) {
                    JOptionPane.showMessageDialog(this, "게임이 성공적으로 저장되었습니다.", 
                        "저장 성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "게임 저장에 실패했습니다.", 
                        "저장 실패", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "게임 저장 중 오류가 발생했습니다: " + e.getMessage(), 
                "저장 오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * 게임 상태 로드 (GameRoomFrame 생성자에서 호출)
     */
    private void loadGame() {
        if (gameSession != null) {
            // 세션에서 저장된 게임 상태 정보 로드
            gameMapPanel.setLife(gameSession.getLife());
            gameMapPanel.setMoney(gameSession.getMoney());
            waveInfoPanel.updateWaveNumber(gameSession.getWave());
            
            // GameMapPanel의 현재 웨이브 설정
            gameMapPanel.setCurrentWave(gameSession.getWave());
            
            // GameMapPanel에 세션 ID 설정
            gameMapPanel.setSessionId(gameSession.getSessionId());
            
            int sessionId = gameSession.getSessionId();
            // 타워 배치 정보 로드 및 화면에 표시
            System.out.println("타워 배치 정보 로드 시작: 세션 ID = " + sessionId);
            
            // 직접 DAO를 사용하여 타워 배치 정보 로드
            TowerDAO towerDAO = new TowerDAO();
            List<TowerPlacement> placements = towerDAO.getTowerPlacementsBySessionId(sessionId);
            
            if (placements != null && !placements.isEmpty()) {
                System.out.println("로드된 타워 배치 목록: " + placements.size() + "개");
                
                // GameMapPanel에 타워 배치 정보 전달
                gameMapPanel.loadTowerPlacements(sessionId);
                
                // 화면 갱신 강제
                gameMapPanel.repaint();
            } else {
                System.out.println("세션 ID " + sessionId + "에 대한 타워 배치 정보가 없음");
            }
            
            System.out.println("게임 상태 로드 완료: 생명력=" + gameSession.getLife() + 
                              ", 자금=" + gameSession.getMoney() + 
                              ", 웨이브=" + gameSession.getWave());
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
                
                // UI 정보 업데이트
                resourcePanel.updateResources(
                    gameMapPanel.getLife(),
                    gameMapPanel.getMoney()
                );
            }
        });
        
        // 타이머 시작
        gameTimer.start();
    }
    
    /**
     * 게임 오버 처리
     */
    public void handleGameOver() {
        // 게임 타이머 정지
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // 게임 점수 계산 및 랭킹 등록
        int finalScore = gameMapPanel.getScore();
        gameSession.setScore(finalScore);
        
        // 랭킹 등록
        RankingController rankingController = new RankingController();
        boolean rankingAdded = rankingController.addRanking(loggedInUser.getUserId(), finalScore);
        System.out.println("랭킹 등록 결과: " + (rankingAdded ? "성공" : "실패"));
        
        // 게임 선택 화면으로 이동
        dispose();
        
        // 현재 창의 상태 저장
        boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
        Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();
        
        // 게임 선택 화면으로 돌아가기
        GameSelectionFrame selectionFrame = new GameSelectionFrame(loggedInUser, currentBounds, currentMaximized);
        selectionFrame.setVisible(true);
    }
    
    /**
     * 게임 승리 처리
     */
    public void handleGameWin() {
        // 게임 타이머 정지
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // 게임 정보 저장
        int finalWave = waveInfoPanel.getCurrentWave();
        int finalScore = finalWave * 100 + gameMapPanel.getScore(); // 웨이브 + 게임맵 점수 합산
        gameSession.setScore(finalScore);
        saveGame(); // 게임 상태 저장
        
        // 랭킹 등록
        RankingController rankingController = new RankingController();
        boolean rankingAdded = rankingController.addRanking(loggedInUser.getUserId(), finalScore);
        System.out.println("랭킹 등록 결과: " + (rankingAdded ? "성공" : "실패"));
        
        // 게임 선택 화면으로 이동
        dispose();
        
        // 현재 창의 상태 저장
        boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
        Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();
        
        // 게임 선택 화면으로 돌아가기
        GameSelectionFrame selectionFrame = new GameSelectionFrame(loggedInUser, currentBounds, currentMaximized);
        selectionFrame.setVisible(true);
    }
    
    /**
     * 게임 재시작
     */
    public void restartGame() {
        // 게임 타이머 정지
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // 게임 점수 계산 및 랭킹 등록
        int finalScore = gameMapPanel.getScore();
        gameSession.setScore(finalScore);
        
        // 랭킹 등록
        RankingController rankingController = new RankingController();
        boolean rankingAdded = rankingController.addRanking(loggedInUser.getUserId(), finalScore);
        System.out.println("랭킹 등록 결과: " + (rankingAdded ? "성공" : "실패"));
        
        // 현재 창의 상태 저장
        boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
        Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();
        
        // 새 게임 세션 시작
        dispose();
        Session newSession = new Session();
        newSession.setUserId(loggedInUser.getUserId());
        
        // 동일한 위치와 크기로 새 게임 창 생성
        GameRoomFrame newGame = new GameRoomFrame(loggedInUser, newSession, currentBounds, currentMaximized);
        newGame.setVisible(true);
    }
    
    /**
     * 랭킹 화면 표시
     */
    public void showRankingScreen() {
        // 게임 타이머 정지
        if (gameTimer != null) {
            gameTimer.stop();
        }
        
        // 게임 점수 계산 및 랭킹 등록
        int finalScore = gameMapPanel.getScore();
        gameSession.setScore(finalScore);
        
        // 랭킹 등록
        RankingController rankingController = new RankingController();
        boolean rankingAdded = rankingController.addRanking(loggedInUser.getUserId(), finalScore);
        System.out.println("랭킹 등록 결과: " + (rankingAdded ? "성공" : "실패"));
        
        // 현재 창의 상태 저장
        boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
        Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();
        
        // 랭킹 화면 표시
        dispose();
        RankingFrame rankingFrame = new RankingFrame(loggedInUser, currentBounds, currentMaximized);
        rankingFrame.setVisible(true);
    }
    
    /**
     * 현재 게임 세션 반환
     * @return 게임 세션
     */
    public Session getGameSession() {
        return this.gameSession;
    }
} 