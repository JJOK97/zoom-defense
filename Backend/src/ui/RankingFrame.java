package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import contorller.RankingController;
import contorller.UserController;
import model.Ranking;
import model.User;
import ui.common.UIConstants;
import ui.components.PixelBackgroundPanel;
import ui.components.PixelButton;
import ui.components.PixelLabel;
import ui.components.PixelTextField;

public class RankingFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	// 컴포넌트
	private PixelLabel lblTitle;
	private JTable topRankingTable;
	private JTable userRankingTable;
	private PixelTextField txtUserSearch;
	private PixelButton btnSearch;
	private PixelButton btnGoBack;

	// 애니메이션 관련 변수
	private Timer animationTimer;
	private int titleBounce = 0;
	private boolean bounceUp = false;
	private int colorPhase = 0;

	// 창 상태 유지 관련 변수
	private static Rectangle frameBounds = null;
	private static boolean isMaximized = false;

	// 현재 로그인한 사용자 정보
	private User loggedInUser;

	// 컨트롤러
	private RankingController rankingController;
	private UserController userController;

	// 새로운 멤버 변수 추가
	private JTable rankingTable;
	private PixelLabel lblTableHeader;
	private PixelButton btnBackToTop;
	private boolean isShowingTopRankings = true;

	/**
	 * 사용자 정보로 생성하는 생성자
	 */
	public RankingFrame(User user) {
		this.loggedInUser = user;
		this.rankingController = new RankingController();
		this.userController = new UserController();

		setTitle("ZOOM Defense - 랭킹 - " + user.getNickname() + "님");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setSize(UIConstants.getScreenSize());
		setLocationRelativeTo(null);
		setResizable(true);

		// 메인 패널 설정
		JPanel mainPanel = createMainPanel();
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

		// 초기 데이터 로드
		loadTopRankings();
	}

	/**
	 * 창 상태를 유지하는 생성자
	 */
	public RankingFrame(User user, Rectangle bounds, boolean maximized) {
		this.loggedInUser = user;
		this.rankingController = new RankingController();
		this.userController = new UserController();

		setTitle("ZOOM Defense - 랭킹 - " + user.getNickname() + "님");
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
		JPanel mainPanel = createMainPanel();
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

		// 초기 데이터 로드
		loadTopRankings();
	}

	/**
	 * 창 크기에 따라 컴포넌트 크기 조정
	 */
	private void adjustComponentSizes() {
		int width = getWidth();
		int height = getHeight();

		// 폰트 크기 조정
		if (lblTitle != null) {
			lblTitle.setFont(UIConstants.getScaledTitleFont(width));
		}

		// 테이블 크기 조정
		if (topRankingTable != null) {
			int tableWidth = Math.max(400, width / 2);
			int tableHeight = Math.max(300, height / 3);
			topRankingTable.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));
		}

		if (userRankingTable != null) {
			int tableWidth = Math.max(400, width / 2);
			int tableHeight = Math.max(200, height / 4);
			userRankingTable.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));
		}

		// 버튼 크기 조정
		if (btnGoBack != null) {
			int buttonWidth = Math.max(150, width / 8);
			int buttonHeight = Math.max(40, height / 20);
			btnGoBack.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		}

		if (btnSearch != null) {
			int buttonWidth = Math.max(100, width / 12);
			int buttonHeight = Math.max(30, height / 25);
			btnSearch.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		}

		// 테이블 크기 조정 후 행 높이도 함께 조정
		if (rankingTable != null) {
			// 테이블 가로 길이 줄임
			int tableWidth = Math.max(350, width / 3);
			int tableHeight = Math.max(300, height / 3);
			JScrollPane scrollPane = (JScrollPane) rankingTable.getParent().getParent();
			scrollPane.setPreferredSize(new Dimension(tableWidth, tableHeight));
			
			// 행 높이 조정
			adjustRowHeight();
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
		int topMargin = (int) (getHeight() * 0.08);
		titlePanel.setBorder(new EmptyBorder(topMargin, 0, 10, 0));

		lblTitle = new PixelLabel("RANKING TOP 10", SwingConstants.CENTER);
		lblTitle.setFont(UIConstants.getTitleFont());
		lblTitle.setForeground(UIConstants.GOLD_COLOR);

		titlePanel.add(lblTitle, BorderLayout.CENTER);

		// 중앙 패널 - 전체 컨텐츠를 담는 메인 컨테이너
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		centerPanel.setOpaque(false);

		// 컨텐츠를 담을 패널
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout(0, 15)); // 상하 간격 추가
		contentPanel.setOpaque(false);
		contentPanel.setBorder(new EmptyBorder(10, 50, 20, 50)); // 여백 추가

		// 사용자 검색 패널
		JPanel userSearchPanel = createUserSearchPanel();
		contentPanel.add(userSearchPanel, BorderLayout.NORTH);

		// 랭킹 테이블 패널
		JPanel rankingPanel = createRankingPanel();
		contentPanel.add(rankingPanel, BorderLayout.CENTER);

		// 뒤로가기 버튼 패널
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setOpaque(false);
		buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

		btnGoBack = new PixelButton(" BACK", UIConstants.getPixelFont());
		btnGoBack.setPreferredSize(new Dimension(120, 40));
		btnGoBack.setForeground(new Color(220, 220, 220));
		btnGoBack.setBackground(new Color(60, 60, 60));
		btnGoBack.setBorderColor(new Color(160, 160, 160));
		btnGoBack.setPixelBorder(3);
		btnGoBack.setIconName("back");
		btnGoBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 현재 창의 상태 저장
				boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
				Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();

				// 게임 선택 화면으로 돌아가기
				dispose();
				GameSelectionFrame gameSelection = new GameSelectionFrame(loggedInUser);
				gameSelection.setBounds(currentBounds);
				if (currentMaximized) {
					gameSelection.setExtendedState(JFrame.MAXIMIZED_BOTH);
				}
				gameSelection.setVisible(true);
			}
		});
		buttonPanel.add(btnGoBack);

		contentPanel.add(buttonPanel, BorderLayout.SOUTH);

		// 중앙 정렬을 위해 GridBagLayout 사용
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		centerPanel.add(contentPanel, gbc);

		// 메인 패널에 배치
		panel.add(titlePanel, BorderLayout.NORTH);
		panel.add(centerPanel, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * 하나의 랭킹 테이블 패널 생성
	 */
	private JPanel createRankingPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);

		// 현재 보여지는 랭킹 타입 제목
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setOpaque(false);
		headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

		lblTableHeader = new PixelLabel("TOP PLAYERS", SwingConstants.CENTER);
		lblTableHeader.setFont(UIConstants.getPixelFont().deriveFont(20f)); // 폰트 크기 증가
		lblTableHeader.setForeground(new Color(255, 255, 100)); // 밝은 노란색
		headerPanel.add(lblTableHeader, BorderLayout.CENTER);

		// 상위 랭킹으로 돌아가는 버튼 (검색 후 표시됨)
		btnBackToTop = new PixelButton("TOP 10", UIConstants.getPixelFont());
		btnBackToTop.setPreferredSize(new Dimension(85, 28));
		btnBackToTop.setForeground(new Color(255, 220, 100));
		btnBackToTop.setBackground(new Color(80, 40, 0));
		btnBackToTop.setBorderColor(new Color(255, 180, 0));
		btnBackToTop.setPixelBorder(2);
		btnBackToTop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 상위 랭킹으로 다시 전환
				loadTopRankings();
				btnBackToTop.setVisible(false);
				lblTableHeader.setText("TOP PLAYERS");
				lblTableHeader.setForeground(new Color(255, 255, 100));
			}
		});
		btnBackToTop.setVisible(false); // 초기에는 숨김

		JPanel headerButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		headerButtonPanel.setOpaque(false);
		headerButtonPanel.add(btnBackToTop);
		headerPanel.add(headerButtonPanel, BorderLayout.EAST);

		panel.add(headerPanel, BorderLayout.NORTH);

		// 테이블 모델 생성
		String[] columns = { "RANK", "PLAYER", "SCORE", "DATE" };
		DefaultTableModel model = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // 모든 셀을 편집 불가능하게 설정
			}
		};

		// 테이블 생성
		rankingTable = new JTable(model);
		rankingTable.getTableHeader().setReorderingAllowed(false);
		rankingTable.getTableHeader().setResizingAllowed(false);
		rankingTable.setGridColor(new Color(80, 80, 80));
		rankingTable.setShowGrid(true);
		rankingTable.setIntercellSpacing(new Dimension(0, 0));
		rankingTable.setFocusable(false);
		rankingTable.setRowSelectionAllowed(true);
		rankingTable.setFont(UIConstants.getPixelFont().deriveFont(16f)); // 폰트 크기 증가
		rankingTable.setBackground(new Color(30, 30, 40));
		rankingTable.setForeground(new Color(220, 220, 220));

		// 테이블 헤더 스타일링 - 가독성 개선
		rankingTable.getTableHeader().setFont(UIConstants.getPixelFont().deriveFont(18f));
		rankingTable.getTableHeader().setBackground(new Color(40, 40, 60));
		rankingTable.getTableHeader().setForeground(new Color(255, 255, 255));
		rankingTable.getTableHeader().setReorderingAllowed(false);
		rankingTable.getTableHeader().setResizingAllowed(false);
		
		// 헤더와 테이블 사이의 간격 제거
		rankingTable.setRowMargin(0);
		rankingTable.getTableHeader().setBackground(new Color(40, 40, 60));

		// 테이블 헤더 가운데 정렬을 위한 렌더러
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
		headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		headerRenderer.setBackground(new Color(40, 40, 60));
		headerRenderer.setForeground(new Color(255, 255, 255));
		headerRenderer.setFont(UIConstants.getPixelFont().deriveFont(18f));

		// 헤더에 렌더러 적용
		for (int i = 0; i < rankingTable.getColumnCount(); i++) {
			rankingTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
		}

		// 가운데 정렬 렌더러
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		centerRenderer.setBackground(new Color(30, 30, 40));
		centerRenderer.setForeground(new Color(220, 220, 220));

		// 랭킹 셀 렌더러 (색상 구분)
		DefaultTableCellRenderer rankRenderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);

				if (isShowingTopRankings) {
					// 상위 3위까지 다른 색상 지정
					if (row == 0) {
						c.setForeground(new Color(255, 215, 0)); // 금색
					} else if (row == 1) {
						c.setForeground(new Color(192, 192, 192)); // 은색
					} else if (row == 2) {
						c.setForeground(new Color(205, 127, 50)); // 동색
					} else {
						c.setForeground(new Color(220, 220, 220)); // 일반
					}
				} else {
					c.setForeground(new Color(220, 220, 220)); // 검색 결과에서는 모두 같은 색상
				}

				setHorizontalAlignment(SwingConstants.CENTER);
				return c;
			}
		};

		// 점수 셀 렌더러
		DefaultTableCellRenderer scoreRenderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);

				// 점수는 밝은 색상으로
				c.setForeground(new Color(100, 255, 100)); // 밝은 초록색
				setHorizontalAlignment(SwingConstants.CENTER);
				return c;
			}
		};

		// 날짜 셀 렌더러
		DefaultTableCellRenderer dateRenderer = new DefaultTableCellRenderer();
		dateRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		dateRenderer.setBackground(new Color(30, 30, 40));
		dateRenderer.setForeground(new Color(255, 180, 100)); // 오렌지색

		// 렌더러 적용
		rankingTable.getColumnModel().getColumn(0).setCellRenderer(rankRenderer);
		rankingTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		rankingTable.getColumnModel().getColumn(2).setCellRenderer(scoreRenderer);
		rankingTable.getColumnModel().getColumn(3).setCellRenderer(dateRenderer);

		// 컬럼 너비 조정 - 비율에 맞게 설정하고 전체 가로 길이 줄임
		rankingTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // RANK (15%)
		rankingTable.getColumnModel().getColumn(1).setPreferredWidth(200); // PLAYER (50%)
		rankingTable.getColumnModel().getColumn(2).setPreferredWidth(100); // SCORE (25%)
		rankingTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // DATE (보이지 않음)

		// 스크롤 패널에 테이블 추가 (가로 길이 줄임)
		JScrollPane scrollPane = new JScrollPane(rankingTable);
		// 너비를 450으로 줄이고 높이는 450 유지
		scrollPane.setPreferredSize(new Dimension(450, 450));
		scrollPane.setBorder(new LineBorder(new Color(60, 60, 100), 2, true));
		scrollPane.getViewport().setBackground(new Color(20, 20, 30));

		// 스크롤 패널 설정 - 헤더와 테이블 사이 간격 제거
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		// JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED로 설정하고 스크롤바 숨기기
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

		// 테이블이 변경되거나 모델이 업데이트될 때마다 행 높이 조정
		rankingTable.getModel().addTableModelListener(e -> adjustRowHeight());

		// 픽셀 스타일 테두리 추가
		JPanel borderPanel = new JPanel(new BorderLayout());
		borderPanel.setBackground(new Color(50, 50, 70));
		borderPanel.setBorder(new EmptyBorder(4, 4, 4, 4)); // 테두리 두께 증가
		borderPanel.add(scrollPane, BorderLayout.CENTER);

		panel.add(borderPanel, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * 테이블 행 높이를 동적으로 조정하는 메서드
	 */
	private void adjustRowHeight() {
		if (rankingTable == null)
			return;
		
		int totalRowCount = 10; // TOP 10 기준으로 설정
		
		// 테이블의 가용 영역 높이 계산 (테이블 영역의 실제 높이)
		JScrollPane scrollPane = (JScrollPane) rankingTable.getParent().getParent();
		int viewportHeight = scrollPane.getViewport().getHeight();
		
		// 테이블의 전체 높이를 계산 (헤더 포함)
		int tableHeight = viewportHeight;
		
		// 각 행의 높이를 테이블 높이의 10등분으로 설정 (헤더 포함)
		int rowHeight = Math.max(20, tableHeight / (totalRowCount + 1)); // +1은 헤더를 위한 공간
		
		// 헤더 높이도 설정
		rankingTable.getTableHeader().setPreferredSize(new Dimension(
				rankingTable.getTableHeader().getWidth(), rowHeight));
		
		// 모든 행의 높이를 균일하게 설정
		rankingTable.setRowHeight(rowHeight);
		
		// 스크롤 패널의 레이아웃을 갱신해서 변경사항 적용
		scrollPane.revalidate();
	}

	private JPanel createUserSearchPanel() {
		// 더 넓은 영역을 사용하도록 수정
		JPanel panel = new JPanel(new GridBagLayout()); // GridBagLayout 사용
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(10, 0, 20, 0)); // 여백 증가
		
		// GridBagConstraints 설정
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 5, 0, 10); // 컴포넌트 간 간격
		
		// 레이블 추가
		PixelLabel lblSearch = new PixelLabel("SEARCH PLAYER:", SwingConstants.RIGHT);
		lblSearch.setFont(UIConstants.getPixelFont().deriveFont(16f));
		lblSearch.setForeground(new Color(200, 200, 255));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0; // 고정 크기
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		panel.add(lblSearch, gbc);
		
		// 텍스트 필드 추가 - 더 넓게
		txtUserSearch = new PixelTextField(20, UIConstants.getPixelFont());
		txtUserSearch.setPreferredSize(new Dimension(400, 30)); // 크기 증가
		txtUserSearch.setMinimumSize(new Dimension(300, 30)); // 최소 크기 설정
		txtUserSearch.setBackground(new Color(30, 30, 40));
		txtUserSearch.setForeground(new Color(220, 220, 220));
		txtUserSearch.setBorder(new LineBorder(new Color(60, 60, 100), 2));
		txtUserSearch.setFont(UIConstants.getPixelFont().deriveFont(14f));
		
		gbc.gridx = 1;
		gbc.weightx = 1.0; // 가용 공간을 모두 사용
		gbc.fill = GridBagConstraints.HORIZONTAL; // 가로로 확장
		panel.add(txtUserSearch, gbc);
		
		// 검색 버튼 추가
		btnSearch = new PixelButton("SEARCH", UIConstants.getPixelFont());
		btnSearch.setPreferredSize(new Dimension(100, 30));
		btnSearch.setForeground(new Color(230, 230, 255));
		btnSearch.setBackground(new Color(40, 40, 100));
		btnSearch.setBorderColor(new Color(100, 100, 200));
		btnSearch.setPixelBorder(2);
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchUserRankings();
			}
		});
		
		gbc.gridx = 2;
		gbc.weightx = 0.0; // 고정 크기
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(btnSearch, gbc);
		
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

				// 타이틀 애니메이션 업데이트
				if (lblTitle instanceof PixelLabel) {
					try {
						// updateAnimation 메소드가 존재한다면 호출
						((PixelLabel) lblTitle).updateAnimation(titleBounce, colorPhase);
					} catch (Exception ex) {
						// updateAnimation 메소드가 없는 경우 대체 애니메이션 로직 사용
						float hue = colorPhase / 360.0f;
						Color color = Color.getHSBColor(0.14f, 0.9f - (0.2f * (float) Math.sin(hue * Math.PI)), 1.0f);

						lblTitle.setForeground(color);
						lblTitle.setBounds(lblTitle.getX(), lblTitle.getY() + titleBounce, lblTitle.getWidth(),
								lblTitle.getHeight());
						lblTitle.repaint();
					}
				}
			}
		});
		animationTimer.start();
	}

	/**
	 * 상위 10위 랭킹 데이터 로드
	 */
	private void loadTopRankings() {
		// 상위 랭킹 모드로 변경
		isShowingTopRankings = true;

		DefaultTableModel model = (DefaultTableModel) rankingTable.getModel();
		model.setRowCount(0); // 기존 데이터 초기화

		ArrayList<Ranking> rankings = rankingController.getTopRankings();

		for (int i = 0; i < rankings.size(); i++) {
			Ranking ranking = rankings.get(i);
			User user = userController.getUserById(ranking.getUserId());
			String nickname = user != null ? user.getNickname() : "UNKNOWN";

			// 날짜 형식을 yy-mm-dd로 변환
			String dateStr = "N/A";
			if (ranking.getRecordDate() != null) {
				dateStr = formatDate(ranking.getRecordDate());
			}

			model.addRow(new Object[] { i + 1, // 순위
					nickname, // 닉네임
					ranking.getScore(), // 점수
					dateStr // 날짜
			});
		}

		// 헤더 텍스트 변경 및 컬럼 조정
		rankingTable.getColumnModel().getColumn(0).setHeaderValue("RANK");
		rankingTable.getColumnModel().getColumn(1).setHeaderValue("PLAYER");
		rankingTable.getColumnModel().getColumn(2).setHeaderValue("SCORE");

		// TOP 10 모드에서는 날짜 컬럼 숨기기
		if (rankingTable.getColumnCount() >= 4) {
			rankingTable.getColumnModel().getColumn(3).setMinWidth(0);
			rankingTable.getColumnModel().getColumn(3).setMaxWidth(0);
			rankingTable.getColumnModel().getColumn(3).setWidth(0);
		}

		// 헤더 업데이트
		lblTableHeader.setText("TOP PLAYERS");
		lblTableHeader.setForeground(new Color(255, 255, 100));
		btnBackToTop.setVisible(false);

		// 테이블 헤더 갱신
		rankingTable.getTableHeader().repaint();
		
		// 행 높이 조정
		adjustRowHeight();
	}

	/**
	 * 사용자 랭킹 검색
	 */
	private void searchUserRankings() {
		String searchText = txtUserSearch.getText().trim();
		if (searchText.isEmpty()) {
			// 검색어가 없으면 상위 10위 랭킹 표시 (변경된 부분)
			loadTopRankings();
			return;
		}

		// 사용자 검색 (닉네임 기반으로 검색 가능한 로직 필요)
		// 여기서는 단순화를 위해 현재 사용자 데이터를 표시
		loadUserRankings(loggedInUser.getUserId(), searchText);
	}

	/**
	 * 특정 사용자의 랭킹 데이터 로드
	 */
	private void loadUserRankings(int userId, String searchName) {
		// 사용자 검색 모드로 변경
		isShowingTopRankings = false;

		DefaultTableModel model = (DefaultTableModel) rankingTable.getModel();
		model.setRowCount(0); // 기존 데이터 초기화

		ArrayList<Ranking> rankings = rankingController.getUserRankings(userId);
		User user = userController.getUserById(userId);
		String nickname = user != null ? user.getNickname() : searchName;

		for (int i = 0; i < rankings.size(); i++) {
			Ranking ranking = rankings.get(i);

			// 날짜 형식을 yy-mm-dd로 변환
			String dateStr = "N/A";
			if (ranking.getRecordDate() != null) {
				dateStr = formatDate(ranking.getRecordDate());
			}

			model.addRow(new Object[] { i + 1, // 순서
					nickname, // 닉네임
					ranking.getScore(), // 점수
					dateStr // 날짜
			});
		}

		// 헤더 텍스트 변경 및 컬럼 조정
		rankingTable.getColumnModel().getColumn(0).setHeaderValue("NO");
		rankingTable.getColumnModel().getColumn(1).setHeaderValue("PLAYER");
		rankingTable.getColumnModel().getColumn(2).setHeaderValue("SCORE");
		rankingTable.getColumnModel().getColumn(3).setHeaderValue("DATE");

		// 날짜 컬럼 표시 (검색 결과에서는 날짜 표시)
		if (rankingTable.getColumnCount() >= 4) {
			rankingTable.getColumnModel().getColumn(3).setMinWidth(120);
			rankingTable.getColumnModel().getColumn(3).setMaxWidth(120);
			rankingTable.getColumnModel().getColumn(3).setPreferredWidth(120);
		}

		// 헤더 업데이트
		lblTableHeader.setText("RECORDS: " + nickname);
		lblTableHeader.setForeground(new Color(100, 180, 255));
		btnBackToTop.setVisible(true);

		// 테이블 헤더 갱신
		rankingTable.getTableHeader().repaint();
		
		// 행 높이 조정
		adjustRowHeight();
	}

	/**
	 * 날짜 형식을 yy-mm-dd로 변환
	 */
	private String formatDate(java.util.Date date) {
		if (date == null)
			return "N/A";

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yy-MM-dd");
		return sdf.format(date);
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			// 화면이 표시될 때 상위 랭킹 로드
			loadTopRankings();
			
			// 약간의 딜레이 후 행 높이 조정 (컴포넌트가 완전히 배치된 후)
			Timer adjustTimer = new Timer(100, e -> adjustRowHeight());
			adjustTimer.setRepeats(false);
			adjustTimer.start();
		}
		super.setVisible(visible);
	}
}