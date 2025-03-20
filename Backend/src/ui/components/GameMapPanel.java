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
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
        // 백엔드에서 제공하는 게임 업데이트 로직 연결
        // 화면 갱신
        repaint();
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
        
        // 타워 그리기 (백엔드에서 제공)
        drawTowers(g2d);
        
        // 선택된 셀 표시
        drawSelectedCell(g2d);
        
        // 마우스 호버 셀 표시
        drawHoveredCell(g2d);
        
        // 적 그리기 (백엔드에서 제공)
        // drawEnemies(g2d);
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
     * 타워 그리기 (임시 구현)
     */
    private void drawTowers(Graphics2D g2d) {
        // 여기에 백엔드에서 제공하는 타워 렌더링 로직 연결
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridColumns; col++) {
                if (towerMap[row][col] > 0) {
                    int towerId = towerMap[row][col];
                    int x = col * GRID_SIZE;
                    int y = row * GRID_SIZE;
                    
                    // 타워 그리기 (임시 - 실제로는 백엔드 연동)
                    g2d.setColor(new Color(80, 80, 200));
                    g2d.fillRect(x + 5, y + 5, GRID_SIZE - 10, GRID_SIZE - 10);
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
} 