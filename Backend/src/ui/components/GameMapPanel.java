package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
        setBackground(new Color(30, 30, 40));
        
        // 그리드 설정
        towerMap = new int[gridRows][gridColumns];
        
        // 적 이동 경로 설정 (임시)
        initializeDefaultPath();
        
        // 마우스 이벤트 설정
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 클릭한 그리드 셀 계산
                int col = e.getX() / GRID_SIZE;
                int row = e.getY() / GRID_SIZE;
                
                // 맵 범위 확인
                if (row >= 0 && row < gridRows && col >= 0 && col < gridColumns) {
                    // 경로와 겹치는지 확인
                    if (isPathCell(row, col)) {
                        return;
                    }
                    
                    // 선택된 셀 업데이트
                    selectedCell = new Point(col, row);
                    repaint();
                    
                    // 여기에 백엔드에서 제공하는 타워 설치/업그레이드 로직 연결
                }
            }
        });
        
        // 마우스 이동 이벤트
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // 마우스가 위치한 그리드 셀 계산
                int col = e.getX() / GRID_SIZE;
                int row = e.getY() / GRID_SIZE;
                
                // 맵 범위 확인
                if (row >= 0 && row < gridRows && col >= 0 && col < gridColumns) {
                    hoveredCell = new Point(col, row);
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
        // 적 이동 및 공격 처리
        updateEnemies();
        
        // 타워 공격 처리
        updateTowers();
        
        // 웨이브 종료 체크
        checkWaveCompletion();
        
        // 화면 갱신
        repaint();
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
        // 각 타워에 대해 처리
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridColumns; col++) {
                if (towerMap[row][col] > 0) {
                    // 타워 ID와 레벨 가져오기
                    int towerId = towerMap[row][col];
                    
                    // 타워 중심 좌표
                    int towerX = col * GRID_SIZE + GRID_SIZE/2;
                    int towerY = row * GRID_SIZE + GRID_SIZE/2;
                    
                    // 타워 공격 범위 (임시로 설정, 실제로는 타워 속성에서 가져와야 함)
                    int attackRange = 3 * GRID_SIZE;
                    
                    // 가장 가까운 적 찾기
                    GameEnemy targetEnemy = findClosestEnemy(towerX, towerY, attackRange);
                    
                    if (targetEnemy != null) {
                        // 적에게 데미지 입히기
                        // 실제로는 타워 속성(데미지, 공격 속도 등)을 고려해야 함
                        targetEnemy.takeDamage(10); // 임시로 10 데미지
                        
                        // 적이 죽었는지 확인
                        if (targetEnemy.getHealth() <= 0) {
                            // 자금 획득
                            money += targetEnemy.getReward();
                            
                            // 적 제거
                            activeEnemies.remove(targetEnemy);
                            
                            // 킬 카운트 증가
                            killedEnemies++;
                            
                            // WaveInfoPanel 업데이트
                            if (waveInfoPanel != null) {
                                waveInfoPanel.updateWaveProgress(killedEnemies, totalEnemies);
                            }
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
                // 마지막 웨이브인지 확인
                if (currentWave < 20) {
                    // 다음 웨이브 자동 시작 (3초 후)
                    Timer waveTimer = new Timer(3000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            nextWave();
                            ((Timer)e.getSource()).stop();
                        }
                    });
                    waveTimer.setRepeats(false);
                    waveTimer.start();
                    
                    // 안내 메시지
                    JOptionPane.showMessageDialog(
                        this, 
                        "웨이브 " + currentWave + " 완료!\n3초 후 다음 웨이브가 시작됩니다.",
                        "웨이브 완료", 
                        JOptionPane.INFORMATION_MESSAGE
                    );
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
     * 타워 설치
     */
    public boolean placeTower(int row, int col, Tower tower) {
        // 이미 타워가 있거나 경로인 경우 설치 불가
        if (towerMap[row][col] > 0 || isPathCell(row, col)) {
            return false;
        }
        
        // 자금 확인
        if (money < tower.getCost()) {
            return false;
        }
        
        // 타워 설치
        towerMap[row][col] = tower.getTowerId();
        
        // 자금 차감
        money -= tower.getCost();
        
        return true;
    }
    
    /**
     * 타워 업그레이드
     */
    public boolean upgradeTower(int row, int col) {
        // 타워가 없는 경우 업그레이드 불가
        if (towerMap[row][col] <= 0) {
            return false;
        }
        
        // 업그레이드 비용 (임시로 50으로 설정)
        int upgradeCost = 50;
        
        // 자금 확인
        if (money < upgradeCost) {
            return false;
        }
        
        // 타워 업그레이드 (ID + 10으로 가정)
        towerMap[row][col] += 10;
        
        // 자금 차감
        money -= upgradeCost;
        
        return true;
    }
    
    /**
     * 적 그리기
     */
    private void drawEnemies(Graphics2D g2d) {
        for (GameEnemy enemy : activeEnemies) {
            int size = enemy.getSize();
            int x = enemy.getX() - size/2;
            int y = enemy.getY() - size/2;
            
            // 적 체력 비율
            float healthRatio = (float)enemy.getHealth() / enemy.getMaxHealth();
            
            // 적 타입에 따른 색상 (임시)
            Color enemyColor;
            switch (enemy.getEnemyId()) {
                case 5: // 보스
                    enemyColor = new Color(200, 50, 50); // 빨강
                    break;
                case 4:
                    enemyColor = new Color(150, 50, 200); // 보라
                    break;
                case 3:
                    enemyColor = new Color(50, 50, 200); // 파랑
                    break;
                case 2:
                    enemyColor = new Color(50, 200, 50); // 초록
                    break;
                default:
                    enemyColor = new Color(200, 200, 50); // 노랑
            }
            
            // 적 그리기
            g2d.setColor(enemyColor);
            g2d.fillOval(x, y, size, size);
            
            // 테두리 그리기
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, size, size);
            
            // 체력바 그리기
            int barWidth = size;
            int barHeight = 5;
            int barX = x;
            int barY = y - 10;
            
            // 체력바 배경
            g2d.setColor(Color.RED);
            g2d.fillRect(barX, barY, barWidth, barHeight);
            
            // 현재 체력
            g2d.setColor(Color.GREEN);
            g2d.fillRect(barX, barY, (int)(barWidth * healthRatio), barHeight);
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
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 그리드 그리기
        drawGrid(g2d);
        
        // 적 경로 그리기
        drawPath(g2d);
        
        // 타워 그리기
        drawTowers(g2d);
        
        // 선택된 셀 표시
        drawSelectedCell(g2d);
        
        // 마우스 호버 셀 표시
        drawHoveredCell(g2d);
        
        // 적 그리기
        drawEnemies(g2d);
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
        TowerController towerController = new TowerController();
        
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridColumns; col++) {
                if (towerMap[row][col] > 0) {
                    int towerId = towerMap[row][col];
                    int x = col * GRID_SIZE;
                    int y = row * GRID_SIZE;
                    
                    // 타워 레벨에 따라 색상 변경
                    Color towerColor;
                    int towerLevel = 1;
                    
                    if (towerId >= 30) {
                        // 3레벨 타워
                        towerColor = new Color(255, 50, 50);
                        towerLevel = 3;
                    } else if (towerId >= 20) {
                        // 2레벨 타워
                        towerColor = new Color(50, 50, 255);
                        towerLevel = 2;
                    } else {
                        // 1레벨 타워
                        towerColor = new Color(50, 200, 50);
                        towerLevel = 1;
                    }
                    
                    // 타워 그리기
                    g2d.setColor(towerColor);
                    g2d.fillRect(x + 5, y + 5, GRID_SIZE - 10, GRID_SIZE - 10);
                    
                    // 테두리
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(x + 5, y + 5, GRID_SIZE - 10, GRID_SIZE - 10);
                    
                    // 타워 레벨 표시
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                    g2d.drawString("Lv" + towerLevel, x + 15, y + GRID_SIZE - 15);
                }
            }
        }
    }
    
    /**
     * 선택된 셀 표시
     */
    private void drawSelectedCell(Graphics2D g2d) {
        if (selectedCell != null) {
            g2d.setColor(new Color(255, 255, 0, 100));
            g2d.setStroke(new BasicStroke(2));
            
            int x = selectedCell.x * GRID_SIZE;
            int y = selectedCell.y * GRID_SIZE;
            
            g2d.fillRect(x, y, GRID_SIZE, GRID_SIZE);
            
            g2d.setColor(new Color(255, 255, 0));
            g2d.drawRect(x, y, GRID_SIZE, GRID_SIZE);
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
        
        // 보스 웨이브인 경우 보스 적 생성 (ID: 5로 가정)
        // 아닌 경우 일반 적 랜덤 생성 (ID: 1~4 중 랜덤)
        int enemyId = isBoss ? 5 : (new Random().nextInt(4) + 1);
        
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
            
            // 보스인 경우 크기 두 배
            if (isBoss) {
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
            if (health < 0) health = 0;
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
} 