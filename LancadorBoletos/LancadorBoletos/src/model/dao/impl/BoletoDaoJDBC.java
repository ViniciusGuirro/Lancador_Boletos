package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Statement;

import db.DB;
import db.DbException;
import model.dao.BoletoDao;
import model.entities.Boleto;
import model.entities.Departamento;

public class BoletoDaoJDBC implements BoletoDao {

	private Connection conn;
	
	public BoletoDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Boleto obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO boleto "
					+ "(Name, Numero, Vencimento, Valor, DepartamentoId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getNumero());
			st.setDate(3, new java.sql.Date(obj.getVencimento().getTime()));
			st.setDouble(4, obj.getValor());
			st.setInt(5, obj.getDepartamento().getId()); 
			
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Boleto obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE Boleto "
					+ "SET Name = ?, Numero = ?, Vencimento = ?, Valor = ?, DepartamentoId = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getNumero());
			st.setDate(3, new java.sql.Date(obj.getVencimento().getTime()));
			st.setDouble(4, obj.getValor());
			st.setInt(5, obj.getDepartamento().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM Boleto WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Boleto findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT Boleto.*,departamento.Name as DepName "
					+ "FROM Boleto INNER JOIN departamento "
					+ "ON Boleto.DepartamentoId = departamento.Id "
					+ "WHERE Boleto.Id = ?");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Departamento dep = instantiateDepartamento(rs);
				Boleto obj = instantiateBoleto(rs, dep);
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Boleto instantiateBoleto(ResultSet rs, Departamento dep) throws SQLException {
		Boleto obj = new Boleto();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setNumero(rs.getString("Numero"));
		obj.setValor(rs.getDouble("Valor"));
		obj.setVencimento(new java.util.Date(rs.getTimestamp("Vencimento").getTime()));
		obj.setDepartamento(dep);
		return obj;
	}

	private Departamento instantiateDepartamento(ResultSet rs) throws SQLException {
		Departamento dep = new Departamento();
		dep.setId(rs.getInt("DepartamentoId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Boleto> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT Boleto.*,departamento.Name as DepName "
					+ "FROM Boleto INNER JOIN departamento "
					+ "ON Boleto.DepartamentoId = departamento.Id "
					+ "ORDER BY Name");
			
			rs = st.executeQuery();
			
			List<Boleto> list = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();
			
			while (rs.next()) {
				
				Departamento dep = map.get(rs.getInt("DepartamentoId"));
				
				if (dep == null) {
					dep = instantiateDepartamento(rs);
					map.put(rs.getInt("DepartamentoId"), dep);
				}
				
				Boleto obj = instantiateBoleto(rs, dep);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Boleto> findByDepartamento(Departamento departamento) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT Boleto.*,departamento.Name as DepName "
					+ "FROM Boleto INNER JOIN departamento "
					+ "ON Boleto.DepartamentoId = departamento.Id "
					+ "WHERE DepartamentoId = ? "
					+ "ORDER BY Name");
			
			st.setInt(1, departamento.getId());
			
			rs = st.executeQuery();
			
			List<Boleto> list = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();
			
			while (rs.next()) {
				
				Departamento dep = map.get(rs.getInt("DepartamentoId"));
				
				if (dep == null) {
					dep = instantiateDepartamento(rs);
					map.put(rs.getInt("DepartamentoId"), dep);
				}
				
				Boleto obj = instantiateBoleto(rs, dep);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}
