package co.nilin.opex.chainscan.gateway.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
@EnableR2dbcRepositories(basePackages = ["co.nilin.opex"])
class PostgresConfig(db: DatabaseClient) {

    init {
        db.sql {
            """
                CREATE TABLE IF NOT EXISTS scanner_module (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(72) NOT NULL UNIQUE,
                    url VARCHAR(100) NOT NULL
                );
                
                INSERT INTO scanner_module(name, url) VALUES('eth', 'lb://scan-eth') ON CONFLICT DO NOTHING; 
            """.trimIndent()
        }.then().subscribe()
    }

}