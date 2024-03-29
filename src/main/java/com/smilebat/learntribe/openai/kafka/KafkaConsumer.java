package com.smilebat.learntribe.openai.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smilebat.learntribe.kafka.KafkaProfileRequest;
import com.smilebat.learntribe.kafka.KafkaSkillsRequest;
import com.smilebat.learntribe.openai.services.ChallengeStore;
import com.smilebat.learntribe.openai.services.SummaryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class KafkaConsumer {

  public static final String KAFKA_LISTENER_CONTAINER_FACTORY = "kafkaListenerContainerFactory";

  private final ObjectMapper mapper;

  private final ChallengeStore challengeStore;

  private final SummaryFactory summaryFactory;

  @Value("${kafka.groupid}")
  private final String groupId = "sb-group-1";

  @Value("${kafka.topic.in.ast}")
  private final String inTopicAst = "challenge-store-event-1";

  @Value("${kafka.topic.in.sum}")
  private final String inTopicSum = "summaries-store-event-1";

  /**
   * Listener for receiving messages from Kafka Topic
   *
   * @param message the message
   * @throws JsonProcessingException on failing to read.
   */
  @KafkaListener(
      groupId = groupId,
      topics = inTopicAst,
      containerFactory = KAFKA_LISTENER_CONTAINER_FACTORY)
  public void receivedMessage(String message) throws JsonProcessingException {
    final KafkaSkillsRequest request = mapper.readValue(message, KafkaSkillsRequest.class);
    log.info("Json message received using Kafka listener {}", request.getSkills());
    try {
      challengeStore.createAssessments(request);
    } catch (Exception ex) {
      log.info("Failed processing the Kafka Message for User Assessment:");
      throw new RuntimeException(ex);
    }
  }

  /**
   * Loads request from queue
   *
   * @param message the message
   * @throws JsonProcessingException on procesing failure.
   */
  @KafkaListener(
      groupId = groupId,
      topics = inTopicSum,
      containerFactory = KAFKA_LISTENER_CONTAINER_FACTORY)
  public void getSummaries(String message) throws JsonProcessingException {
    final KafkaProfileRequest request = mapper.readValue(message, KafkaProfileRequest.class);
    log.info("Json message received using Kafka listener: {}", request);
    try {
      summaryFactory.createSummaries(request);
    } catch (Exception ex) {
      log.info("Failed processing the kafka message for Profile Summaries: ");
      throw new RuntimeException(ex);
    }
  }
}
