package com.mitocode;

import com.mitocode.observer.PesoARGObservador;
import com.mitocode.observer.PesoMXObservador;
import com.mitocode.observer.SolObservador;
import com.mitocode.observer.Subject;

public class App {

	public static void main(String[] args) {
		//creando una instancia del sujeto
		Subject subject = new Subject();

		//creando isntancia de los observadores pasandole el sujeto
		//ejecuta el metodo agregrar para agregar a la lista de observadores
		new SolObservador(subject);
		new PesoARGObservador(subject);
		new PesoMXObservador(subject);
		
		System.out.println("Si desea cambiar 10 dólares obtendrá : ");
		subject.setEstado(10);
		System.out.println("-----------------");
		System.out.println("Si desea cambiar 100 dólares obtendrá : ");
		subject.setEstado(100);
	}
}
