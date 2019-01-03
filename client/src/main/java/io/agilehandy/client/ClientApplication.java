package io.agilehandy.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientApplication {

	Logger log = LoggerFactory.getLogger(ClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(@Value("${rsocket.host}") String host, @Value("${rsocket.port}") int port) {
		RSocket client = RSocketFactory.connect()
				.transport(TcpClientTransport.create(host, port))
				.start()
				.block();

		return args -> {
			client.requestStream(DefaultPayload.create("RSocket Demo"))
					.map(Payload::getDataUtf8)
					.doOnNext(log::info)
					.take(10)
					.then()
					.doFinally(signalType -> client.dispose())
					.then()
					.block();
		};
	}
}

