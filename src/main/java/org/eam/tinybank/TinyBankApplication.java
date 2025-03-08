package org.eam.tinybank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// TODO move annotations to config, when created
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class TinyBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinyBankApplication.class, args);
    }

}