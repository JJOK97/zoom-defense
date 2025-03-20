package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import ui.common.UIConstants;
import ui.components.PixelBackgroundPanel;
import ui.components.PixelButton;
import ui.components.PixelLabel;
import model.User;

/**
 * 게임 선택 화면을 구현한 UI 클래스 (새 게임, 이어하기, 랭킹)
 */
public class GameSelectionFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	// 컴포넌트
	private PixelButton btnNewGame;
	private PixelButton btnContinue;
	private PixelButton btnRanking;
	private PixelButton btnLogout;
	private PixelLabel lblTitle;
	private PixelLabel lblWelcome;

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

	/**
	 * 사용자 정보로 생성하는 생성자
	 */
	public GameSelectionFrame(User user) {
		this.loggedInUser = user;

		setTitle("ZOOM Defense - 게임 선택 - " + user.getNickname() + "님");
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
	}

	/**
	 * 창 상태를 유지하는 생성자
	 * 
	 * @param nickname  로그인한 사용자 닉네임
	 * @param bounds    이전 창의 경계값
	 * @param maximized 이전 창의 최대화 상태
	 */
	public GameSelectionFrame(String nickname, Rectangle bounds, boolean maximized) {

		setTitle("ZOOM Defense - 게임 선택 - " + nickname + "님");
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

		if (lblWelcome != null) {
			lblWelcome.setFont(UIConstants.getScaledPixelFont(width));
		}

		// 버튼 크기 조정
		if (btnNewGame != null && btnContinue != null && btnRanking != null && btnLogout != null) {
			int buttonWidth = Math.max(220, width / 5);
			int buttonHeight = Math.max(65, height / 12);
			btnNewGame.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
			btnContinue.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
			btnRanking.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
			btnLogout.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
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
		int topMargin = (int) (getHeight() * 0.15);
		titlePanel.setBorder(new EmptyBorder(topMargin, 0, 0, 0));

		lblTitle = new PixelLabel("ZOOM DEFENSE", SwingConstants.CENTER);
		lblTitle.setFont(UIConstants.getTitleFont());
		lblTitle.setForeground(UIConstants.GOLD_COLOR);

		lblWelcome = new PixelLabel(loggedInUser.getNickname() + "님 환영합니다", SwingConstants.CENTER);
		lblWelcome.setFont(UIConstants.getPixelFont());
		lblWelcome.setForeground(UIConstants.WHITE_COLOR);
		lblWelcome.setBorder(new EmptyBorder(25, 0, 25, 0));

		titlePanel.add(lblTitle, BorderLayout.CENTER);
		titlePanel.add(lblWelcome, BorderLayout.SOUTH);

		// 중앙 패널 - 전체 컨텐츠를 담는 메인 컨테이너
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		centerPanel.setOpaque(false);

		// 게임 선택 버튼 패널
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(6, 0, 6, 0);

		// 버튼 크기 설정
		int buttonWidth = Math.max(220, getWidth() / 5);
		int buttonHeight = Math.max(65, getHeight() / 12);
		Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);

		// 새 게임 버튼 - 검 아이콘 느낌
		btnNewGame = new PixelButton(" 새 게임", UIConstants.getPixelFont());
		btnNewGame.setPreferredSize(buttonSize);
		btnNewGame.setForeground(new Color(255, 255, 100));
		btnNewGame.setBackground(new Color(40, 80, 40));
		btnNewGame.setBorderColor(new Color(200, 255, 80));
		btnNewGame.setPixelBorder(3);
		btnNewGame.setIconName("sword");
		btnNewGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(GameSelectionFrame.this, "새 게임 기능은 아직 구현되지 않았습니다.", "개발 중",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		buttonPanel.add(btnNewGame, gbc);

		// 이어하기 버튼 - 디스켓 아이콘 느낌
		btnContinue = new PixelButton(" 이어하기", UIConstants.getPixelFont());
		btnContinue.setPreferredSize(buttonSize);
		btnContinue.setForeground(new Color(80, 220, 255));
		btnContinue.setBackground(new Color(30, 40, 100));
		btnContinue.setBorderColor(new Color(100, 180, 255));
		btnContinue.setPixelBorder(3);
		btnContinue.setIconName("save");
		btnContinue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(GameSelectionFrame.this, "이어하기 기능은 아직 구현되지 않았습니다.", "개발 중",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		buttonPanel.add(btnContinue, gbc);

		// 랭킹 버튼 - 트로피 아이콘 느낌
		btnRanking = new PixelButton(" 랭킹", UIConstants.getPixelFont());
		btnRanking.setPreferredSize(buttonSize);
		btnRanking.setForeground(new Color(255, 220, 100));
		btnRanking.setBackground(new Color(100, 40, 40));
		btnRanking.setBorderColor(new Color(255, 200, 80));
		btnRanking.setPixelBorder(3);
		btnRanking.setIconName("trophy");
		btnRanking.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(GameSelectionFrame.this, "랭킹 기능은 아직 구현되지 않았습니다.", "개발 중",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		buttonPanel.add(btnRanking, gbc);

		// 로그아웃 버튼 - 문 아이콘 느낌
		btnLogout = new PixelButton(" 로그아웃", UIConstants.getPixelFont());
		btnLogout.setPreferredSize(buttonSize);
		btnLogout.setForeground(new Color(220, 220, 220));
		btnLogout.setBackground(new Color(60, 60, 60));
		btnLogout.setBorderColor(new Color(160, 160, 160));
		btnLogout.setPixelBorder(3);
		btnLogout.setIconName("exit");
		btnLogout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 현재 창의 상태 저장
				boolean currentMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
				Rectangle currentBounds = currentMaximized ? frameBounds : getBounds();

				// 로그아웃 후 메인 화면으로 이동
				dispose();
				MainGameFrame mainFrame = new MainGameFrame(currentBounds, currentMaximized);
				mainFrame.setVisible(true);
			}
		});
		buttonPanel.add(btnLogout, gbc);

		// 중앙 패널에 버튼 패널 추가 (가운데 정렬)
		GridBagConstraints centerGbc = new GridBagConstraints();
		centerGbc.gridx = 0;
		centerGbc.gridy = 0;
		centerGbc.weightx = 1.0;
		centerGbc.weighty = 1.0;
		centerGbc.fill = GridBagConstraints.NONE;
		centerGbc.anchor = GridBagConstraints.CENTER;
		centerPanel.add(buttonPanel, centerGbc);

		// 메인 패널에 배치
		panel.add(titlePanel, BorderLayout.NORTH);
		panel.add(centerPanel, BorderLayout.CENTER);

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
}