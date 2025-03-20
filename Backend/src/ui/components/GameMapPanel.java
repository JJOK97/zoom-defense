package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Iterator;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

import contorller.EnemyController;
import contorller.TowerController;
import model.Enemy;
import model.Tower;
import ui.GameRoomFrame;
import model.Session;
import model.TowerPlacement;
import service.SessionService;
import service.SessionServiceImpl;
import service.TowerService;
import service.TowerServicelmpl;
import service.TowerPlacementService;
import service.TowerPlacementServicelmpl;
import dao.TowerDAO;

/**
 * 게임 맵을 표시하고 타워 배치 및 게임 진행을 관리하는 패널
 */
public class GameMapPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // 그리드 설정
    private static final int GRID_SIZE = 40; // 그리드 한 칸의 크기
    private int gridRows = 15;      // 그리드 행 수
    private int gridColumns = 30;   // 그리드 열 수 (1.5배 증가)
    
    // 게임 오브젝트 위치 관리
    private int[][] towerMap;       // 타워가 배치된 위치 (0: 빈 공간, >0: 타워 ID)
    private List<Point> pathPoints; // 적이 이동하는 경로 포인트
    private Point selectedCell;     // 현재 선택된 셀
    private Point hoveredCell;      // 마우스가 위치한 셀
    
    // 게임 자원 정보
    private int money = 100;        // 보유 금액
    private int life = 100;         // 남은 생명력
    
    // 연결된 UI 컴포넌트
    private WaveInfoPanel waveInfoPanel;
    
    // 적 객체 리스트 (현재 화면에 표시된 적들)
    private List<GameEnemy> activeEnemies = new ArrayList<>();

    // 웨이브 정보
    private int currentWave = 1;
    private int totalEnemies = 0;
    private int killedEnemies = 0;
    private boolean waveInProgress = false;

    // 적 스폰 타이머
    private Timer enemySpawnTimer;
    private int spawnDelay = 1000; // 1초마다 적 생성
    
    // 공격 애니메이션을 위한 데이터 구조
    private class AttackAnimation {
        private int startX, startY;   // 공격 시작 위치
        private int targetX, targetY; // 공격 대상 위치
        private long startTime;       // 애니메이션 시작 시간
        private int duration;         // 애니메이션 지속 시간(ms)
        private int damage;           // 공격 데미지
        private Color color;          // 공격 효과 색상
        
        public AttackAnimation(int startX, int startY, int targetX, int targetY, int damage, Color color) {
            this.startX = startX;
            this.startY = startY;
            this.targetX = targetX;
            this.targetY = targetY;
            this.startTime = System.currentTimeMillis();
            this.duration = 500; // 0.5초
            this.damage = damage;
            this.color = color;
        }
        
        public boolean isActive() {
            return System.currentTimeMillis() - startTime < duration;
        }
        
        public float getProgress() {
            long elapsed = System.currentTimeMillis() - startTime;
            return Math.min(1.0f, (float)elapsed / duration);
        }
    }

    // 활성화된 공격 애니메이션 목록
    private List<AttackAnimation> attackAnimations = new ArrayList<>();
    
    // 추가할 필드
    private boolean isPaused = false;
    
    // 점수 필드 추가
    private int score = 0;
    
    // TowerSelectListener 추가
    private TowerSelectListener towerSelectListener = null;

    // 타워 선택 이벤트 인터페이스 추가
    public interface TowerSelectListener {
        void onTowerSelected(int towerId, int row, int col);
        void onEmptyCellSelected(int row, int col);
    }

    // 카운트다운 상태 변수
    private int countdown = 0;
    private long countdownStartTime = 0;
    private boolean isCountingDown = false;

    /**
     * 기본 생성자
     */
    public GameMapPanel() {
        initialize();
    }
    
    /**
     * 웨이브 정보 패널 설정
     */
    public void setWaveInfoPanel(WaveInfoPanel waveInfoPanel) {
        this.waveInfoPanel = waveInfoPanel;
    }
    
    /**
     * 패널 초기화
     */
    private void initialize() {
        // 초기 설정
        setOpaque(false);
        setLayout(null);
        
        // 그리드 초기화
        towerMap = new int[gridRows][gridColumns];
        
        // 기본 경로 설정
        initializeDefaultPath();
        
        // 마우스 이벤트 처리
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 마우스 클릭 위치에서 그리드 셀 좌표 계산
                int col = e.getX() / GRID_SIZE;
                int row = e.getY() / GRID_SIZE;
                
                // 유효한 셀인지 확인
                if (row >= 0 && row < gridRows && col >= 0 && col < gridColumns) {
                    // 경로 위인지 확인
                    if (isPathCell(row, col)) {
                        System.out.println("경로 위에는 타워를 배치할 수 없습니다.");
                        return;
                    }
                    
                    // 이미 타워가 있는지 확인
                    if (towerMap[row][col] > 0) {
                        // 타워가 있으면 해당 타워 선택
                        selectedCell = new Point(col, row);
                        int towerId = towerMap[row][col];
                        System.out.println("타워 선택됨: (" + col + ", " + row + ") - ID: " + towerId);
                        
                        // 타워 선택 이벤트 발생
                        if (towerSelectListener != null) {
                            towerSelectListener.onTowerSelected(towerId, row, col);
                        }
                    } else {
                        // 타워가 없으면 해당 위치 선택 (타워 배치 예정 위치)
                        selectedCell = new Point(col, row);
                        System.out.println("셀 선택됨: (" + col + ", " + row + ")");
                        
                        // 빈 셀 선택 이벤트 발생
                        if (towerSelectListener != null) {
                            towerSelectListener.onEmptyCellSelected(row, col);
                        }
                    }
                    
                    // 화면 갱신
                    repaint();
                }
            }
        });
        
        // 마우스 이동 이벤트 처리
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // 마우스 위치에서 그리드 셀 좌표 계산
                int col = e.getX() / GRID_SIZE;
                int row = e.getY() / GRID_SIZE;
                
                // 유효한 셀인지 확인
                if (row >= 0 && row < gridRows && col >= 0 && col < gridColumns) {
                    hoveredCell = new Point(col, row);
                    repaint();
                } else {
                    // 맵 밖으로 나갔을 때
                    hoveredCell = null;
                    repaint();
                }
            }
        });
    }
    
    /**
     * 기본 적 이동 경로 초기화 (임시)
     */
    private void initializeDefaultPath() {
        pathPoints = new ArrayList<>();
        
        // 더 복잡한 경로 설정
        // 시작점 (왼쪽 상단)
        pathPoints.add(new Point(0, 3));
        
        // 오른쪽으로 이동
        for (int i = 1; i < 8; i++) {
            pathPoints.add(new Point(i, 3));
        }
        
        // 아래로 이동
        for (int i = 4; i < 9; i++) {
            pathPoints.add(new Point(7, i));
        }
        
        // 왼쪽으로 이동
        for (int i = 6; i >= 3; i--) {
            pathPoints.add(new Point(i, 8));
        }
        
        // 아래로 이동
        for (int i = 12; i < 14; i++) {
            pathPoints.add(new Point(3, i));
        }
        
        // 오른쪽으로 이동
        for (int i = 4; i < 18; i++) {
            pathPoints.add(new Point(i, 13));
        }
        
        // 위로 이동
        for (int i = 12; i >= 8; i--) {
            pathPoints.add(new Point(17, i));
        }
        
        // 왼쪽으로 이동
        for (int i = 17; i >= 12; i--) {
            pathPoints.add(new Point(i, 8));
        }
        
        // 위로 이동
        for (int i = 8; i >= 3; i--) {
            pathPoints.add(new Point(11, i));
        }
        
        // 오른쪽으로 이동
        for (int i = 12; i < 25; i++) {
            pathPoints.add(new Point(i, 3));
        }
        
        // 아래로 이동
        for (int i = 4; i < 9; i++) {
            pathPoints.add(new Point(24, i));
        }
        
        // 왼쪽으로 이동
        for (int i = 23; i >= 20; i--) {
            pathPoints.add(new Point(i, 8));
        }
        
        // 아래로 이동
        for (int i = 9; i < 14; i++) {
            pathPoints.add(new Point(20, i));
        }
        
        // 오른쪽으로 이동 (도착점)
        for (int i = 20; i <= 29; i++) {
            pathPoints.add(new Point(i, 13));
        }
    }
    
    /**
     * 특정 위치가 적 경로인지 확인
     * @param row 행 인덱스
     * @param col 열 인덱스
     * @return 경로 포함 여부
     */
    private boolean isPathCell(int row, int col) {
        for (Point p : pathPoints) {
            if (p.y == row && p.x == col) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 자금 설정
     */
    public void setMoney(int money) {
        this.money = money;
    }
    
    /**
     * 생명력 설정
     */
    public void setLife(int life) {
        this.life = life;
    }
    
    /**
     * 게임 상태 업데이트
     */
    public void update() {
        if (!isPaused) {
            // 적 업데이트
            updateEnemies();
            
            // 타워 업데이트 (공격 처리)
            updateTowers();
            
            // 웨이브 완료 체크
            checkWaveCompletion();
            
            // 화면 다시 그리기
            repaint();
        }
    }
    
    /**
     * 적 업데이트 (이동 및 생명력 감소)
     */
    private void updateEnemies() {
        Iterator<GameEnemy> it = activeEnemies.iterator();
        
        while (it.hasNext()) {
            GameEnemy enemy = it.next();
            
            // 적 이동
            moveEnemy(enemy);
            
            // 목적지 도달 체크
            if (enemy.hasReachedEnd(pathPoints)) {
                // 생명력 감소
                life -= enemy.getDamage();
                
                // 적 제거
                it.remove();
                
                // UI 업데이트 - 자원 패널에 반영될 수 있도록
                
                // 게임 오버 체크
                if (life <= 0) {
                    // 생명력이 0 이하로 떨어지면 게임 오버
                    life = 0; // 음수 방지
                    gameOver();
                    return;
                }
            }
        }
    }
    
    /**
     * 적 이동 처리
     */
    private void moveEnemy(GameEnemy enemy) {
        // 목표 지점 (다음 경로 포인트)
        int pathIndex = enemy.getPathIndex();
        
        if (pathIndex < pathPoints.size()) {
            Point targetPoint = pathPoints.get(pathIndex);
            
            // 그리드 좌표를 픽셀 좌표로 변환
            int targetX = targetPoint.x * GRID_SIZE + GRID_SIZE/2;
            int targetY = targetPoint.y * GRID_SIZE + GRID_SIZE/2;
            
            // 현재 위치
            int currentX = enemy.getX();
            int currentY = enemy.getY();
            
            // 이동 방향 계산
            double distX = targetX - currentX;
            double distY = targetY - currentY;
            double distance = Math.sqrt(distX * distX + distY * distY);
            
            // 목표 지점에 도달했는지 확인
            if (distance < enemy.getSpeed()) {
                // 다음 경로 포인트로 이동
                enemy.setPathIndex(pathIndex + 1);
                
                // 마지막 지점이었다면 목적지 도달
                if (pathIndex + 1 >= pathPoints.size()) {
                    enemy.setReachedEnd(true);
                }
            } else {
                // 속도에 따른 이동
                double speedX = (distX / distance) * enemy.getSpeed();
                double speedY = (distY / distance) * enemy.getSpeed();
                
                enemy.setPosition(currentX + (int)speedX, currentY + (int)speedY);
            }
        }
    }
    
    /**
     * 타워 업데이트 (적 공격)
     */
    private void updateTowers() {
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridColumns; col++) {
                int towerId = towerMap[row][col];
                if (towerId > 0) {
                    // 타워 중심 좌표 계산
                    int towerCenterX = col * GRID_SIZE + GRID_SIZE / 2;
                    int towerCenterY = row * GRID_SIZE + GRID_SIZE / 2;
                    
                    // 타워 정보에 따른 사거리 및 공격력 결정
                    int range = 150; // 기본 사거리
                    int damage = 10; // 기본 공격력
                    Color attackColor;
                    
                    switch(towerId) {
                        case 1: // 1단계 타워
                            range = 150;
                            damage = 10;
                            attackColor = new Color(50, 220, 255);
                            break;
                        case 2: // 2단계 타워
                            range = 200;
                            damage = 20;
                            attackColor = new Color(255, 215, 0);
                            break;
                        case 3: // 3단계 타워
                            range = 250;
                            damage = 35;
                            attackColor = new Color(255, 50, 50);
                            break;
                        default:
                            attackColor = Color.WHITE;
                    }
                    
                    // 가장 가까운 적 찾기
                    GameEnemy target = findClosestEnemy(towerCenterX, towerCenterY, range);
                    
                    if (target != null) {
                        // 적에게 데미지 주기
                        target.takeDamage(damage);
                        
                        // 공격 애니메이션 추가
                        attackAnimations.add(new AttackAnimation(
                            towerCenterX, 
                            towerCenterY, 
                            target.getX() + target.getSize() / 2, 
                            target.getY() + target.getSize() / 2,
                            damage,
                            attackColor
                        ));
                        
                        // 적이 죽었는지 확인
                        if (target.getHealth() <= 0) {
                            // 적이 죽으면 돈 획득
                            money += target.getReward();
                            
                            // 처치한 적 카운트 증가
                            killedEnemies++;
                            
                            // 점수 증가 - 적의 종류에 따라 다른 점수 부여
                            int enemyScore = target.getReward() * 10; // 보상금의 10배를 점수로
                            score += enemyScore;
                            
                            // 적 제거
                            activeEnemies.remove(target);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 가장 가까운 적 찾기
     */
    private GameEnemy findClosestEnemy(int x, int y, int range) {
        GameEnemy closest = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (GameEnemy enemy : activeEnemies) {
            double distance = Math.sqrt(
                Math.pow(enemy.getX() - x, 2) + 
                Math.pow(enemy.getY() - y, 2)
            );
            
            if (distance <= range && distance < closestDistance) {
                closest = enemy;
                closestDistance = distance;
            }
        }
        
        return closest;
    }
    
    /**
     * 웨이브 완료 체크
     */
    private void checkWaveCompletion() {
        if (waveInProgress && activeEnemies.isEmpty() && 
            (enemySpawnTimer == null || !enemySpawnTimer.isRunning())) {
            
            // 웨이브 완료
            waveInProgress = false;
            
            // 모든 적 처치 완료했을 때
            if (killedEnemies >= totalEnemies) {
                // 웨이브 완료 보너스 점수
                int waveBonus = currentWave * 100;
                score += waveBonus;
                
                // 마지막 웨이브인지 확인
                if (currentWave < 20) {
                    // 카운트다운 표시 및 다음 웨이브 자동 시작
                    startWaveCountdown();
                } else {
                    // 모든 웨이브 클리어
                    gameWin();
                }
            }
        }
    }
    
    /**
     * 게임 오버
     */
    private void gameOver() {
        // 게임 진행 정지 (타이머 정지)
        if (enemySpawnTimer != null) {
            enemySpawnTimer.stop();
        }
        
        // 게임 오버 메시지 표시
        JOptionPane.showMessageDialog(
            this, 
            "게임 오버!\n생명력이 모두 소진되었습니다.\n웨이브 " + currentWave + "에서 패배했습니다.", 
            "게임 오버", 
            JOptionPane.INFORMATION_MESSAGE
        );
        
        // GameRoomFrame에 게임 오버 알림
        // 부모 컴포넌트를 찾아 GameRoomFrame 인스턴스 확인
        Container parent = getParent();
        while (parent != null) {
            if (parent instanceof JFrame) {
                if (parent instanceof GameRoomFrame) {
                    ((GameRoomFrame) parent).handleGameOver();
                    return;
                }
                break;
            }
            parent = parent.getParent();
        }
        
        // 부모 프레임을 찾지 못한 경우 직접 창 닫기 시도
        SwingUtilities.getWindowAncestor(this).dispose();
    }
    
    /**
     * 타워 배치
     * @param row 행 인덱스
     * @param col 열 인덱스
     * @param tower 배치할 타워 정보
     * @return 배치 성공 여부
     */
    public boolean placeTower(int row, int col, Tower tower) {
        // 상세 로그 출력
        System.out.println("타워 배치 시도(placeTower): 행=" + row + ", 열=" + col + ", 타워ID=" + tower.getTowerId());
        
        // 위치 유효성 검사
        if (row < 0 || row >= gridRows || col < 0 || col >= gridColumns) {
            System.out.println("배치 실패: 맵 범위 밖입니다.");
            return false;
        }
        
        // 이미 타워가 있거나 경로인지 확인
        if (towerMap[row][col] > 0) {
            System.out.println("배치 실패: 이미 타워가 있습니다.");
            return false;
        }
        
        if (isPathCell(row, col)) {
            System.out.println("배치 실패: 경로 위입니다.");
            return false;
        }
        
        // 돈이 충분한지 확인
        if (money < tower.getCost()) {
            System.out.println("배치 실패: 자금 부족 (필요: " + tower.getCost() + ", 보유: " + money + ")");
            return false;
        }
        
        // 세션 ID 가져오기 시도
        int sessionId = 0;
        try {
            Container container = this;
            while (container != null && !(container instanceof GameRoomFrame)) {
                container = container.getParent();
            }
            
            if (container instanceof GameRoomFrame) {
                GameRoomFrame gameRoom = (GameRoomFrame) container;
                sessionId = gameRoom.getGameSession().getSessionId();
                System.out.println("세션 ID 확인: " + sessionId);
            } else {
                System.out.println("GameRoomFrame을 찾을 수 없습니다.");
                // 로컬에서만 타워 배치 (디버깅용)
                towerMap[row][col] = tower.getTowerId();
                money -= tower.getCost();
                System.out.println("로컬에만 타워 설치: ID=" + tower.getTowerId() + ", 위치=(" + col + "," + row + ")");
                repaint();
                return true;
            }
        } catch (Exception e) {
            System.out.println("세션 ID 가져오기 실패: " + e.getMessage());
            e.printStackTrace();
            // 로컬에서만 타워 배치 (디버깅용)
            towerMap[row][col] = tower.getTowerId();
            money -= tower.getCost();
            System.out.println("예외 발생 후 로컬에만 타워 설치: ID=" + tower.getTowerId() + ", 위치=(" + col + "," + row + ")");
            repaint();
            return true;
        }
        
        // 간단한 테스트: 로컬 타워 맵에 바로 설치
        towerMap[row][col] = tower.getTowerId();
        money -= tower.getCost();
        System.out.println("로컬 타워 설치 성공: ID=" + tower.getTowerId() + ", 위치=(" + col + "," + row + ")");
        
        try {
            // 타워 배치 정보 생성
            TowerPlacement placement = new TowerPlacement();
            placement.setTowerId(tower.getTowerId());
            placement.setSessionId(sessionId);
            placement.setPositionX(col);
            placement.setPositionY(row);
            
            // 서비스를 통해 DB에 저장 시도
            TowerPlacementService service = new TowerPlacementServicelmpl();
            boolean success = service.placeTower(placement);
            
            System.out.println("타워 배치 DB 저장 결과: " + (success ? "성공" : "실패"));
            
            // DB 저장 실패해도 UI상으로는 타워 표시 (이미 위에서 배치함)
            repaint(); // 화면 갱신
            return true;
        } catch (Exception e) {
            System.out.println("타워 배치 저장 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            // 이미 로컬에 타워를 배치했으므로 UI에는 표시됨
            repaint();
            return true;
        }
    }
    
    // 타워 배치 효과 표시
    public void showTowerPlacementEffect(int col, int row) {
        // 콘솔에 로그 출력
        System.out.println("🏗️ 타워 건설 중... 위치: (" + col + "," + row + ")");
        
        // 타워 배치 효과 애니메이션 추가
        try {
            // 그리드 좌표를 픽셀 좌표로 변환
            int pixelX = col * GRID_SIZE;
            int pixelY = row * GRID_SIZE;
            
            // 임시 효과를 위한 애니메이션 (간단한 플래시 효과)
            final int duration = 500; // 0.5초
            final int steps = 5;
            final int delay = duration / steps;
            
            new Thread(() -> {
                try {
                    for (int i = 0; i < steps; i++) {
                        final int alpha = (i % 2 == 0) ? 200 : 100; // 깜빡임 효과
                        
                        SwingUtilities.invokeLater(() -> {
                            Graphics2D g2d = (Graphics2D) getGraphics();
                            if (g2d != null) {
                                g2d.setColor(new Color(255, 255, 0, alpha));
                                g2d.fillRect(pixelX, pixelY, GRID_SIZE, GRID_SIZE);
                                g2d.dispose();
                            }
                        });
                        
                        Thread.sleep(delay);
                    }
                    
                    // 마지막 repaint로 원래 상태로 복원
                    repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            System.out.println("타워 배치 효과 표시 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 타워 업그레이드
     * @param row 행 인덱스
     * @param col 열 인덱스
     * @return 업그레이드 성공 여부
     */
    public boolean upgradeTower(int row, int col) {
        // 빈 칸이면 업그레이드 불가능
        if (towerMap[row][col] <= 0) {
            System.out.println("업그레이드 실패: 타워가 없는 위치");
            return false;
        }
        
        int currentTowerId = towerMap[row][col];
        System.out.println("업그레이드 시도: 현재 타워 ID = " + currentTowerId);
        
        // 타워 정보 가져오기
        TowerController towerController = new TowerController();
        Tower currentTower = towerController.getTowerById(currentTowerId);
        
        if (currentTower == null) {
            System.out.println("업그레이드 실패: 타워 정보를 찾을 수 없음");
            return false;
        }
        
        System.out.println("현재 타워 정보: ID=" + currentTower.getTowerId() + 
                          ", 레벨=" + currentTower.getTowerLevel() + 
                          ", 이름=" + currentTower.getTowerName());
        
        // 이미 최고 레벨인 경우
        if (currentTower.getTowerLevel() >= 3) {
            System.out.println("업그레이드 실패: 이미 최고 레벨 타워");
            JOptionPane.showMessageDialog(this, "이미 최고 레벨의 타워입니다.");
            return false;
        }
        
        // 업그레이드 비용 확인
        int upgradeCost = currentTower.getUpgradeCost();
        
        if (money < upgradeCost) {
            // 자금 부족
            System.out.println("업그레이드 실패: 자금 부족 (필요: " + upgradeCost + ", 보유: " + money + ")");
            JOptionPane.showMessageDialog(this, "타워 업그레이드 비용이 부족합니다.\n필요 비용: " + upgradeCost + ", 보유 자금: " + money);
            return false;
        }
        
        // 타워 업그레이드 수행
        Tower upgradedTower = null;
        
        // 타워 레벨에 따라 다음 레벨 타워 선택
        if (currentTower.getTowerLevel() == 1) {
            upgradedTower = towerController.getSecondTower();
        } else if (currentTower.getTowerLevel() == 2) {
            upgradedTower = towerController.getThirdTower();
        }
        
        if (upgradedTower == null) {
            System.out.println("업그레이드 실패: 다음 레벨 타워를 가져올 수 없음");
            return false;
        }
        
        // 타워 맵 업데이트
        towerMap[row][col] = upgradedTower.getTowerId();
        
        // 비용 차감
        money -= upgradeCost;
        
        // DB에도 업데이트
        TowerPlacementService towerPlacementService = new TowerPlacementServicelmpl();
        int sessionId = 0;
        
        // 상위 컴포넌트에서 세션 ID 가져오기
        Container parent = getParent();
        while (parent != null) {
            if (parent instanceof GameRoomFrame) {
                sessionId = ((GameRoomFrame) parent).getGameSession().getSessionId();
                break;
            }
            parent = parent.getParent();
        }
        
        if (sessionId > 0) {
            boolean dbUpdateSuccess = towerPlacementService.upgradeTower(sessionId, col, row);
            System.out.println("DB 업데이트 결과: " + (dbUpdateSuccess ? "성공" : "실패"));
        }
        
        // 효과음 및 애니메이션 추가
        showTowerPlacementEffect(col, row);
        
        // 화면 갱신
        repaint();
        
        System.out.println("타워 업그레이드 완료: ID=" + upgradedTower.getTowerId() + 
                           ", 레벨=" + upgradedTower.getTowerLevel() + 
                           ", 이름=" + upgradedTower.getTowerName());
        
        return true;
    }
    
    /**
     * 적 그리기
     */
    private void drawEnemies(Graphics2D g2d) {
        for (GameEnemy enemy : activeEnemies) {
            int x = enemy.getX();
            int y = enemy.getY();
            int size = enemy.getSize();
            
            // 적 몸체 그리기 - 적 ID에 따라 색상 결정
            switch (enemy.getEnemyId()) {
                case 1: // 슬라임
                    g2d.setColor(new Color(0, 150, 255)); // 파란색
                    break;
                case 2: // 좀비
                    g2d.setColor(new Color(100, 255, 100)); // 연두색
                    break;
                case 3: // 고블린
                    g2d.setColor(new Color(50, 200, 50)); // 녹색
                    break;
                case 4: // 오크
                    g2d.setColor(new Color(100, 150, 100)); // 어두운 녹색
                    break;
                case 5: // 암살자
                    g2d.setColor(new Color(50, 50, 50)); // 검은색
                    break;
                case 6: // 마법사
                    g2d.setColor(new Color(150, 50, 200)); // 보라색
                    break;
                case 7: // 트롤
                    g2d.setColor(new Color(150, 200, 50)); // 황토색
                    break;
                case 8: // 드래곤
                    g2d.setColor(new Color(200, 50, 50)); // 빨간색
                    break;
                case 9: // 데몬
                    g2d.setColor(new Color(255, 50, 0)); // 주황색
                    break;
                case 10: // 고대 드래곤
                    g2d.setColor(new Color(200, 200, 0)); // 금색
                    break;
                case 11: // 마왕
                    g2d.setColor(new Color(150, 0, 0)); // 암적색
                    break;
                case 12: // 지옥의 군주
                    g2d.setColor(new Color(255, 0, 0)); // 밝은 빨간색
                    break;
                default:
                    g2d.setColor(new Color(100, 100, 100)); // 기본 회색
            }
            g2d.fillOval(x, y, size, size);
            
            // 적 테두리
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, size, size);
            
            // 체력바 배경
            g2d.setColor(new Color(60, 60, 60));
            g2d.fillRect(x - 5, y - 10, size + 10, 5);
            
            // 체력바
            float healthRatio = (float) enemy.getHealth() / enemy.getMaxHealth();
            if (healthRatio > 0.7f) {
                g2d.setColor(new Color(50, 200, 50)); // 녹색
            } else if (healthRatio > 0.3f) {
                g2d.setColor(new Color(230, 230, 0)); // 노란색
            } else {
                g2d.setColor(new Color(200, 50, 50)); // 빨간색
            }
            g2d.fillRect(x - 5, y - 10, (int)((size + 10) * healthRatio), 5);
        }
    }
    
    /**
     * 패널 크기를 그리드 크기에 맞게 조정
     */
    public void adjustSize() {
        // 게임 맵 크기 계산 (그리드 기반)
        int mapWidth = gridColumns * GRID_SIZE;
        int mapHeight = gridRows * GRID_SIZE;
        
        // 패널 크기 설정
        setPreferredSize(new Dimension(mapWidth, mapHeight));
        setMinimumSize(new Dimension(mapWidth, mapHeight));
        setMaximumSize(new Dimension(mapWidth, mapHeight));
        
        revalidate();
        repaint();
    }
    
    /**
     * 창 크기에 맞게 패널 크기 조정
     * @param width 창 너비
     * @param height 창 높이
     */
    public void adjustSize(int width, int height) {
        // 기본 그리드 크기로 조정
        adjustSize();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // 안티앨리어싱 적용
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 그리드 그리기
        drawGrid(g2d);
        
        // 경로 그리기
        drawPath(g2d);
        
        // 선택된 셀 그리기
        if (selectedCell != null) {
            drawSelectedCell(g2d);
        }
        
        // 마우스 위치 셀 그리기
        if (hoveredCell != null) {
            drawHoveredCell(g2d);
        }
        
        // 타워 그리기
        drawTowers(g2d);
        
        // 적 그리기
        drawEnemies(g2d);
        
        // 공격 애니메이션 그리기
        drawAttackAnimations(g2d);
        
        // 카운트다운 텍스트 그리기
        drawCountdown(g2d);
        
        g2d.dispose();
    }
    
    /**
     * 그리드 그리기
     */
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(60, 60, 70));
        
        // 가로선
        for (int i = 0; i <= gridRows; i++) {
            g2d.drawLine(0, i * GRID_SIZE, gridColumns * GRID_SIZE, i * GRID_SIZE);
        }
        
        // 세로선
        for (int i = 0; i <= gridColumns; i++) {
            g2d.drawLine(i * GRID_SIZE, 0, i * GRID_SIZE, gridRows * GRID_SIZE);
        }
    }
    
    /**
     * 적 이동 경로 그리기
     */
    private void drawPath(Graphics2D g2d) {
        if (pathPoints.size() < 2) return;
        
        // 경로 선 그리기
        g2d.setColor(new Color(150, 120, 50));
        g2d.setStroke(new BasicStroke(GRID_SIZE * 0.6f));
        
        for (int i = 0; i < pathPoints.size() - 1; i++) {
            Point p1 = pathPoints.get(i);
            Point p2 = pathPoints.get(i + 1);
            
            int x1 = p1.x * GRID_SIZE + GRID_SIZE / 2;
            int y1 = p1.y * GRID_SIZE + GRID_SIZE / 2;
            int x2 = p2.x * GRID_SIZE + GRID_SIZE / 2;
            int y2 = p2.y * GRID_SIZE + GRID_SIZE / 2;
            
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // 경로 포인트 마커 그리기
        g2d.setColor(new Color(180, 140, 60));
        
        for (Point p : pathPoints) {
            int x = p.x * GRID_SIZE + GRID_SIZE / 2;
            int y = p.y * GRID_SIZE + GRID_SIZE / 2;
            
            g2d.fillOval(x - 5, y - 5, 10, 10);
        }
        
        // 시작점과 끝점 강조
        g2d.setColor(new Color(50, 200, 50)); // 시작점
        int startX = pathPoints.get(0).x * GRID_SIZE + GRID_SIZE / 2;
        int startY = pathPoints.get(0).y * GRID_SIZE + GRID_SIZE / 2;
        g2d.fillOval(startX - 8, startY - 8, 16, 16);
        
        g2d.setColor(new Color(200, 50, 50)); // 끝점
        int endX = pathPoints.get(pathPoints.size() - 1).x * GRID_SIZE + GRID_SIZE / 2;
        int endY = pathPoints.get(pathPoints.size() - 1).y * GRID_SIZE + GRID_SIZE / 2;
        g2d.fillOval(endX - 8, endY - 8, 16, 16);
    }
    
    /**
     * 타워 그리기 (업데이트)
     */
    private void drawTowers(Graphics2D g2d) {
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridColumns; col++) {
                int towerId = towerMap[row][col];
                if (towerId > 0) {
                    int x = col * GRID_SIZE;
                    int y = row * GRID_SIZE;
                    
                    // 타워 레벨에 따라 다른 색상과 크기로 표시
                    Color baseColor;
                    Color topColor;
                    int size = GRID_SIZE - 10;
                    
                    switch (towerId) {
                        case 1: // 1단계 타워 - 파란색
                            baseColor = new Color(30, 100, 200);
                            topColor = new Color(100, 180, 255);
                            break;
                        case 2: // 2단계 타워 - 노란색
                            baseColor = new Color(200, 180, 30);
                            topColor = new Color(255, 220, 50);
                            size = GRID_SIZE - 8; // 약간 더 크게
                            break;
                        case 3: // 3단계 타워 - 빨간색
                            baseColor = new Color(200, 50, 50);
                            topColor = new Color(255, 100, 100);
                            size = GRID_SIZE - 6; // 더 크게
                            break;
                        default:
                            baseColor = Color.GRAY;
                            topColor = Color.LIGHT_GRAY;
                    }
                    
                    // 타워 그림자
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillRect(x + 3, y + 3, size, size);
                    
                    // 타워 베이스
                    g2d.setColor(baseColor);
                    g2d.fillRect(x + (GRID_SIZE - size) / 2, y + (GRID_SIZE - size) / 2, size, size);
                    
                    // 타워 상단 (원형)
                    int circleSize = (int)(size * 0.7);
                    g2d.setColor(topColor);
                    g2d.fillOval(
                        x + (GRID_SIZE - circleSize) / 2, 
                        y + (GRID_SIZE - circleSize) / 2, 
                        circleSize, circleSize
                    );
                    
                    // 타워 테두리
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawRect(x + (GRID_SIZE - size) / 2, y + (GRID_SIZE - size) / 2, size, size);
                    
                    // 타워 레벨 표시
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    String levelText = String.valueOf(towerId);
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.drawString(levelText, 
                                  x + (GRID_SIZE - fm.stringWidth(levelText)) / 2,
                                  y + GRID_SIZE / 2 + fm.getAscent() / 2);
                }
            }
        }
    }
    
    /**
     * 선택된 셀 표시
     */
    private void drawSelectedCell(Graphics2D g2d) {
        if (selectedCell != null) {
            int x = selectedCell.x * GRID_SIZE;
            int y = selectedCell.y * GRID_SIZE;
            
            // 타워가 있는지 확인
            boolean hasTower = towerMap[selectedCell.y][selectedCell.x] > 0;
            
            // 선택된 셀 강조 표시 (타워가 있으면 다른 색상으로)
            if (hasTower) {
                // 타워가 있는 경우 - 황금색 테두리로 강조
                g2d.setColor(new Color(255, 215, 0, 180));
                g2d.setStroke(new BasicStroke(3.0f));
            } else {
                // 빈 셀인 경우 - 녹색 테두리로 강조
                g2d.setColor(new Color(0, 255, 0, 150));
                g2d.setStroke(new BasicStroke(2.0f));
            }
            
            // 셀 테두리 그리기
            g2d.drawRect(x + 1, y + 1, GRID_SIZE - 2, GRID_SIZE - 2);
            
            // 추가 효과 (깜빡임 효과)
            long time = System.currentTimeMillis() % 1000;
            int alpha = (int)(128 + 127 * Math.sin(time * Math.PI / 500.0));
            
            if (hasTower) {
                // 타워가 있는 경우 - 황금색 배경으로 깜빡임
                g2d.setColor(new Color(255, 215, 0, alpha / 4));
            } else {
                // 빈 셀인 경우 - 녹색 배경으로 깜빡임
                g2d.setColor(new Color(0, 255, 0, alpha / 4));
            }
            
            g2d.fillRect(x + 1, y + 1, GRID_SIZE - 2, GRID_SIZE - 2);
        }
    }
    
    /**
     * 마우스 호버 셀 표시
     */
    private void drawHoveredCell(Graphics2D g2d) {
        if (hoveredCell != null && (selectedCell == null || 
            !hoveredCell.equals(selectedCell))) {
            
            int x = hoveredCell.x * GRID_SIZE;
            int y = hoveredCell.y * GRID_SIZE;
            
            // 경로 위인지 확인
            if (isPathCell(hoveredCell.y, hoveredCell.x)) {
                // 경로 위에는 타워를 설치할 수 없음을 표시
                g2d.setColor(new Color(255, 0, 0, 50));
                g2d.fillRect(x, y, GRID_SIZE, GRID_SIZE);
            } else {
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRect(x, y, GRID_SIZE, GRID_SIZE);
            }
        }
    }
    
    /**
     * 현재 자금 반환
     */
    public int getMoney() {
        return money;
    }
    
    /**
     * 현재 생명력 반환
     */
    public int getLife() {
        return life;
    }
    
    /**
     * 선택된 셀 반환
     */
    public Point getSelectedCell() {
        return selectedCell;
    }
    
    /**
     * 선택 해제
     */
    public void clearSelection() {
        selectedCell = null;
        repaint();
    }
    
    /**
     * 웨이브 시작
     */
    public void startWave() {
        // 이미 진행 중인 웨이브가 있으면 무시
        if (waveInProgress) return;
        
        waveInProgress = true;
        
        // 웨이브에 따른 적 수와 종류 결정
        int enemyCount = 10 + (currentWave * 2); // 웨이브당 기본 10마리 + 웨이브 수 * 2
        totalEnemies = enemyCount;
        killedEnemies = 0;
        
        // WaveInfoPanel 업데이트
        if (waveInfoPanel != null) {
            waveInfoPanel.updateWaveProgress(killedEnemies, totalEnemies);
        }
        
        // 적 스폰 타이머 설정
        if (enemySpawnTimer != null) {
            enemySpawnTimer.stop();
        }
        
        // 보스 웨이브 체크 (10, 20)
        final boolean isBossWave = (currentWave == 10 || currentWave == 20);
        
        // 남은 스폰할 적 수
        final int[] remainingEnemies = {isBossWave ? 1 : enemyCount};
        
        enemySpawnTimer = new Timer(spawnDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingEnemies[0] > 0) {
                    spawnEnemy(isBossWave);
                    remainingEnemies[0]--;
                } else {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        
        enemySpawnTimer.start();
    }
    
    /**
     * 적 생성
     */
    private void spawnEnemy(boolean isBoss) {
        EnemyController enemyController = new EnemyController();
        
        int enemyId;
        
        // 보스 웨이브인 경우 보스 적 생성
        if (isBoss) {
            if (currentWave == 10) {
                enemyId = 10; // 10웨이브 보스: 고대 드래곤
            } else if (currentWave == 20) {
                enemyId = 12; // 20웨이브 보스: 지옥의 군주
            } else {
                enemyId = 11; // 기타 보스 웨이브: 마왕
            }
        } else {
            // 웨이브에 따라 등장 가능한 적 종류 결정
            if (currentWave <= 5) {
                // 1-5 웨이브: 슬라임, 좀비, 고블린 중 랜덤
                enemyId = new Random().nextInt(3) + 1;
            } else if (currentWave <= 10) {
                // 6-10 웨이브: 슬라임~오크 중 랜덤
                enemyId = new Random().nextInt(4) + 1;
            } else if (currentWave <= 15) {
                // 11-15 웨이브: 고블린~마법사 중 랜덤
                enemyId = new Random().nextInt(4) + 3;
            } else {
                // 16-20 웨이브: 마법사~데몬 중 랜덤 (드래곤도 간혹 등장)
                enemyId = new Random().nextInt(4) + 6;
                // 10% 확률로 드래곤 등장
                if (new Random().nextInt(10) == 0) {
                    enemyId = 8;
                }
            }
        }
        
        // 웨이브가 높을수록 체력 증가
        int healthMultiplier = 1 + (currentWave / 5);
        
        // DB에서 적 정보 가져오기
        Enemy enemyModel = enemyController.getEnemyById(enemyId);
        
        if (enemyModel != null) {
            // 적 객체 생성 (게임 화면에 표시될 적)
            GameEnemy enemy = new GameEnemy(
                enemyModel.getEnemyId(),
                enemyModel.getEnemyName(),
                enemyModel.getHealth() * healthMultiplier,
                enemyModel.getSpeed(),
                enemyModel.getReward(),
                enemyModel.getDamage()
            );
            
            // 시작 위치 설정 (경로의 첫 지점)
            if (!pathPoints.isEmpty()) {
                Point startPoint = pathPoints.get(0);
                enemy.setPosition(startPoint.x * GRID_SIZE + GRID_SIZE/2, 
                                 startPoint.y * GRID_SIZE + GRID_SIZE/2);
                enemy.setPathIndex(0);
            }
            
            // 보스 또는 강력한 적(드래곤, 데몬, 마왕, 지옥의 군주)인 경우 크기 두 배
            if (isBoss || enemyId >= 8) {
                enemy.setSize(GRID_SIZE * 2);
            } else {
                enemy.setSize(GRID_SIZE);
            }
            
            // 활성 적 목록에 추가
            activeEnemies.add(enemy);
        }
    }
    
    /**
     * 게임 적 클래스 (내부 클래스로 정의)
     */
    private class GameEnemy {
        private int enemyId;
        private String name;
        private int health;
        private int maxHealth;
        private int speed;
        private int reward;
        private int damage;
        private int x, y;
        private int pathIndex;
        private boolean reachedEnd;
        private int size;
        
        public GameEnemy(int enemyId, String name, int health, int speed, int reward, int damage) {
            this.enemyId = enemyId;
            this.name = name;
            this.health = health;
            this.maxHealth = health;
            this.speed = speed;
            this.reward = reward;
            this.damage = damage;
            this.pathIndex = 0;
            this.reachedEnd = false;
            this.size = GRID_SIZE;
        }
        
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        public int getEnemyId() { return enemyId; }
        public int getHealth() { return health; }
        public int getMaxHealth() { return maxHealth; }
        public int getSpeed() { return speed; }
        public int getReward() { return reward; }
        public int getDamage() { return damage; }
        public int getPathIndex() { return pathIndex; }
        public int getSize() { return size; }
        
        public void setPathIndex(int index) { this.pathIndex = index; }
        public void setReachedEnd(boolean reached) { this.reachedEnd = reached; }
        public void setSize(int size) { this.size = size; }
        
        public boolean hasReachedEnd(List<Point> path) {
            return reachedEnd || pathIndex >= path.size();
        }
        
        public void takeDamage(int damage) {
            health -= damage;
            
            // 체력이 0 이하로 떨어지면 죽음 처리
            if (health <= 0) {
                health = 0;
                // 죽음 효과 표시 - 실제로는 여기서 애니메이션, 효과음 등을 추가할 수 있음
                System.out.println(name + " 처치! 보상 +" + reward);
            }
        }
    }
    
    /**
     * 다음 웨이브로 이동
     */
    public void nextWave() {
        currentWave++;
        
        // 웨이브 정보 패널 업데이트
        if (waveInfoPanel != null) {
            waveInfoPanel.updateWaveNumber(currentWave);
        }
        
        // 새 웨이브 시작
        startWave();
    }
    
    /**
     * 모든 웨이브 클리어 (게임 승리)
     */
    private void gameWin() {
        JOptionPane.showMessageDialog(
            this, 
            "축하합니다! 모든 웨이브를 클리어했습니다!",
            "게임 승리", 
            JOptionPane.INFORMATION_MESSAGE
        );
        
        // 게임 종료 처리
        Container parent = getParent();
        while (parent != null) {
            if (parent instanceof JFrame) {
                if (parent instanceof GameRoomFrame) {
                    ((GameRoomFrame) parent).handleGameWin();
                    return;
                }
                break;
            }
            parent = parent.getParent();
        }
    }
    
    /**
     * 현재 배치된 타워 정보를 세션에 저장
     * @param sessionId 세션 ID
     */
    public void saveTowerPlacements(int sessionId) {
        SessionService sessionService = new SessionServiceImpl();
        
        // 모든 타워 배치 정보를 수집
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridColumns; col++) {
                if (towerMap[row][col] > 0) {
                    // 타워가 있는 위치라면 저장
                    TowerPlacement placement = new TowerPlacement(sessionId, col, row);
                    placement.setTowerId(towerMap[row][col]);
                    sessionService.saveTowerPlacement(sessionId, placement);
                }
            }
        }
        System.out.println("타워 배치 정보 저장 완료");
    }
    
    /**
     * 세션에서 타워 배치 정보 로드
     * @param sessionId 세션 ID
     */
    public List<TowerPlacement> loadTowerPlacements(int sessionId) {
        System.out.println("GameMapPanel: 타워 배치 정보 로드 시작 - 세션 ID = " + sessionId);
        
        // 기존 타워 맵 초기화
        for (int i = 0; i < gridRows; i++) {
            for (int j = 0; j < gridColumns; j++) {
                towerMap[i][j] = 0;
            }
        }
        
        // 세션에서 저장된 타워 배치 정보를 로드 (DAO 직접 사용)
        TowerDAO towerDAO = new TowerDAO();
        List<TowerPlacement> placements = towerDAO.getTowerPlacementsBySessionId(sessionId);
        
        if (placements != null && !placements.isEmpty()) {
            System.out.println("불러온 타워 배치 정보: " + placements.size() + "개");
            
            for (TowerPlacement placement : placements) {
                int x = placement.getPositionX();
                int y = placement.getPositionY();
                int towerId = placement.getTowerId();
                
                System.out.println("타워 배치 정보: TowerId=" + towerId + ", X=" + x + ", Y=" + y);
                
                // 맵 범위 내에 있는지 확인
                if (y >= 0 && y < gridRows && x >= 0 && x < gridColumns) {
                    // 타워 정보 가져오기
                    Tower tower = towerDAO.getTowerById(towerId);
                    
                    if (tower != null) {
                        // 타워 맵에 배치
                        towerMap[y][x] = towerId;
                        System.out.println("타워 배치 완료: " + x + ", " + y + " - 타워ID: " + towerId + ", 타워명: " + tower.getTowerName());
                    } else {
                        System.out.println("타워 정보를 찾을 수 없음: TowerId=" + towerId);
                    }
                } else {
                    System.out.println("잘못된 타워 위치: " + x + ", " + y);
                }
            }
        } else {
            System.out.println("타워 배치 정보가 없음");
        }
        
        repaint(); // 화면 갱신
        System.out.println("타워 배치 정보 로드 완료");
        return placements;
    }
    
    private void drawAttackAnimations(Graphics2D g2d) {
        // 완료된 애니메이션을 제거하기 위한 임시 리스트
        List<AttackAnimation> finishedAnimations = new ArrayList<>();
        
        for (AttackAnimation anim : attackAnimations) {
            if (!anim.isActive()) {
                finishedAnimations.add(anim);
                continue;
            }
            
            float progress = anim.getProgress();
            
            // 현재 애니메이션 위치 계산
            int currentX = (int)(anim.startX + (anim.targetX - anim.startX) * progress);
            int currentY = (int)(anim.startY + (anim.targetY - anim.startY) * progress);
            
            // 공격 효과 그리기 (레이저 빔 스타일)
            g2d.setColor(anim.color);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawLine(anim.startX, anim.startY, currentX, currentY);
            
            // 공격 충돌 효과 (원)
            int effectSize = 10 + (int)(10 * progress);
            g2d.fillOval(currentX - effectSize/2, currentY - effectSize/2, effectSize, effectSize);
            
            // 데미지 텍스트 표시 (애니메이션 중간쯤에서)
            if (progress > 0.4f && progress < 0.6f) {
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("-" + anim.damage, anim.targetX + 10, anim.targetY - 10);
            }
        }
        
        // 완료된 애니메이션 제거
        attackAnimations.removeAll(finishedAnimations);
    }
    
    // 이 메서드도 추가하여 일시정지 기능 구현
    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    // 점수 반환 메서드 추가
    public int getScore() {
        return score;
    }
    
    // 점수 설정 메서드 추가
    public void setScore(int score) {
        this.score = score;
    }

    // 타워 선택 리스너 설정 메서드
    public void setTowerSelectListener(TowerSelectListener listener) {
        this.towerSelectListener = listener;
    }

    /**
     * 웨이브 카운트다운 시작
     */
    private void startWaveCountdown() {
        countdown = 3;
        isCountingDown = true;
        countdownStartTime = System.currentTimeMillis();
        
        // 카운트다운 타이머 시작
        Timer countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countdown--;
                if (countdown <= 0) {
                    // 카운트다운 종료, 다음 웨이브 시작
                    isCountingDown = false;
                    ((Timer)e.getSource()).stop();
                    nextWave();
                }
                repaint(); // 화면 갱신
            }
        });
        countdownTimer.start();
        
        // 화면 갱신
        repaint();
    }

    /**
     * 카운트다운 텍스트 그리기 (paintComponent에서 호출)
     */
    private void drawCountdown(Graphics2D g2d) {
        if (isCountingDown && countdown > 0) {
            int width = getWidth();
            int height = getHeight();
            
            // 반투명 배경
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, width, height);
            
            // 웨이브 완료 텍스트
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            String completeText = "웨이브 " + currentWave + " 완료!";
            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(completeText);
            g2d.drawString(completeText, (width - textWidth) / 2, height / 2 - 50);
            
            // 카운트다운 숫자
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 72));
            String countText = String.valueOf(countdown);
            metrics = g2d.getFontMetrics();
            textWidth = metrics.stringWidth(countText);
            g2d.drawString(countText, (width - textWidth) / 2, height / 2 + 50);
        }
    }
} 