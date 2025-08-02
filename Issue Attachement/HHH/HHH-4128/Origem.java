package br.gov.mpf.prce.inquel.embedded;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Embeddable;

@Embeddable
public class Origem 
extends Constante 
implements Serializable {

	public static final Origem FEDERAL = new Origem("F", "Federal");

	static {
		CONSTANTES = Arrays.asList(FEDERAL);
	}
	
	public Origem() {
		super();
	}
	
	private Origem(String valor, String descricao) {
		super(valor, descricao);
	}
	
	public static Collection<Origem> getConstantes() {
		return getConstantes(Origem.class);
	}
}