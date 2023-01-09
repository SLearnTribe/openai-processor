package com.smilebat.learntribe.reactor.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smilebat.learntribe.dataaccess.jpa.entity.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka Consumer for the receiving notifications.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Component
@Slf4j
public class KafkaConsumer {

  private final ObjectMapper mapper = new ObjectMapper();

  /**
   * Listener for receiving messages from Kafka Topic
   *
   * @param message the message
   * @throws JsonProcessingException on failing to read.
   */
  @KafkaListener(
      groupId = ApplicationConstant.GROUP_ID_JSON,
      topics = ApplicationConstant.TOPIC_NAME,
      containerFactory = ApplicationConstant.KAFKA_LISTENER_CONTAINER_FACTORY)
  public void receivedMessage(String message) throws JsonProcessingException {
    final UserProfile profile = mapper.readValue(message, UserProfile.class);
    log.info("Json message received using Kafka listener {}", profile);
  }
}
