package com.mitocode.observer;

//SolObservador : va a ser cliente del cliente-subject
public class SolObservador extends Observador{

	private double valorCambio = 3.25;
	
	public SolObservador(Subject sujeto) {
		this.sujeto = sujeto;//revibe el sujeto
		//para agregar a la lista de observadores
		this.sujeto.agregar(this);
	}
	
	@Override
	public void actualizar() {		
		System.out.println("PEN: " + (sujeto.getEstado() * valorCambio));
	}

}
