package SecureNotesPracticeProject.SecureNotesPracticeProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"entity", "restControllers", "services", "repo", "filter", "configs"})
@EntityScan(basePackages = {"entity"})
@EnableJpaRepositories(basePackages = {"repos"})
public class SecureNotesPracticeProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureNotesPracticeProjectApplication.class, args);
	}

}
