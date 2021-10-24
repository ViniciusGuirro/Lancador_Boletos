package model.dao;

import java.util.List;

import model.entities.Departamento;
import model.entities.Boleto;

public interface BoletoDao {

	void insert(Boleto obj);
	void update(Boleto obj);
	void deleteById(Integer id);
	Boleto findById(Integer id);
	List<Boleto> findAll();
	List<Boleto> findByDepartamento(Departamento departamento);
}
