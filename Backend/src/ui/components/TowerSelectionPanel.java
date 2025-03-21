package ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import contorller.TowerController;
import model.Tower;
import service.TowerService;
import service.TowerServiceImpl;
import ui.components.common.PixelButton;
import ui.components.common.PixelLabel;
import ui.components.common.UIConstants;

/**
 * 타워 선택 및 구매를 위한 패널
 */
public class TowerSelectionPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // 타워 목록을 표시할 패널
    private JPanel towerListPanel;
    
    // 현재 선택된 타워 정보를 표시할 패널
    private JPanel selectedTowerInfoPanel;
    
    // 타워 액션 버튼 (구매/업그레이드)
    private PixelButton btnBuyTower;
    private PixelButton btnUpgradeTower;
    private PixelButton btnCreateBasicTower; // 1단계 타워 생성 버튼
    
    // 현재 사용 가능한 자금
    private int availableMoney = 100;
    
    // 선택된 타워 정보
    private Tower selectedTower;
    private GameMapPanel gameMapPanel;
    
    /**
     * 기본 생성자
     */
    public TowerSelectionPanel() {
        initialize();
        
        // 타워 목록 로드
        loadTowersFromDB();
    }
    
    /**
     * 패널 초기화
     */
    private void initialize() {
        // 기존 코드 제거하고 새로운 레이아웃 적용
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(40, 40, 60, 220));
        
        // 상단 제목 패널
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        PixelLabel titleLabel = new PixelLabel("타워 컨트롤", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 타워 리스트 패널 초기화
        towerListPanel = new JPanel();
        towerListPanel.setLayout(new BoxLayout(towerListPanel, BoxLayout.Y_AXIS));
        towerListPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(towerListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(250, 120));
        
        // 중앙에 큰 버튼 패널 (메인 기능)
        JPanel mainButtonPanel = new JPanel();
        mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel, BoxLayout.Y_AXIS));
        mainButtonPanel.setOpaque(false);
        mainButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // 크고 눈에 띄는 타워 생성 버튼
        btnCreateBasicTower = new PixelButton("기본 타워 설치");
        btnCreateBasicTower.setFont(new Font("Dialog", Font.BOLD, 16));
        btnCreateBasicTower.setBackground(new Color(30, 150, 70));
        btnCreateBasicTower.setForeground(Color.WHITE);
        btnCreateBasicTower.setPreferredSize(new Dimension(250, 50));
        btnCreateBasicTower.setMinimumSize(new Dimension(250, 50));
        btnCreateBasicTower.setMaximumSize(new Dimension(500, 50));
        btnCreateBasicTower.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCreateBasicTower.setToolTipText("클릭하여 선택한 위치에 기본 타워 설치 (비용: 50)");
        btnCreateBasicTower.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createBasicTower();
            }
        });
        
        // 간격 추가
        mainButtonPanel.add(Box.createVerticalStrut(15));
        mainButtonPanel.add(btnCreateBasicTower);
        mainButtonPanel.add(Box.createVerticalStrut(15));
        
        // 타워 업그레이드 버튼 (비활성화 상태로 시작)
        btnUpgradeTower = new PixelButton("타워 업그레이드");
        btnUpgradeTower.setFont(new Font("Dialog", Font.BOLD, 16));
        btnUpgradeTower.setBackground(new Color(200, 150, 50));
        btnUpgradeTower.setForeground(Color.WHITE);
        btnUpgradeTower.setPreferredSize(new Dimension(250, 50));
        btnUpgradeTower.setMinimumSize(new Dimension(250, 50));
        btnUpgradeTower.setMaximumSize(new Dimension(500, 50));
        btnUpgradeTower.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpgradeTower.setEnabled(false);
        btnUpgradeTower.setToolTipText("선택한 타워를 업그레이드 (타워 선택 필요)");
        btnUpgradeTower.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upgradeSelectedTower();
            }
        });
        
        mainButtonPanel.add(btnUpgradeTower);
        
        // 타워 정보 패널 (하단에 배치)
        selectedTowerInfoPanel = new JPanel();
        selectedTowerInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 150), 2),
            "타워 정보",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Dialog", Font.BOLD, 14),
            Color.WHITE
        ));
        selectedTowerInfoPanel.setBackground(new Color(50, 50, 70, 180));
        selectedTowerInfoPanel.setLayout(new BoxLayout(selectedTowerInfoPanel, BoxLayout.Y_AXIS));
        selectedTowerInfoPanel.setPreferredSize(new Dimension(250, 150));
        
        // 초기 메시지
        JLabel instructions = new JLabel("<html><div style='text-align: center;'>맵에서 타워를 설치할<br>위치를 선택하세요</div></html>");
        instructions.setFont(new Font("Dialog", Font.PLAIN, 14));
        instructions.setForeground(Color.WHITE);
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectedTowerInfoPanel.add(Box.createVerticalStrut(20));
        selectedTowerInfoPanel.add(instructions);
        
        // 자금 정보 (추가)
        JPanel moneyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        moneyPanel.setOpaque(false);
        moneyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel moneyLabel = new JLabel("보유 자금: ");
        moneyLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        moneyLabel.setForeground(Color.WHITE);
        
        JLabel moneyValue = new JLabel("100");
        moneyValue.setFont(new Font("Dialog", Font.BOLD, 16));
        moneyValue.setForeground(new Color(255, 215, 0)); // 골드 색상
        
        moneyPanel.add(moneyLabel);
        moneyPanel.add(moneyValue);
        selectedTowerInfoPanel.add(Box.createVerticalStrut(15));
        selectedTowerInfoPanel.add(moneyPanel);
        
        // 전체 패널 구성
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(mainButtonPanel);
        
        // 레이아웃 조립
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(selectedTowerInfoPanel, BorderLayout.SOUTH);
        
        // 기존의 구매 버튼(btnBuyTower)은 사용하지 않을 예정이므로 제거
        btnBuyTower = null;
    }
    
    /**
     * 샘플 타워 데이터 추가 (임시)
     */
    private void addSampleTowers() {
        // 임시 타워 데이터 (실제로는 API에서 받아올 예정)
        String[] towerNames = {"공격형 타워", "방어형 타워", "지원형 타워", "특수 타워"};
        int[] costs = {100, 150, 200, 300};
        
        for (int i = 0; i < towerNames.length; i++) {
            final int index = i;
            
            // 타워 정보 패널
            JPanel towerPanel = new JPanel();
            towerPanel.setLayout(new BorderLayout());
            towerPanel.setOpaque(false);
            towerPanel.setBackground(new Color(60, 60, 60, 150));
            towerPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
            
            // 타워 이름 및 비용
            JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.setOpaque(false);
            
            PixelLabel lblName = new PixelLabel(towerNames[i], SwingConstants.LEFT);
            lblName.setForeground(Color.WHITE);
            lblName.setFont(UIConstants.getSmallPixelFont());
            
            PixelLabel lblCost = new PixelLabel("비용: $" + costs[i], SwingConstants.RIGHT);
            lblCost.setForeground(UIConstants.GOLD_COLOR);
            lblCost.setFont(UIConstants.getSmallPixelFont());
            
            infoPanel.add(lblName, BorderLayout.WEST);
            infoPanel.add(lblCost, BorderLayout.EAST);
            
            // 타워 패널에 추가
            towerPanel.add(infoPanel, BorderLayout.CENTER);
            
            // 타워 선택 이벤트
            towerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    selectTower(towerNames[index], costs[index]);
                }
                
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    towerPanel.setBackground(new Color(80, 80, 100, 180));
                    towerPanel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 200), 1));
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    towerPanel.setBackground(new Color(60, 60, 60, 150));
                    towerPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
                }
            });
            
            // 타워 리스트에 추가
            towerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            towerPanel.setPreferredSize(new Dimension(200, 50));
            towerListPanel.add(towerPanel);
            
            // 간격 추가
            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            spacer.setPreferredSize(new Dimension(200, 5));
            towerListPanel.add(spacer);
        }
    }
    
    /**
     * 타워 선택 시 정보 표시
     * @param name 타워 이름
     * @param cost 비용
     */
    private void selectTower(String name, int cost) {
        selectedTowerInfoPanel.removeAll();
        
        // 타워 이름
        PixelLabel lblName = new PixelLabel(name, SwingConstants.CENTER);
        lblName.setForeground(UIConstants.GOLD_COLOR);
        lblName.setFont(UIConstants.getPixelFont());
        lblName.setAlignmentX(CENTER_ALIGNMENT);
        
        // 타워 비용
        PixelLabel lblCost = new PixelLabel("비용: $" + cost, SwingConstants.CENTER);
        lblCost.setForeground(Color.WHITE);
        lblCost.setFont(UIConstants.getSmallPixelFont());
        lblCost.setAlignmentX(CENTER_ALIGNMENT);
        
        // 패널에 추가
        selectedTowerInfoPanel.add(lblName);
        selectedTowerInfoPanel.add(lblCost);
        
        // 업그레이드 버튼은 비활성화 (구매 후 활성화)
        btnUpgradeTower.setEnabled(false);
        
        selectedTowerInfoPanel.revalidate();
        selectedTowerInfoPanel.repaint();
    }
    
    /**
     * 선택한 타워 구매 처리
     */
    private void buySelectedTower() {
        // 로그 추가
        System.out.println("타워 구매 시도");
        
        if (selectedTower == null) {
            System.out.println("선택된 타워가 없습니다.");
            return;
        }
        
        if (gameMapPanel == null) {
            System.out.println("게임 맵 패널이 설정되지 않았습니다.");
            return;
        }
        
        // 선택된 셀 가져오기
        Point selectedCell = gameMapPanel.getSelectedCell();
        if (selectedCell == null) {
            System.out.println("선택된 셀이 없습니다. 먼저 맵에서 위치를 선택해주세요.");
            return;
        }
        
        // 셀 좌표 출력
        int row = (int) selectedCell.getY(); 
        int col = (int) selectedCell.getX();
        System.out.println("구매 시도: " + selectedTower.getTowerName() + " (ID:" + selectedTower.getTowerId() 
                          + "), 위치: (" + col + "," + row + ")");
        
        // 타워 구매 시도
        boolean success = gameMapPanel.placeTower(row, col, selectedTower);
        
        if (success) {
            System.out.println("타워 설치 성공!");
            // 설치 성공 후 잔액 업데이트
            updateAvailableMoney(gameMapPanel.getMoney());
            // 선택 초기화
            gameMapPanel.clearSelection();
        } else {
            System.out.println("타워 설치 실패. 자금 부족 또는 타워 설치가 불가능한 위치입니다.");
        }
    }
    
    /**
     * 선택된 타워 업그레이드
     */
    public void upgradeSelectedTower() {
        if (gameMapPanel == null) {
            System.out.println("GameMapPanel이 설정되지 않았습니다.");
            return;
        }
        
        // 현재 선택된 셀 정보 가져오기
        Point selectedCell = gameMapPanel.getSelectedCell();
        
        if (selectedCell == null) {
            JOptionPane.showMessageDialog(this, "먼저 업그레이드할 타워를 선택해주세요.", 
                                         "업그레이드 실패", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int row = selectedCell.y;
        int col = selectedCell.x;
        
        // GameMapPanel을 통해 타워 업그레이드 시도
        System.out.println("타워 업그레이드 시도: " + col + ", " + row);
        boolean success = gameMapPanel.upgradeTower(row, col);
        
        if (success) {
            System.out.println("타워 업그레이드 성공!");
            // 업그레이드 성공 메시지 표시
            JOptionPane.showMessageDialog(this, "타워 업그레이드에 성공했습니다!", 
                                         "업그레이드 성공", JOptionPane.INFORMATION_MESSAGE);
            
            // 업그레이드 성공 애니메이션 또는 효과 추가
            gameMapPanel.showTowerPlacementEffect(col, row);
            
            // 선택 해제
            gameMapPanel.clearSelection();
        } else {
            System.out.println("타워 업그레이드 실패");
            // 실패 메시지는 GameMapPanel의 upgradeTower 메서드 내에서 표시됨
        }
    }
    
    /**
     * 사용 가능한 자금 업데이트
     * @param money 현재 자금
     */
    public void updateAvailableMoney(int money) {
        this.availableMoney = money;
        
        // btnBuyTower가 null이므로 이 부분 제거
        // btnBuyTower와 관련된 코드 삭제
    }
    
    /**
     * 패널 크기 조정
     * @param width 창 너비
     * @param height 창 높이
     */
    public void adjustSize(int width, int height) {
        // 패널 크기 조정
        int panelWidth = Math.max(200, width / 5);
        setPreferredSize(new Dimension(panelWidth, height));
        
        // 타워 리스트 아이템 크기 조정
        for (int i = 0; i < towerListPanel.getComponentCount(); i++) {
            if (towerListPanel.getComponent(i) instanceof JPanel) {
                JPanel towerPanel = (JPanel) towerListPanel.getComponent(i);
                towerPanel.setMaximumSize(new Dimension(panelWidth - 20, 50));
                towerPanel.setPreferredSize(new Dimension(panelWidth - 20, 50));
            }
        }
        
        revalidate();
        repaint();
    }

    /**
     * GameMapPanel 설정
     */
    public void setGameMapPanel(GameMapPanel panel) {
        this.gameMapPanel = panel;
        
        // 타워 선택 리스너 설정
        if (panel != null) {
            panel.setTowerSelectListener(new GameMapPanel.TowerSelectListener() {
                @Override
                public void onTowerSelected(int towerId, int row, int col) {
                    // 타워 선택 시 업그레이드 버튼 활성화, 생성 버튼 비활성화
                    btnUpgradeTower.setEnabled(true);
                    btnCreateBasicTower.setEnabled(false);
                    
                    // btnBuyTower는 null이므로 관련 코드 제거
                    
                    // 선택된 타워 정보 표시
                    showSelectedTowerInfo(towerId, row, col);
                }
                
                @Override
                public void onEmptyCellSelected(int row, int col) {
                    // 빈 셀 선택 시 생성 버튼 활성화, 업그레이드 버튼 비활성화
                    btnUpgradeTower.setEnabled(false);
                    btnCreateBasicTower.setEnabled(true);
                    
                    // btnBuyTower와 selectedTower 관련 코드 제거
                    
                    // 타워 정보 초기화
                    clearTowerInfo();
                }
            });
        }
    }

    /**
     * 선택된 타워 정보 표시
     */
    private void showSelectedTowerInfo(int towerId, int row, int col) {
        // 선택된 타워 정보 패널 초기화
        selectedTowerInfoPanel.removeAll();
        
        // 타워 레벨에 따른 정보
        String type = "불명";
        String level = "?";
        String damage = "?";
        String range = "?";
        int upgradeCost = 0;
        boolean canUpgrade = true;
        
        switch (towerId) {
            case 1:
                type = "기본 타워";
                level = "1";
                damage = "10";
                range = "3";
                upgradeCost = 50;
                break;
            case 2:
                type = "중급 타워";
                level = "2";
                damage = "20";
                range = "4";
                upgradeCost = 100;
                break;
            case 3:
                type = "고급 타워";
                level = "3";
                damage = "35";
                range = "5";
                canUpgrade = false; // 최대 레벨
                break;
            default:
                canUpgrade = false;
        }
        
        // 정보 표시
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel typeLabel = new JLabel("타입: " + type);
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel levelLabel = new JLabel("레벨: " + level);
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel posLabel = new JLabel("위치: (" + col + ", " + row + ")");
        posLabel.setForeground(Color.WHITE);
        posLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel damageLabel = new JLabel("공격력: " + damage);
        damageLabel.setForeground(Color.WHITE);
        damageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel rangeLabel = new JLabel("범위: " + range);
        rangeLabel.setForeground(Color.WHITE);
        rangeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(typeLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(levelLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(posLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(damageLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(rangeLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        
        if (canUpgrade) {
            JLabel upgradeLabel = new JLabel("업그레이드 비용: " + upgradeCost);
            upgradeLabel.setForeground(new Color(255, 215, 0));
            upgradeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(upgradeLabel);
        } else {
            JLabel maxLabel = new JLabel("최대 레벨");
            maxLabel.setForeground(new Color(255, 100, 100));
            maxLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            infoPanel.add(maxLabel);
        }
        
        selectedTowerInfoPanel.add(infoPanel);
        btnUpgradeTower.setEnabled(canUpgrade);
        
        selectedTowerInfoPanel.revalidate();
        selectedTowerInfoPanel.repaint();
    }

    /**
     * 타워 정보 초기화
     */
    private void clearTowerInfo() {
        selectedTowerInfoPanel.removeAll();
        
        JLabel label = new JLabel("<html><div style='text-align: center;'>타워를 설치할<br>위치를 선택하세요</div></html>");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Dialog", Font.PLAIN, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        selectedTowerInfoPanel.add(Box.createVerticalStrut(20));
        selectedTowerInfoPanel.add(label);
        
        selectedTowerInfoPanel.revalidate();
        selectedTowerInfoPanel.repaint();
    }

    /**
     * 타워 목록 로드
     */
    private void loadTowersFromDB() {
        towerListPanel.removeAll();
        
        try {
            TowerController towerController = new TowerController();
            Tower tower = towerController.getFirstTower();
            
            if (tower != null) {
                // 타워 정보 패널 생성
                JPanel towerPanel = createTowerPanel(tower);
                
                // 타워 리스트에 추가
                towerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                towerPanel.setPreferredSize(new Dimension(200, 50));
                towerListPanel.add(towerPanel);
                
                // 간격 추가
                JPanel spacer = new JPanel();
                spacer.setOpaque(false);
                spacer.setPreferredSize(new Dimension(200, 5));
                towerListPanel.add(spacer);
            } else {
                // 타워가 없을 경우 메시지 표시
                PixelLabel lblNoTower = new PixelLabel("사용 가능한 타워가 없습니다", SwingConstants.CENTER);
                lblNoTower.setForeground(Color.LIGHT_GRAY);
                lblNoTower.setFont(UIConstants.getSmallPixelFont());
                towerListPanel.add(lblNoTower);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 오류 메시지 표시
            PixelLabel lblError = new PixelLabel("타워 정보를 불러올 수 없습니다", SwingConstants.CENTER);
            lblError.setForeground(Color.RED);
            lblError.setFont(UIConstants.getSmallPixelFont());
            towerListPanel.add(lblError);
        }
        
        towerListPanel.revalidate();
        towerListPanel.repaint();
    }

    /**
     * 타워 패널 생성
     */
    private JPanel createTowerPanel(final Tower tower) {
        // 타워 정보 패널
        JPanel towerPanel = new JPanel();
        towerPanel.setLayout(new BorderLayout());
        towerPanel.setOpaque(false);
        towerPanel.setBackground(new Color(60, 60, 60, 150));
        towerPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        
        // 타워 이름 및 비용
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        
        PixelLabel lblName = new PixelLabel(tower.getTowerName(), SwingConstants.LEFT);
        lblName.setForeground(Color.WHITE);
        lblName.setFont(UIConstants.getSmallPixelFont());
        
        PixelLabel lblCost = new PixelLabel("비용: $" + tower.getCost(), SwingConstants.RIGHT);
        lblCost.setForeground(UIConstants.GOLD_COLOR);
        lblCost.setFont(UIConstants.getSmallPixelFont());
        
        infoPanel.add(lblName, BorderLayout.WEST);
        infoPanel.add(lblCost, BorderLayout.EAST);
        
        // 타워 패널에 추가
        towerPanel.add(infoPanel, BorderLayout.CENTER);
        
        // 타워 선택 이벤트
        towerPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectTower(tower.getTowerName(), tower.getCost());
                selectedTower = tower; // 선택된 타워 저장
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                towerPanel.setBackground(new Color(80, 80, 100, 180));
                towerPanel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 200), 1));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                towerPanel.setBackground(new Color(60, 60, 60, 150));
                towerPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
            }
        });
        
        return towerPanel;
    }

    /**
     * 기본 타워 생성 (1레벨 타워)
     * GameRoomFrame에서도 접근할 수 있도록 public으로 변경
     */
    public void createBasicTower() {
        if (gameMapPanel == null) {
            System.out.println("게임 맵 패널이 설정되지 않았습니다.");
            return;
        }
        
        // 선택된 셀 가져오기
        Point selectedCell = gameMapPanel.getSelectedCell();
        if (selectedCell == null) {
            JOptionPane.showMessageDialog(this, "먼저 맵에서 타워를 설치할 위치를 선택해주세요.", 
                                         "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // 셀 위치
        int row = (int) selectedCell.getY();
        int col = (int) selectedCell.getX();
        
        // 타워 서비스에서 1레벨 타워 가져오기
        TowerService towerService = new TowerServiceImpl();
        Tower tower = null;
        
        try {
            tower = towerService.getFirstTower();
            if (tower == null) {
                JOptionPane.showMessageDialog(this, "타워 정보를 가져올 수 없습니다.", 
                                             "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            System.out.println("타워 가져오기 실패: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // 돈이 충분한지 확인
        if (gameMapPanel.getMoney() < tower.getCost()) {
            JOptionPane.showMessageDialog(this, "돈이 부족합니다. 필요: " + tower.getCost() + 
                                         ", 보유: " + gameMapPanel.getMoney(), 
                                         "자금 부족", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 타워 설치 시도
        boolean success = gameMapPanel.placeTower(row, col, tower);
        
        if (success) {
            // 타워 설치 성공
            JOptionPane.showMessageDialog(this, "랜덤 1레벨 타워가 설치되었습니다: " + tower.getTowerName(), 
                                         "설치 성공", JOptionPane.INFORMATION_MESSAGE);
            // 선택 초기화
            gameMapPanel.clearSelection();
            // 잔액 업데이트
            updateAvailableMoney(gameMapPanel.getMoney());
        } else {
            JOptionPane.showMessageDialog(this, "타워 설치에 실패했습니다. 경로 위이거나 이미 타워가 있는 위치입니다.", 
                                         "설치 실패", JOptionPane.WARNING_MESSAGE);
        }
    }
}