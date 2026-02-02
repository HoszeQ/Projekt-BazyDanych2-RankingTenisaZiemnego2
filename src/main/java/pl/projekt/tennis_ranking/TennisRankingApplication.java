package pl.projekt.tennis_ranking;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TennisRankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TennisRankingApplication.class, args);
	}

	@Bean
	CommandLineRunner dbPing(DataSource dataSource) {
		return args -> {
			try (Connection c = dataSource.getConnection()) {
				System.out.println("✅ DB CONNECTED: " + c.getMetaData().getURL());
				System.out.println("✅ DB USER: " + c.getMetaData().getUserName());
			} catch (Exception e) {
				System.out.println("❌ DB CONNECTION FAILED: " + e.getMessage());
				e.printStackTrace();
			}
		};
	}
}
