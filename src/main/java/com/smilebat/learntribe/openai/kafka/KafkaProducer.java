package com.smilebat.learntribe.openai.kafka;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka Producer for the sending notifications.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@Slf4j
@ToString
public class KafkaProducer {
  @Autowired private KafkaTemplate<String, Object> kafkaTemplate;

  @Value("${kafka.topic.out}")
  private String outTopic;

  /**
   * Sends a notifcation to assessment service.
   *
   * @param message the message to be sent
   */
  public void sendMessage(String message) {
    try {
      kafkaTemplate.send(outTopic, message);
    } catch (Exception e) {
      log.info("Unable to send message {} to {}", message, outTopic);
      throw new RuntimeException(e);
    }
  }
}
