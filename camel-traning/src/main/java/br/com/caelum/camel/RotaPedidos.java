package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class RotaPedidos {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
	    context.addRoutes(new RouteBuilder() {

	            @Override
	            public void configure() throws Exception {
	            	from("file:pedidos?delay=5s&noop=true"). //aqui tem um ponto para encadear a chamada do pr�ximo m�todo
	            	split().
	                	xpath("/pedido/itens/item").
	            	filter().
	            		xpath("/item/formato[text()='EBOOK']").
	            	marshal(). //queremos transformar a mensagem em outro formato
	                	xmljson(). //de xml para json
	            	log("${id} - ${body}"). //usando EL
	            	setHeader(Exchange.FILE_NAME, simple("${file:name.noext}-${header.CamelSplitIndex}.json")).
	                to("file:saida");
	            }
	    });

	    context.start(); //aqui camel realmente come�a a trabalhar
        Thread.sleep(2000); //esperando um pouco para dar um tempo para camel
        context.stop();
        
	}	
}
