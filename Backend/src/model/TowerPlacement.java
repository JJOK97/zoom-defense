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
	public TowerPlacement(int placementid, int sessionid, int positionx, int positiony) {
		super();
		this.placementId = placementid;
		this.sessionId = sessionid;
		this.positionX = positionx;
		this.positionY = positiony;
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
