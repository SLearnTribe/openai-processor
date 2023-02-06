package com.smilebat.learntribe.openai.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;
import com.smilebat.learntribe.assessment.AssessmentRequest;
import com.smilebat.learntribe.dataaccess.ChallengeRepository;
import com.smilebat.learntribe.enums.AssessmentDifficulty;
import com.smilebat.learntribe.kafka.KafkaSkillsRequest;
import com.smilebat.learntribe.openai.kafka.KafkaProducer;
import com.smilebat.learntribe.openai.services.ChallengeFactory.ChallengeFactoryRequest;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Store for computing challenges and distribution.
 *
 * <p>Copyright &copy; 2022 Smile .Bat
 *
 * @author Pai,Sai Nandan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeStore {

  private final ChallengeFactory factory;

  private final ChallengeRepository challengeRepository;

  private final KafkaProducer producer;

  private final ObjectMapper mapper;

  @Value("${feature.maxquestions}")
  private Integer maxQuestionsCap;

  @Value("${feature.openai}")
  private boolean openAiFeature;

  /**
   * Creates assessments and challenges for missing skills. Acknowledges the assessment service for
   * missing assessments.
   *
   * @param request the {@link AssessmentRequest}.
   * @throws JsonProcessingException on failure to process json.
   */
  public void createAssessments(KafkaSkillsRequest request) throws JsonProcessingException {
    Set<String> skills = request.getSkills();
    Verify.verifyNotNull(skills, "Skills cannot be null");
    // Preconditions.checkArgument(skills.size() > 0, "Skills cannot be empty");

    if (openAiFeature) {
      skills.forEach(this::createAssessment);
    }
    final AssessmentRequest assessmentRequest = request.getAssessmentRequest();
    if (assessmentRequest != null) {
      final List<String> assigneeEmails = assessmentRequest.getAssigneeEmails();
      /*If request contains assigneeEmails forward back to assessment service*/
      if (assigneeEmails != null && !assigneeEmails.isEmpty()) {
        producer.sendMessage(mapper.writeValueAsString(assessmentRequest));
      }
    }
  }

  @Transactional
  private void createAssessment(String skill) {
    Verify.verifyNotNull(skill, "Skill cannot be null");
    final Integer existingChallenges = challengeRepository.countBySkill(skill);
    int pendingChallenges = getPendingChallengeCount(existingChallenges);
    ChallengeFactoryRequest factoryRequest =
        ChallengeFactoryRequest.builder()
            .difficulty(AssessmentDifficulty.BEGINNER.getString())
            .skill(skill)
            .quantity(pendingChallenges)
            .build();
    factory.createChallenges(factoryRequest);
    //    for (Challenge challenge : challenges) {
    //      challenge.setSkill(skill.trim().toUpperCase());
    //
    // challenge.setDifficulty(AssessmentDifficulty.getFromValue(factoryRequest.getDifficulty()));
    //    }
    // challengeRepository.saveAll(challenges);
  }

  private int getPendingChallengeCount(Integer existingChallenges) {
    int pendingChallenges = 0;
    if (existingChallenges < maxQuestionsCap) {
      pendingChallenges = maxQuestionsCap - existingChallenges;
    }
    return pendingChallenges;
  }
}
