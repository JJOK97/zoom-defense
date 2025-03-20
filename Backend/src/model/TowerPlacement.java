package model;

public class TowerPlacement {
	private int placementId;
	private int sessionId;
	private int positionX;
	private int positionY;
	
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
