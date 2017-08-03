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
		            	setProperty("pedidoId", xpath("/pedido/id/text()")).
		                setProperty("clienteId", xpath("/pedido/pagamento/email-titular/text()")).
		                split().
		                    xpath("/pedido/itens/item").
		                filter().
		                    xpath("/item/formato[text()='EBOOK']").
		                setProperty("ebookId", xpath("/item/livro/codigo/text()")).
		                log("${id} \n ${body}").
		                marshal().
		                    xmljson().
		                setHeader(Exchange.HTTP_QUERY, 
		                        simple("clienteId=${property.clienteId}&pedidoId=${property.pedidoId}&ebookId=${property.ebookId}")).
		            to("http4://localhost:8080/webservices/ebook/item");
	            }
	    });

	    context.start(); //aqui camel realmente come�a a trabalhar
        Thread.sleep(2000); //esperando um pouco para dar um tempo para camel
        context.stop();
        
	}	
}
