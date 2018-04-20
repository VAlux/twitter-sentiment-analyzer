package com.alvo.twitteringestor.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitmqConfigurationProperties {

  private int port;
  private String host;
  private String username;
  private String password;
  private String realtimeQueueName;
  private String dataproxyQueueName;
  private String fanoutExchangeName;

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRealtimeQueueName() {
    return realtimeQueueName;
  }

  public void setRealtimeQueueName(String realtimeQueueName) {
    this.realtimeQueueName = realtimeQueueName;
  }

  public String getDataproxyQueueName() {
    return dataproxyQueueName;
  }

  public void setDataproxyQueueName(String dataproxyQueueName) {
    this.dataproxyQueueName = dataproxyQueueName;
  }

  public String getFanoutExchangeName() {
    return fanoutExchangeName;
  }

  public void setFanoutExchangeName(String fanoutExchangeName) {
    this.fanoutExchangeName = fanoutExchangeName;
  }
}
