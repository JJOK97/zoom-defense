package model;

public class TowerPlacement {
	private int placementId;
	private int sessionId;
	private int towerId;
	private int positionX;
	private int positionY;

	// 타워 업그레이드
	public TowerPlacement(int sessionId, int positionX, int positionY) {
		super();
		this.sessionId = sessionId;
		this.positionX = positionX;
		this.positionY = positionY;
	}

	// 타워 설치
	public TowerPlacement(int towerId, int sessionId, int positionX, int positionY) {
		super();
		this.towerId = towerId;
		this.sessionId = sessionId;
		this.positionX = positionX;
		this.positionY = positionY;
	}

	// 모든 필드를 포함하는 생성자 추가
	public TowerPlacement(int placementId, int sessionId, int towerId, int positionX, int positionY) {
		super();
		this.placementId = placementId;
		this.sessionId = sessionId;
		this.towerId = towerId;
		this.positionX = positionX;
		this.positionY = positionY;
	}

	// 기본 생성자 추가
	public TowerPlacement() {
		super();
	}

	// getter setter 메소드
	public int getPlacementId() {
		return placementId;
	}

	public void setPlacementId(int placementId) {
		this.placementId = placementId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public int getTowerId() {
		return towerId;
	}

	public void setTowerId(int towerId) {
		this.towerId = towerId;
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

}
