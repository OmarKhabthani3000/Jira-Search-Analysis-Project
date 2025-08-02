package br.gov.mpf.prce.inquel.embedded;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.persistence.Transient;

/**
 * Classe 'incorporável' abstrata. 
 * 
 * @author fernando
 * Revisado em 05-Ago-2009
 */

public class Constante implements Serializable {
 
	protected static Collection<? extends Constante> CONSTANTES;
	
	protected String valor;

	@Transient
	protected String descricao;
	
	public Constante() {}
	
	protected Constante(String valor, String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		Constante constante = get(getValor());
		return (constante != null ? constante.descricao : "");
	}
	
	public String getValor() {
		return this.valor;
	}

	public static Constante get(String valor) {
		for (Constante constante : CONSTANTES) {
			if (constante.getValor().equalsIgnoreCase(valor)) {
				return constante;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends Constante> Collection<T> getConstantes(Class<T> clazz) {
		Collection<T> items = new ArrayList<T>();
		for (Constante item : CONSTANTES) {
			items.add((T)item);
		}
		return Collections.unmodifiableCollection(items);
	}
	
	public String toString() {
		return "["+getValor()+":"+getDescricao()+"]";
	}
	
}
 
