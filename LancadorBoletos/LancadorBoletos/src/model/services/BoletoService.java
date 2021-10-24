package model.services;

import java.util.List;

import model.dao.BoletoDao;
import model.dao.DaoFactory;
import model.entities.Boleto;

public class BoletoService {
	
	private BoletoDao dao = DaoFactory.createBoletoDao();
	
	public List<Boleto> findAll() {
		
		return dao.findAll();
	}
	
	public void saveOrUpdate(Boleto obj) {
		if (obj.getId()== null) {
			dao.insert(obj);
		}
		else
			dao.update(obj);
	}
	
	public void remove(Boleto obj) {
		dao.deleteById(obj.getId());
	}
}
