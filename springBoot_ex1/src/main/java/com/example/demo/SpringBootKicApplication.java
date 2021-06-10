package com.example.demo;

import java.util.Collections;

import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.util.descriptor.web.JspConfigDescriptorImpl;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroup;
import org.apache.tomcat.util.descriptor.web.JspPropertyGroupDescriptorImpl;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = "controller")
@ComponentScan(basePackages = "service")
@EnableWebMvc
public class SpringBootKicApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootKicApplication.class, args);
	}

	
	@Bean
	public WebMvcConfigurer webMvcConfigurer() {   //web-xml
		return new WebMvcConfigurer() {

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				System.out.println("resource");
				registry.addResourceHandler("/uploadFile/**").addResourceLocations("/uploadFile/").setCachePeriod(20);
				registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(20);
				registry.addResourceHandler("/common/**").addResourceLocations("/common/").setCachePeriod(20);
				registry.addResourceHandler("/memimg/**").addResourceLocations("/memimg/").setCachePeriod(20);
			}
			
			@Override
			  public void addViewControllers(ViewControllerRegistry registry) {
			    registry.addViewController("/index").setViewName("index");
			    registry.addViewController("/member/joinForm").setViewName("/member/joinForm");
			    registry.addViewController("/member/loginForm").setViewName("/member/loginForm");
			    registry.addViewController("/member/pictureimgForm").setViewName("/single/pictureimgForm");
			    registry.addViewController("/index").setViewName("index");
			  }
			
			
		};
	}
	
	
	
	
	
	
	@Bean  //jsp-config
	public ConfigurableServletWebServerFactory configurableServletWebServerFactory() {
		return new TomcatServletWebServerFactory() {

			@Override
			protected void postProcessContext(Context context) {
				super.postProcessContext(context);
				JspPropertyGroup jspPropertyGroup = new JspPropertyGroup();
				jspPropertyGroup.addUrlPattern("/view/board/*");
				jspPropertyGroup.addUrlPattern("/view/member/*");
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

	

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver cm = new CommonsMultipartResolver();
		cm.setDefaultEncoding("UTF-8");
		return cm;
	}
	 @Bean  //mybatis
	    public SqlSessionFactory sqlSessionFactory(DataSource dataSource)throws Exception{
	            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
	            sessionFactory.setDataSource(dataSource);
	            
	       
	            sessionFactory.setConfigLocation(new PathMatchingResourcePatternResolver()
	            		.getResource("classpath:mapper/mybatis-config.xml"));
	           
	            
	            return sessionFactory.getObject();
	    }
	 
	    @Bean
	    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
	        return new SqlSessionTemplate(sqlSessionFactory);
	    }

}
