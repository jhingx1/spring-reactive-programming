package com.reactor.springboot.reactor.app.models;

public class UsuarioComentario {
	
	private Usuario usuario;	
	private Comentarios comentarios;
	
	public UsuarioComentario(Usuario usuario, Comentarios comentarios) {
		this.usuario = usuario;
		this.comentarios = comentarios;
	}

	@Override
	public String toString() {
		return "UsuarioComentario [usuario=" + usuario + ", comentarios=" + comentarios + "]";
	}
	
}
