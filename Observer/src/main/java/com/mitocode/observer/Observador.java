package com.mitocode.observer;

//clase observador
public abstract class Observador {
	
	protected Subject sujeto;//atributo al sujeto
	//funcion actualizar para que cada vez que se ejecute este cambie su estado
	public abstract void actualizar(); 

}
