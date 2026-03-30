package com.practice.todo.modules.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Optional Kafka-side projection for {@code project-events} (same payload as transactional outbox body).
 * Lives in the search slice so eventing does not depend on search (ArchUnit slice cycles).
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class ProjectSearchKafkaConsumer {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final SearchDocumentRepository searchDocumentRepository;

	@KafkaListener(topics = "project-events", groupId = "${spring.application.name:todo}-search")
	@Transactional
	public void onProjectEvent(String payload) {
		try {
			JsonNode root = MAPPER.readTree(payload);
			String type = root.hasNonNull("type") ? root.get("type").asText() : "project.created";
			if (!"project.created".equals(type)) {
				return;
			}
			JsonNode body = root.has("payload") ? root.get("payload") : root;
			UUID projectId = UUID.fromString(body.get("projectId").asText());
			UUID workspaceId = UUID.fromString(body.get("workspaceId").asText());
			String name = body.get("name").asText();
			SearchDocument d = searchDocumentRepository
					.findByDocTypeAndRefId("PROJECT", projectId)
					.orElseGet(SearchDocument::new);
			d.setDocType("PROJECT");
			d.setRefId(projectId);
			d.setTitle(name);
			d.setWorkspaceId(workspaceId);
			searchDocumentRepository.save(d);
		} catch (Exception ex) {
			log.warn("Kafka search projection skipped: {}", ex.getMessage());
		}
	}
}
