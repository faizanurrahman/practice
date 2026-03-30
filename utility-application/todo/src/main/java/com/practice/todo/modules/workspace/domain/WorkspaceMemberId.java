package com.practice.todo.modules.workspace.domain;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMemberId implements Serializable {

	private UUID workspaceId;
	private UUID userId;
}
