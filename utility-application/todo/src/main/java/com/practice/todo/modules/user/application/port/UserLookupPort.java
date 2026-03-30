package com.practice.todo.modules.user.application.port;

import com.practice.todo.modules.user.domain.User;
import java.util.UUID;

public interface UserLookupPort {

	User getById(UUID id);
}
