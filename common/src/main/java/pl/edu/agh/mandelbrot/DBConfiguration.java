package pl.edu.agh.mandelbrot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DBConfiguration {

    @Bean
    public MandelbrotResultDAO getConnection() throws URISyntaxException, SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        return MandelbrotResultDAO.createDAO(DriverManager.getConnection(dbUrl));
    }
}
