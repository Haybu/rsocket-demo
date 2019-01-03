/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.agilehandy.service;

import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Haytham Mohamed
 **/
@Component
public class MyListener implements ApplicationListener<ApplicationReadyEvent> {

	@Value("${rsocket.host}")
	String host;
	@Value("${rsocket.port}")
	int port;

	final private MyHandler handler;

	public MyListener(MyHandler handler) {
		this.handler = handler;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		RSocketFactory.receive()
				.acceptor((setupPayload, sender) -> Mono.just(handler))
				.transport(TcpServerTransport.create(host, port))
				.start()
				.subscribe();
	}
}
