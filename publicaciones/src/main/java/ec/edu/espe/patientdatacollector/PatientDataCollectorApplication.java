package ec.edu.espe.patientdatacollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PatientDataCollectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientDataCollectorApplication.class, args);
    }

}
