package com.example.sbpromgrafanadockercompose;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SbPromGrafanaDockerComposeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbPromGrafanaDockerComposeApplication.class, args);
    }

}

/*
https://betterprogramming.pub/how-to-monitor-a-spring-boot-app-with-prometheus-and-grafana-22e2338f97fc

How To Monitor a Spring Boot App With Prometheus and Grafana

    Моніторинг мікросервісів є складним завданням. Це важливий крок до стабільної системи виробництва. Спостерігаючи за 
    різними показниками, ми розуміємо, як система поводиться за різних умов навантаження та яким показникам потрібно 
    приділити більше уваги.
    
    У цьому посібнику я покажу вам, як налаштувати Prometheus і Grafana для моніторингу програми Spring Boot.
    
    Ви дізнаєтеся, як:
        > Налаштуйте Spring Boot Actuator, щоб увімкнути показники
        > Налаштуйте Prometheus для аналізу показників
        > Використовуйте PromQL для запиту різних показників в інтерфейсі користувача Prometheus
        > Візуалізуйте показники на інформаційній панелі Grafana
        
        
    Зрозумійте компоненти
        По-перше, давайте поглянемо на діаграму нижче, щоб зрозуміти, як працює моніторинг:
        
                  View                 Query Prometheus              Scrape exposed metrics
                Dashboard               as Data Sources                 by Micrometer
        User -------------->  Grafana  ----------------->  Prometheus  -----------------> Spring Boot     
    
        > Програма Spring Boot має модуль Actuator, який дозволяє нам контролювати нашу програму та керувати нею. 
          Він бездоганно інтегрується зі сторонніми інструментами моніторингу, такими як Prometheus.
        > Micrometer збирає показники з нашого додатка та надає їх зовнішнім системам, у цьому випадку Prometheus.
        > Grafana — це візуальний інструмент, який показує показники з джерела даних (наприклад, Prometheus) на інформаційній панелі.
    
    
    Додайте залежності:
        implementation 'org.springframework.boot:spring-boot-starter-actuator'
        runtimeOnly 'io.micrometer:micrometer-registry-prometheus'
    
    
    Налаштувати Spring Boot Actuator
        Тепер давайте налаштуємо application.ymlфайл, щоб увімкнути моніторинг:
        
                server:
                  port: 9002
                  
                spring:
                  application:
                    name: monitoring-demo
                
                management:
                  endpoints:
                    web:
                      base-path: /actuator
                      exposure:
                        include: [ "health","prometheus", "metrics" ]
                  endpoint:
                    health:
                      show-details: always
                    metrics:
                      enabled: true
                    prometheus:
                      enabled: true
   
   
       Зверніть увагу, що ми ввімкнули   include: [ "health","prometheus", "metrics" ] 
        
       Запустіть програму та відкрийте http://localhost:9002/actuator 
       
           {
            "_links": {
                "self": {
                "href": "http://localhost:9002/actuator",
                "templated": false
                },
                "health": {
                "href": "http://localhost:9002/actuator/health",
                "templated": false
                },
                "prometheus": {
                "href": "http://localhost:9002/actuator/prometheus",
                "templated": false
                },
                "metrics": {
                "href": "http://localhost:9002/actuator/metrics",
                "templated": false
                }
                }
            }
            
            
            http://localhost:9002/actuator/health
                {
                    "status": "UP",
                    "components": {
                        ...
                        "ping": {
                            "status": "UP"
                        }
                    }
                }
                
            
        http://localhost:9002/actuator/prometheus ендпоінт prometheus показує різні показники, такі як стан потоків JVM, 
        інформацію про запити HTTP-сервера тощо.
        
        Кінцева точка metrics надає інформацію про пам’ять JVM, використання ЦП системи тощо.
        
        
        
    Налаштувати Prometheus
    
        Prometheus періодично збирає показники, і йому потрібно знати, як часто їх знімати. Для цього нам потрібно 
        налаштувати файл конфігурації.
        
        Створіть новий файл config/prometheus.yml із таким вмістом:
        
            scrape_configs:
              - job_name: 'sample_monitoring'
                scrape_interval: 3s
                metrics_path: '/actuator/prometheus'
                static_configs:
                  - targets: ['192.168.0.98:9002']
        
        
        Important notes:
            > It instructs Prometheus to scrape the app every three seconds.
            > The target is the host and port of our app.
            > The path we want to scrape is the prometheus path, which you saw earlier.



    Install and Run Prometheus and Grafana in Docker
        We’ll create a docker-compose.yml file to install and start Prometheus and Grafana.
        
                version: '3'
        
                services:
                
                  prometheus:
                    image: prom/prometheus
                    container_name: prometheus-container
                    volumes:
                      - ./config/prometheus.yml:/etc/prometheus/prometheus.yml
                      - ./prometheus:/prometheus
                    ports:
                      - "9090:9090"
                    restart: unless-stopped
                
                  grafana:
                    image: grafana/grafana-oss
                    container_name: grafana-container
                    depends_on:
                      - prometheus
                    ports:
                      - "3000:3000"
                    volumes:
                      - ./config/grafana:/etc/grafana/provisioning/datasource
                    environment:
                      - GF_SECURITY_ADMIN_PASSWORD=admin
                      - GF_SERVER_DOMAIN=localhost
                    restart: unless-stopped
        
        
        У корені проєкту є папка config  у якій є файли що прометеуса і гафани:
            > config/prometheus.yml    
            > config/grafana/datasource.yml
            
            
    Let’s start the services by running this command:
         docker-compose up -d
    
    
    
    Prometheus is accessible via http://localhost:9090/.

    Grafana is running on http://localhost:3000/
   
 */