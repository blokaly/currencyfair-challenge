package github.blokaly.cfcs.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class GlobalCorsConfig {
    @Bean
    fun  corsFilter(): CorsFilter {
        val config = CorsConfiguration()
        config.addAllowedOrigin("*")
        config.allowCredentials = true
        config.addAllowedMethod("*")
        config.addAllowedHeader("*")
        config.maxAge = 3600
        val corsConfigurationSource = UrlBasedCorsConfigurationSource()
        corsConfigurationSource.registerCorsConfiguration("/**",config)
        return CorsFilter(corsConfigurationSource);
    }
}
