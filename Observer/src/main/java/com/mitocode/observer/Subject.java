package com.mitocode.observer;

import java.util.ArrayList;
import java.util.List;


public class Subject { //sujeto en observacion

	//lista de observadores
	private List<Observador> observadores = new ArrayList<Observador>();
	//estado el cual vamos a cambiar
	private int estado; 

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
		//apenas el estado cambien va a lanzar una notificaion
		//a todos los observadores que esta suscritos a este sujeto
		notificarTodosObservadores();
	}

	//para agregar suscriptores a este sujeto
	public void agregar(Observador observador) {		
		//añade a la lista de observadores
		observadores.add(observador);
	}

	//recorrer todas la lista
	public void notificarTodosObservadores() {
		//java 8
		observadores.forEach(x -> x.actualizar());
	}

}
