package org.example;


import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        final Flux<String> typings = Flux.create(emitter -> {
            try {
                emitter.next("j");
                Thread.sleep(100);
                emitter.next("ja");
                Thread.sleep(100);
                emitter.next("jav");
                Thread.sleep(100);
                emitter.next("java");
                Thread.sleep(100);
                emitter.next("javas");
                Thread.sleep(100);
                emitter.next("javasc");
                Thread.sleep(100);
                emitter.next("javascr");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        final WebClient webClient = WebClient.create("http://localhost:8080/api/search");
        // TODO: "j", "ja", "jav", "java"

        typings
                .sample(Duration.ofMillis(500))
                // нужен аналог [debounceTime](https://rxmarbles.com/#debounceTime)
                .concatMap(
                        o -> {
                            System.out.printf("\nSent data: %s\n\n", o);
                            return webClient
                                    .get()
                                    .uri(builder -> builder.queryParam("text", o).build())
                                    .retrieve()
                                    .bodyToMono(String[].class);
                        }
                )
                .subscribe(
                        data -> System.out.printf("\nReceived data: %s\n\n", Arrays.asList(data)),
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("complete")
                );

        typings.blockLast();
    }
}
