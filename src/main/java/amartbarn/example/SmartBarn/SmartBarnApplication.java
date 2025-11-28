package amartbarn.example.SmartBarn;

import amartbarn.example.SmartBarn.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartBarnApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartBarnApplication.class, args);
	}

	// Hàm này sẽ chạy ngay sau khi Spring Boot khởi động xong
	@Bean
	CommandLineRunner initData(DeviceService deviceService) {
		return args -> {
			System.out.println("--- KHOI TAO DU LIEU THIET BI ---");
			deviceService.initDevices(); // Tạo Quạt, Đèn, Phun sương nếu chưa có
		};
	}

}
