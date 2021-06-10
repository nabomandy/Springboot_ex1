package com.example.demo;

import java.util.Collections;

import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.JspConfigDescriptorImpl;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroup;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroupDescriptorImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = "controller")
@ComponentScan(basePackages = "service")
@EnableWebMvc
public class BoardBoot1Application {

	public static void main(String[] args) {
		SpringApplication.run(BoardBoot1Application.class, args);
	}

	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				System.out.println("resource");
				registry.addResourceHandler("/uploadFile/**").addResourceLocations("/uploadFile/").setCachePeriod(20);
				registry.addResourceHandler("/images/**").addResourceLocations("/images/").setCachePeriod(20);
			}
		};
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {

		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
		filter.setForceEncoding(true);
		filter.setEncoding("UTF-8");
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

	// <jsp-config>
	@Bean
	public ConfigurableServletWebServerFactory configurableServletWebServerFactory() {
		return new TomcatServletWebServerFactory() {

			@Override
			protected void postProcessContext(Context context) {
				super.postProcessContext(context);
				JspPropertyGroup jspPropertyGroup = new JspPropertyGroup();
				jspPropertyGroup.addUrlPattern("/view/*");
				jspPropertyGroup.addIncludePrelude("/common/head.jsp");

				JspPropertyGroupDescriptorImpl jspPropertyGroupDescriptor = new JspPropertyGroupDescriptorImpl(
						jspPropertyGroup);
				context.setJspConfigDescriptor(new JspConfigDescriptorImpl(
						Collections.singletonList(jspPropertyGroupDescriptor), Collections.emptyList()));
			}
		};
	}

	@Bean
	public ViewResolver getViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/view/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

//upload	
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver cm = new CommonsMultipartResolver();
		cm.setDefaultEncoding("UTF-8");
		return cm;
	}

}
