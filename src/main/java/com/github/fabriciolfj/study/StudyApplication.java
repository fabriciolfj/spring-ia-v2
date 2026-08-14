package com.github.fabriciolfj.study;

import com.github.fabriciolfj.study.service.OrchestratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class StudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyApplication.class, args);
	}

	/*@Bean
	CommandLineRunner demo(OrchestratorService orchestratorService) {

		return args -> {
			String response = orchestratorService.ask(
					"""
                    Perform the following tasks:
                    - Review the code quality.
                    - Generate concise technical documentation like user guide.
                    """
			);
			log.info("{}", response);
		};
	}*/

}
