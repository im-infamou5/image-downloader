package test.interactivestandard.app.config

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient


@Configuration
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAsync
class Config {
    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        val size = 16 * 1024 * 1024 // shared buffer for reading images into memory
        val strategies = ExchangeStrategies.builder()
            .codecs { codecs: ClientCodecConfigurer ->
                codecs.defaultCodecs().maxInMemorySize(size)
            }
            .build()
        return WebClient.builder().exchangeStrategies(strategies)
            .clientConnector(ReactorClientHttpConnector(httpClient())).build()
    }

    @Bean
    fun httpClient(): HttpClient = HttpClient.create()
}