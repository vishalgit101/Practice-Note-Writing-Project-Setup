package configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	// Frontend url
	@Value("${frontend.url}")
	private String frontendUrl;
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
			return new WebMvcConfigurer() {

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				.allowedOrigins(frontendUrl)
				.allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
				.allowedHeaders("*") // means all the headers
				.allowCredentials(true)
				.maxAge(3600); // for one hour... preflight Options request is send by the brower to cache the results for 1h
				// it is used to minimize the CORS overhead
			}
			
			 // Static Resource Mapping
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Serve files from 'uploads/profile/' folder
                registry.addResourceHandler("/profile/**")
                .addResourceLocations("file:/D:/uploads/profile");
            }
        };
	}
	
	
}
