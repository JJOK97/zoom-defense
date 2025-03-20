package contorller;

import dao.TowerPlacementDAO;
import service.TowerPlacementServicelmpl;

public class TowerPlacementController {
	private TowerPlacementDAO towerplacementDAO;
	
	public TowerPlacementController() {
		this.towerplacementDAO = new TowerPlacementDAO();
	}
}
