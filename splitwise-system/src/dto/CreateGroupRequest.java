package dto;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public record CreateGroupRequest(String groupId, String groupName, Set<String> memberIds) {
    public CreateGroupRequest(String groupId, String groupName, Set<String> memberIds) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.memberIds = memberIds == null ? Set.of() : Collections.unmodifiableSet(new LinkedHashSet<>(memberIds));
    }
}
