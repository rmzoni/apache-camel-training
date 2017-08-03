package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;

import com.thoughtworks.xstream.XStream;


public class RotaTimer {

	
	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		final XStream xstream = new XStream();
    	xstream.alias("negociacao", Negociacao.class);
	    context.addRoutes(new RouteBuilder() {
	    	
	            @Override
	            public void configure() throws Exception {
	            	from("timer://negociacoes?fixedRate=true&delay=3s&period=360s")
	                	.to("http4://argentumws.caelum.com.br/negociacoes")
	                	.convertBodyTo(String.class)
	                	.unmarshal(new XStreamDataFormat(xstream))
	                	.split(body())
	                	.log("${body}")
	                .end(); //só deixa explícito que é o fim da rota
	            }
	    });

	    context.start(); //aqui camel realmente começa a trabalhar
        Thread.sleep(10000); //esperando um pouco para dar um tempo para camel
        context.stop();
        
	}	
	
}
