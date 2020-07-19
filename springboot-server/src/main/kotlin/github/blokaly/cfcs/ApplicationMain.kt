package github.blokaly.cfcs

import github.blokaly.cfcs.common.MainLogging
import github.blokaly.cfcs.common.logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import javax.annotation.PreDestroy

fun main(args: Array<String>) {
    runApplication<ApplicationMain>(*args)
}

@SpringBootApplication
@EnableScheduling
class ApplicationMain {
    @PreDestroy
    @Throws(Exception::class)
    fun onDestroy() {
        MainLogging.logger().info("Application terminated")
    }
}