package ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

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
    
    // 현재 사용 가능한 자금
    private int availableMoney = 100;
    
    /**
     * 기본 생성자
     */
    public TowerSelectionPanel() {
        initialize();
    }
    
    /**
     * 패널 초기화
     */
    private void initialize() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        
        // 타이틀
        PixelLabel lblTitle = new PixelLabel("타워 선택", SwingConstants.CENTER);
        lblTitle.setForeground(UIConstants.WHITE_COLOR);
        lblTitle.setFont(UIConstants.getPixelFont());
        lblTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 타워 목록 패널
        towerListPanel = new JPanel();
        towerListPanel.setLayout(new BoxLayout(towerListPanel, BoxLayout.Y_AXIS));
        towerListPanel.setOpaque(false);
        
        // 스크롤 패널에 타워 목록 추가
        JScrollPane scrollPane = new JScrollPane(towerListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // 현재 선택된 타워 정보 패널
        selectedTowerInfoPanel = new JPanel();
        selectedTowerInfoPanel.setLayout(new BoxLayout(selectedTowerInfoPanel, BoxLayout.Y_AXIS));
        selectedTowerInfoPanel.setOpaque(false);
        selectedTowerInfoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200, 120), 1),
                "타워 정보",
                SwingConstants.LEFT,
                SwingConstants.TOP,
                UIConstants.getPixelFont(),
                Color.WHITE));
        
        // 임시 정보 레이블
        PixelLabel lblNoTowerSelected = new PixelLabel("타워를 선택하세요", SwingConstants.CENTER);
        lblNoTowerSelected.setForeground(Color.LIGHT_GRAY);
        lblNoTowerSelected.setFont(UIConstants.getSmallPixelFont());
        selectedTowerInfoPanel.add(lblNoTowerSelected);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 구매 버튼
        btnBuyTower = new PixelButton("구매", UIConstants.getPixelFont());
        btnBuyTower.setEnabled(false);
        btnBuyTower.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buySelectedTower();
            }
        });
        
        // 업그레이드 버튼
        btnUpgradeTower = new PixelButton("업그레이드", UIConstants.getPixelFont());
        btnUpgradeTower.setEnabled(false);
        btnUpgradeTower.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upgradeSelectedTower();
            }
        });
        
        buttonPanel.add(btnBuyTower);
        buttonPanel.add(btnUpgradeTower);
        
        // 샘플 타워 항목 추가 (임시)
        addSampleTowers();
        
        // 메인 패널에 배치
        add(lblTitle, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(selectedTowerInfoPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 샘플 타워 데이터 추가 (임시)
     */
    private void addSampleTowers() {
        // 임시 타워 데이터 (실제로는 API에서 받아올 예정)
        String[] towerNames = {"공격형 타워", "방어형 타워", "지원형 타워", "특수 타워"};
        int[] costs = {100, 150, 200, 300};
        String[] descriptions = {
            "기본 공격 타워: 적에게 데미지를 입힙니다.",
            "방어 타워: 적의 이동을 늦춥니다.",
            "지원 타워: 주변 타워의 공격력을 증가시킵니다.",
            "특수 타워: 범위 공격이 가능합니다."
        };
        
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
                    selectTower(towerNames[index], costs[index], descriptions[index]);
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
     * @param description 설명
     */
    private void selectTower(String name, int cost, String description) {
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
        
        // 타워 설명
        PixelLabel lblDescription = new PixelLabel("<html><div style='text-align: center;'>" + description + "</div></html>", SwingConstants.CENTER);
        lblDescription.setForeground(Color.LIGHT_GRAY);
        lblDescription.setFont(UIConstants.getSmallPixelFont());
        lblDescription.setAlignmentX(CENTER_ALIGNMENT);
        
        // 패널에 추가
        selectedTowerInfoPanel.add(lblName);
        selectedTowerInfoPanel.add(lblCost);
        selectedTowerInfoPanel.add(lblDescription);
        
        // 구매 버튼 활성화 (비용이 가능한 경우)
        btnBuyTower.setEnabled(cost <= availableMoney);
        
        // 업그레이드 버튼은 비활성화 (구매 후 활성화)
        btnUpgradeTower.setEnabled(false);
        
        selectedTowerInfoPanel.revalidate();
        selectedTowerInfoPanel.repaint();
    }
    
    /**
     * 현재 선택된 타워 구매
     */
    private void buySelectedTower() {
        // 구매 로직 (API 연동 필요)
        System.out.println("타워 구매 요청");
    }
    
    /**
     * 설치된 타워 업그레이드
     */
    private void upgradeSelectedTower() {
        // 업그레이드 로직 (API 연동 필요)
        System.out.println("타워 업그레이드 요청");
    }
    
    /**
     * 사용 가능한 자금 업데이트
     * @param money 현재 자금
     */
    public void updateAvailableMoney(int money) {
        this.availableMoney = money;
        
        // 구매 버튼 상태 업데이트 (선택된 타워가 있다면)
        if (btnBuyTower.isEnabled()) {
            // 이미 선택된 타워의 비용을 확인하고 구매 가능 여부 업데이트
            // 현재는 임시 구현이므로 생략
        }
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
} 