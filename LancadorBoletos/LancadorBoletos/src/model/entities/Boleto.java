package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Boleto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private String numero;
	private Date vencimento;
	private Double valor;
	private Departamento departamento;
	
	public Boleto() {
		
	}
	
	public Boleto(Integer id, String name, String numero, Date vencimento, Double valor, Departamento departamento) {
		this.id = id;
		this.name = name;
		this.numero = numero;
		this.vencimento = vencimento;
		this.valor = valor;
		this.departamento = departamento;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getVencimento() {
		return vencimento;
	}

	public void setVencimento(Date vencimento) {
		this.vencimento = vencimento;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Departamento getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Departamento dep) {
		this.departamento = dep;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Boleto other = (Boleto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Boleto [id=" + id + ", name=" + name + ", numero=" + numero + ", vencimento=" + vencimento + ", valor="
				+ valor + "]";
	}
	
	
}
