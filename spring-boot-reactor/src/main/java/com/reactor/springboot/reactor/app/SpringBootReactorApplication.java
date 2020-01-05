package com.reactor.springboot.reactor.app;

import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.reactor.springboot.reactor.app.models.Comentarios;
import com.reactor.springboot.reactor.app.models.Usuario;
import com.reactor.springboot.reactor.app.models.UsuarioComentario;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner{ //CommandLineRunner : para que sea una interfaz de consola

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		//ejemploIterable();
		//ejemploFlatMap();
		//convertirAStrign();
		//convertirCollectList();
		//ejemploUsuarioComentariosFlatMap();
		//ejemploUsuarioComenrarioZipWith();	
		//ejemploUsuarioComenrarioZipWithForma2();
		//ejemploZipWithRangos();
		//ejemploInterval();
		//ejemploDelayElements();
		//ejemploIntervalInfinito();
		//ejemploIntervalInfinito();
		//ejemploIntervalDesdeCreate();
		ejemploContraPresion();
	}//.subscribe(i->log.info(i.toString()));
	
	public void ejemploContraPresion() {
		Flux.range(1, 10)
		.log() //nos permite ver la traza completa de nuestro flux -> ver como es suscrptio solicita la maxima cantida de elementos
		.limitRate(2)
		.subscribe(/*
				new Subscriber<Integer>() {
					
					private Subscription s;
					//limite que le queremos dar
					private Integer limite = 2;
					private Integer consumido = 0;
					
					//Manejamos lo que podemos pedir al productor o al flujo de arriba
					@Override
					public void onSubscribe(Subscription s) {
						this.s = s;
						//solictar el maximo elementos posibles
						//s.request(Long.MAX_VALUE);
						//limite
						s.request(limite);
						
					}

					//cada ves que recibimos un objeto
					//procesamos el elemento que se esta emitiendo cada vez
					@Override
					public void onNext(Integer t) {
						//mostrar en el log cada elemento
						log.info(t.toString());
						consumido++;
						if(consumido == limite) {
							consumido = 0;
							//volver a pedir dos mas
							s.request(limite);
						}
					}
					
					//para el error
					@Override
					public void onError(Throwable t) {
						// TODO Auto-generated method stub
						
					}
					
					//cuando terminamos
					@Override
					public void onComplete() {
						// TODO Auto-generated method stub
						
					}
					
				}*/
			); 
	}
	
	
	public void ejemploIntervalDesdeCreate() {
		Flux.create(emitter -> {
			Timer time = new Timer();
			time.schedule(new TimerTask() {	
				private Integer contador = 0;
				
				@Override
				public void run() {
					//almacenamos el dato que vamos a emitir despues en este
					//obserbable que estamos creando usaremos un contador que aumente cada vez
					//que se ejecute en metodo run										
					emitter.next(++contador);
					if(contador == 10) {
						time.cancel();
						emitter.complete();					
					}			
					
					if(contador == 5) {
						time.cancel();
						emitter.error(new InterruptedException("Error, se ha detenido el flux en 5 !"));
					}					
				}
			}, 1000, 1000);
		})		
		.subscribe(next -> log.info(next.toString())
				,error -> log.error(error.getMessage())
				,() -> log.info("Hermos terminado")
				);
		
//		.doOnNext(next -> log.info(next.toString()))
//		.doOnComplete(() -> log.info("Hemos terminado"))
//		.subscribe();
		
	}
	
	public void ejemploIntervalInfinito() throws InterruptedException {
		
		//para realizar el retrazo del hilo
		//cuando decrementa y llega a 0 y libera el hilo
		CountDownLatch latch = new CountDownLatch(1);
		
		//Modifcar el tiempo del interval con map
		Flux.interval(Duration.ofSeconds(1))
		.doOnTerminate(() -> latch.countDown()) //el obserbable y libera el hilo .doOnTerminate(latch::countDown)
		.flatMap(i->{
			if(i>=5) {
				//retornamos otro obsebale
				return Flux.error(new InterruptedException("Solo hasta 5!"));
			}
			return Flux.just(i);
		})
		.map(i->"Hola " + i)
		.retry(2)
		//.doOnNext(s->log.info(s))
		.subscribe(s->log.info(s), e -> log.error(e.getMessage()));
		
		//Espera que el hilo llegue a cero
		latch.await();
		
	}
	
	public void ejemploDelayElements() throws InterruptedException {
		Flux<Integer> rango = Flux.range(1, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> log.info(i.toString()));
				rango.subscribe();
				//rango.blockLast();
				
				Thread.sleep(13000);
	}
	
	public void ejemploInterval() {
		Flux<Integer> rango = Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));
		
		rango.zipWith(retraso,(ra,re) -> ra)
		.doOnNext(i -> log.info(i.toString()))
		.blockLast();
		///.subscribe();
	}	
	
	public void ejemploZipWithRangos() {
		
		Flux<Integer> rangos = Flux.range(0, 4);
		
		Flux.just(1,2,3,4)
		.map(i->(i*2)) //cambiando el flujo multiplicado por 2. Flux.range(0, 4) : flujo 2, flujo 1 : Flux.just(1,2,3,4)
		.zipWith(rangos,(uno,dos) -> String.format("Primer Flux ; %d, Segundo Flux: %d",uno,dos)) //combinado los rangos de flujos
		.subscribe(texto -> log.info(texto));
	}
	
	
	public void ejemploUsuarioComenrarioZipWithForma2() {
		//Funcion de los dos flujos
		//crear un observable de cada uno : usuario y comentario
		Mono<Usuario> usuarioMono = Mono.fromCallable(()->{
			return new Usuario("John", "Doe");
		});
		
		//ahora para comentarios
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()->{
			//creamos los comentarios
			Comentarios comentarios = new Comentarios();						
			comentarios.addComentario("Hola Pepe que tal...");
			comentarios.addComentario("Mañana me voy a la playa");
			comentarios.addComentario("Estoy en el curso de spring con reactor");
			return comentarios;
		});
		
		//con un solo argumento		
		Mono<UsuarioComentario> usuarioConComentarios = usuarioMono
			.zipWith(comentariosUsuarioMono)
			.map(tuple -> {
				Usuario u = tuple.getT1(); //flujo1
				Comentarios c = tuple.getT2(); //flujo2
				return new UsuarioComentario(u, c);
			});
		
		usuarioConComentarios.subscribe(uc->log.info(uc.toString()));
		
	}
	
	public void ejemploUsuarioComenrarioZipWith() {
		//Funcion de los dos flujos
		//crear un observable de cada uno : usuario y comentario
		Mono<Usuario> usuarioMono = Mono.fromCallable(()->{
			return new Usuario("John", "Doe");
		});
		
		//ahora para comentarios
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()->{
			//creamos los comentarios
			Comentarios comentarios = new Comentarios();						
			comentarios.addComentario("Hola Pepe que tal...");
			comentarios.addComentario("Mañana me voy a la playa");
			comentarios.addComentario("Estoy en el curso de spring con reactor");
			return comentarios;
		});
		
		//colocamos un mono, y luego la combinacion del flujo
		//tranformando el flujo principal en usuarioMono y los combinamos con otro flujo
		//comentariosUsuarioMono que tiene los dos elementos (usuario,comentariosUsuario) y se mescla a UsuarioComentario
		Mono<UsuarioComentario> usuarioConComentarios = usuarioMono
			.zipWith(comentariosUsuarioMono,(usuario,comentariosUsuario) -> new UsuarioComentario(usuario, comentariosUsuario));
		
		usuarioConComentarios.subscribe(uc->log.info(uc.toString()));
		
	}
	
	//creando un metodo para crear el objeto usuario
