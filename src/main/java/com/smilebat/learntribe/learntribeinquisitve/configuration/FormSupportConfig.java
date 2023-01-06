package com.smilebat.learntribe.learntribeinquisitve.configuration;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration required by open feign for appending requests
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Configuration(proxyBeanMethods = false)
public class FormSupportConfig {

  /**
   * Encoder for open feign http client to append xml/form data.
   *
   * @param messageConverters the {@link HttpMessageConverters}.
   * @return bean of type {@link Encoder}.
   */
  @Bean
  public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
    return new SpringFormEncoder(new SpringEncoder(messageConverters));
  }
}
