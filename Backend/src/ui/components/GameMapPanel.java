package ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * 게임 맵을 표시하고 타워 배치 및 게임 진행을 관리하는 패널
 */
public class GameMapPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // 그리드 설정
    private static final int GRID_SIZE = 40; // 그리드 한 칸의 크기
    private int gridRows = 15;      // 그리드 행 수
    private int gridColumns = 20;   // 그리드 열 수
    
    // 게임 오브젝트 위치 관리
    private int[][] towerMap;       // 타워가 배치된 위치 (0: 빈 공간, >0: 타워 ID)
    private List<Point> pathPoints; // 적이 이동하는 경로 포인트
    private Point selectedCell;     // 현재 선택된 셀
    private Point hoveredCell;      // 마우스가 위치한 셀
    
    // 게임 오브젝트 정보
    private List<Enemy> enemies;    // 현재 맵에 있는 적
    private int selectedTowerId = 0; // 현재 선택된 타워 ID (0: 선택 없음)
    
    /**
     * 기본 생성자
     */
    public GameMapPanel() {
        initialize();
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
        
        // 적 리스트 초기화
        enemies = new ArrayList<>();
        
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
                    if (!isPathCell(row, col)) {
                        // 타워 설치 또는 업그레이드 가능한 위치
                        selectedCell = new Point(col, row);
                        
                        // 타워가 이미 있는지 확인
                        if (towerMap[row][col] > 0) {
                            // 타워 업그레이드 또는 정보 표시
                            System.out.println("타워 선택: " + towerMap[row][col] + " at (" + row + ", " + col + ")");
                        } else {
                            // 새 타워 설치
                            System.out.println("타워 설치 위치 선택: (" + row + ", " + col + ")");
                        }
                        
                        repaint();
                    }
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
        
        // 경로 포인트 추가 (입구에서 출구까지)
        pathPoints.add(new Point(0, 5));      // 입구
        pathPoints.add(new Point(5, 5));      // 오른쪽으로 이동
        pathPoints.add(new Point(5, 2));      // 위로 이동
        pathPoints.add(new Point(10, 2));     // 오른쪽으로 이동
        pathPoints.add(new Point(10, 8));     // 아래로 이동
        pathPoints.add(new Point(15, 8));     // 오른쪽으로 이동
        pathPoints.add(new Point(15, 3));     // 위로 이동
        pathPoints.add(new Point(19, 3));     // 출구
    }
    
    /**
     * 특정 위치가 적 경로인지 확인
     * @param row 행 인덱스
     * @param col 열 인덱스
     * @return 경로 포함 여부
     */
    private boolean isPathCell(int row, int col) {
        // 경로 포인트 간 직선 체크
        for (int i = 0; i < pathPoints.size() - 1; i++) {
            Point p1 = pathPoints.get(i);
            Point p2 = pathPoints.get(i + 1);
            
            // 수직 경로
            if (p1.x == p2.x && p1.x == col && 
                Math.min(p1.y, p2.y) <= row && row <= Math.max(p1.y, p2.y)) {
                return true;
            }
            
            // 수평 경로
            if (p1.y == p2.y && p1.y == row && 
                Math.min(p1.x, p2.x) <= col && col <= Math.max(p1.x, p2.x)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 타워 설치
     * @param row 행 위치
     * @param col 열 위치
     * @param towerId 타워 ID
     * @return 설치 성공 여부
     */
    public boolean placeTower(int row, int col, int towerId) {
        // 설치 가능 위치 확인
        if (row >= 0 && row < gridRows && col >= 0 && col < gridColumns &&
            towerMap[row][col] == 0 && !isPathCell(row, col)) {
            
            towerMap[row][col] = towerId;
            return true;
        }
        
        return false;
    }
    
    /**
     * 타워 업그레이드
     * @param row 행 위치
     * @param col 열 위치
     * @return 업그레이드 성공 여부
     */
    public boolean upgradeTower(int row, int col) {
        // 업그레이드 가능 확인
        if (row >= 0 && row < gridRows && col >= 0 && col < gridColumns &&
            towerMap[row][col] > 0) {
            
            // 타워 ID 증가 (임시 - 실제로는 타워 객체의 레벨 또는 다른 타워로 교체)
            towerMap[row][col] += 100;
            return true;
        }
        
        return false;
    }
    
    /**
     * 현재 타워 선택 설정
     * @param towerId 선택한 타워 ID
     */
    public void setSelectedTower(int towerId) {
        this.selectedTowerId = towerId;
    }
    
    /**
     * 적 추가
     * @param enemy 추가할 적 객체
     */
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }
    
    /**
     * 게임 상태 업데이트 (프레임마다 호출)
     */
    public void updateGameState() {
        // 적 이동 업데이트
        updateEnemies();
        
        // 화면 갱신
        repaint();
    }
    
    /**
     * 적 이동 및 상태 업데이트
     */
    private void updateEnemies() {
        List<Enemy> toRemove = new ArrayList<>();
        
        for (Enemy enemy : enemies) {
            // 적 이동
            enemy.move();
            
            // 제거할 적 확인 (사망 또는 목적지 도달)
            if (enemy.isDead() || enemy.hasReachedEnd()) {
                toRemove.add(enemy);
            }
        }
        
        // 제거할 적 정리
        enemies.removeAll(toRemove);
    }
    
    /**
     * 캔버스 크기 조정
     * @param width 창 너비
     * @param height 창 높이
     */
    public void adjustSize(int width, int height) {
        // 게임 맵 크기 계산
        int mapWidth = gridColumns * GRID_SIZE;
        int mapHeight = gridRows * GRID_SIZE;
        
        // 최소 크기 설정
        setMinimumSize(new Dimension(mapWidth, mapHeight));
        setPreferredSize(new Dimension(mapWidth, mapHeight));
        
        revalidate();
        repaint();
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
     * 타워 그리기
     */
    private void drawTowers(Graphics2D g2d) {
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridColumns; col++) {
                if (towerMap[row][col] > 0) {
                    int towerId = towerMap[row][col];
                    int x = col * GRID_SIZE;
                    int y = row * GRID_SIZE;
                    
                    // 타워 그리기 (임시 - 실제로는 타워 이미지 사용)
                    if (towerId < 100) {
                        // 레벨 1 타워
                        g2d.setColor(new Color(80, 80, 200));
                    } else if (towerId < 200) {
                        // 레벨 2 타워
                        g2d.setColor(new Color(80, 200, 80));
                    } else {
                        // 레벨 3 타워
                        g2d.setColor(new Color(200, 80, 80));
                    }
                    
                    g2d.fillRect(x + 5, y + 5, GRID_SIZE - 10, GRID_SIZE - 10);
                    
                    // 타워 레벨 표시
                    g2d.setColor(Color.WHITE);
                    String levelText = "L" + (1 + towerId / 100);
                    g2d.drawString(levelText, x + GRID_SIZE / 2 - 5, y + GRID_SIZE / 2 + 5);
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
     * 적 그리기
     */
    private void drawEnemies(Graphics2D g2d) {
        for (Enemy enemy : enemies) {
            enemy.draw(g2d, GRID_SIZE);
        }
    }
    
    /**
     * 적 클래스 (임시 - 실제로는 별도 클래스 파일로 분리)
     */
    class Enemy {
        private double x, y;        // 위치 (그리드 단위)
        private double speed = 0.05; // 이동 속도
        private int health = 100;    // 체력
        private int pathIndex = 0;   // 현재 따라가는 경로 포인트 인덱스
        
        public Enemy(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        /**
         * 적 이동 처리
         */
        public void move() {
            if (pathIndex >= pathPoints.size() - 1) return;
            
            Point currentTarget = pathPoints.get(pathIndex + 1);
            double targetX = currentTarget.x;
            double targetY = currentTarget.y;
            
            // 목표 지점까지의 방향 계산
            double dx = targetX - x;
            double dy = targetY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance < speed) {
                // 다음 경로 포인트로 이동
                x = targetX;
                y = targetY;
                pathIndex++;
            } else {
                // 현재 방향으로 이동
                x += (dx / distance) * speed;
                y += (dy / distance) * speed;
            }
        }
        
        /**
         * 적 그리기
         */
        public void draw(Graphics2D g2d, int gridSize) {
            int screenX = (int)(x * gridSize + gridSize / 2);
            int screenY = (int)(y * gridSize + gridSize / 2);
            int size = gridSize / 3;
            
            // 적 모양 그리기 (임시)
            g2d.setColor(new Color(255, 50, 50));
            g2d.fillOval(screenX - size/2, screenY - size/2, size, size);
            
            // 체력바 그리기
            int healthBarWidth = gridSize / 2;
            int healthBarHeight = 4;
            int healthWidth = (int)(healthBarWidth * (health / 100.0));
            
            g2d.setColor(new Color(60, 60, 60));
            g2d.fillRect(screenX - healthBarWidth/2, screenY - size/2 - 8, healthBarWidth, healthBarHeight);
            
            g2d.setColor(new Color(50, 200, 50));
            g2d.fillRect(screenX - healthBarWidth/2, screenY - size/2 - 8, healthWidth, healthBarHeight);
        }
        
        /**
         * 적이 죽었는지 확인
         */
        public boolean isDead() {
            return health <= 0;
        }
        
        /**
         * 적이 맵 끝에 도달했는지 확인
         */
        public boolean hasReachedEnd() {
            return pathIndex >= pathPoints.size() - 1;
        }
        
        /**
         * 체력 감소
         */
        public void takeDamage(int damage) {
            health -= damage;
            if (health < 0) health = 0;
        }
    }
} 