//	public Usuario crearUsuario() {
//		return new Usuario("John", "Doe");
//	}
	
	//creando un metodo
	public void ejemploUsuarioComentariosFlatMap() {
		//Funcion de los dos flujos
		//crear un observable de cada uno : usuario y comentario
		Mono<Usuario> usuarioMono = Mono.fromCallable(()->{
			return new Usuario("John", "Doe");
		});
		
		//ahora para comentarios
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()->{
			//creamos los comentarios
			Comentarios comentarios = new Comentarios();						
			comentarios.addComentario("Hola Pepe que tal...");
			comentarios.addComentario("Mañana me voy a la playa");
			comentarios.addComentario("Estoy en el curso de spring con reactor");
			return comentarios;
		});
		
		//crear apartir de este stream crear un flujo de tream de tipo usuarioComentario
		//un flujo que contenga al obj usuario comentarios convinados
		//lo que se ha hecho es convetir el obsevable comentariosUsuarioMono en el tipo
		//UsuarioComentario mediante el map ahora es un mono de usuarioComentarios y no de comentarios
		usuarioMono.flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioComentario(u, c)))
			.subscribe(uc->log.info(uc.toString()));
		
	}
	
	public void convertirCollectList(String... args) throws Exception {
		
		//creando un flujo apartir de una lista		
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Jean"," Hurtado"));
		usuariosList.add(new Usuario("Juna" , "Galo"));
		usuariosList.add(new Usuario("Pedro" , " Malo"));
		usuariosList.add(new Usuario("Carlos" , " Alngelo"));
		usuariosList.add(new Usuario("Bruce" ," lee"));
		usuariosList.add(new Usuario("Bruce" , " willis"));
		
		Flux.fromIterable(usuariosList)
			.collectList()
			.subscribe(lista -> {
				lista.forEach(item -> log.info(item.toString()));				
			});		
	}
	
	public void convertirAStrign(String... args) throws Exception {
		
		//creando un flujo apartir de una lista		
		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Jean"," Hurtado"));
		usuariosList.add(new Usuario("Juna" , "Galo"));
		usuariosList.add(new Usuario("Pedro" , " Malo"));
		usuariosList.add(new Usuario("Carlos" , " Alngelo"));
		usuariosList.add(new Usuario("Bruce" ," lee"));
		usuariosList.add(new Usuario("Bruce" , " willis"));
		
		Flux.fromIterable(usuariosList).
			map(usuario -> usuario.getNombre().toUpperCase().concat(usuario.getApellido().toUpperCase())) //transformando a usuario en todos
				.flatMap(nombre -> { //convierte de obj a usuario a string
					if(nombre.contains("bruce".toUpperCase())) { //solo filtra los que coincidan
						return Mono.just(nombre); //tiene que se de tipo observable - mono
					}else {
						//no emite ningun elemento usuario
						return Mono.empty();
					}					
					
				}).map(nombre -> {
						 return nombre.toLowerCase();
					}).subscribe(u -> log.info(u.toString()));		
	}
	
	public void ejemploFlatMap(String... args) throws Exception {
		
		//creando un flujo apartir de una lista		
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Jean Hurtado");
		usuariosList.add("Juna Galo");
		usuariosList.add("Pedro Malo");
		usuariosList.add("Carlos Alngelo");
		usuariosList.add("Bruce lee");
		usuariosList.add("Bruce willis");
		
		Flux.fromIterable(usuariosList).
			map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase())) //transformando a usuario en todos
				.flatMap(usuario -> { //convierte de string a obj a usuario
					if(usuario.getNombre().equalsIgnoreCase("bruce")) { //solo filtra los que coincidan
						return Mono.just(usuario); //tiene que se de tipo observable - mono
					}else {
						//no emite ningun elemento usuario
						return Mono.empty();
					}					
					
				}).map(usuario -> {  //convetir de un flujo de string a obj persona
						 String nombre = usuario.getNombre().toLowerCase();
						 usuario.setNombre(nombre);
						 return usuario;
					}).subscribe(u -> log.info(u.toString()));		
	}
	
	
	public void ejemploIterable(String... args) throws Exception {
		//observable/observador flux - flujo reactivo
		//Flux<String> nombres = Flux.just("Jean Hurtado","Juna Galo","Pedro Malo","Carlos Alngel","Bruce lee","Bruce willis"); //implementado un tipo de tares: cada vez que recivimos algun elemento
		
		//creando un flujo apartir de una lista		
		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Jean Hurtado");
		usuariosList.add("Juna Galo");
		usuariosList.add("Pedro Malo");
		usuariosList.add("Carlos Alngelo");
		usuariosList.add("Bruce lee");
		usuariosList.add("Bruce willis");
		
		Flux<String> nombres = Flux.fromIterable(usuariosList);
		
		Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase())) //transformando a usuario en todos
				.filter(usuario -> {
					return usuario.getNombre().toLowerCase().equals("bruce");
				})
				.doOnNext(usuario -> {   
						if(usuario == null) {
							throw new RuntimeException("Nombres no pueden ser vacios");
						}
						System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
					}).map(usuario -> {  //convetir
						 String nombre = usuario.getNombre().toLowerCase();
						 usuario.setNombre(nombre);
						 return usuario;
					});  
		//.doOnNext(System.out::println); //mas corto
		
		//subcribiendo al flujo
		//manejar eventos - tambien en los operadores
		nombres.subscribe(e -> log.info(e.toString()),error -> log.error(error.getMessage()),
				new Runnable() { //clase anonima - cuando termina la ejecutacion del flux					
					@Override
					public void run() {
						log.info("Ha finalizado la ejecucion del observable con exito");						
					}
				});
		
//		Usuario u1=new Usuario("Carlos", "Peña");
//		Flux<Usuario> usuario = Flux.just(u1).doOnNext(usuario1 -> System.out.println(usuario1.getNombre()));
//		usuario.subscribe();
		
	}

}
