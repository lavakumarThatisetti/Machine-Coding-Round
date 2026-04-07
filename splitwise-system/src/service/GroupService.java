package service;

import dto.AddMemberToGroupRequest;
import dto.CreateGroupRequest;
import exception.DuplicateEntityException;
import exception.EntityNotFoundException;
import exception.ValidationException;
import model.Group;
import repository.GroupRepository;
import repository.UserRepository;

import java.util.List;
import java.util.Objects;

public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = Objects.requireNonNull(groupRepository, "GroupRepository cannot be null");
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
    }

    public Group createGroup(CreateGroupRequest request) {
        validate(request);

        if (groupRepository.existsById(request.groupId())) {
            throw new DuplicateEntityException("Group already exists with id: " + request.groupId());
        }

        for (String memberId : request.memberIds()) {
            if (!userRepository.existsById(memberId)) {
                throw new EntityNotFoundException("User not found for group membership: " + memberId);
            }
        }

        Group group = new Group(
                request.groupId(),
                request.groupName(),
                request.memberIds()
        );

        groupRepository.save(group);
        return group;
    }

    public void addMember(AddMemberToGroupRequest request) {
        validate(request);

        Group group = groupRepository.findById(request.groupId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + request.groupId()));

        if (!userRepository.existsById(request.userId())) {
            throw new EntityNotFoundException("User not found: " + request.userId());
        }

        if (group.hasMember(request.userId())) {
            return;
        }

        group.addMember(request.userId());
        groupRepository.save(group);
    }

    public Group getGroup(String groupId) {
        if (groupId == null || groupId.isBlank()) {
            throw new ValidationException("Group id cannot be blank");
        }

        return groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + groupId));
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public boolean isMember(String groupId, String userId) {
        Group group = getGroup(groupId);
        return group.hasMember(userId);
    }

    private void validate(CreateGroupRequest request) {
        if (request == null) {
            throw new ValidationException("CreateGroupRequest cannot be null");
        }
        if (request.groupId() == null || request.groupId().isBlank()) {
            throw new ValidationException("Group id cannot be blank");
        }
        if (request.groupName() == null || request.groupName().isBlank()) {
            throw new ValidationException("Group name cannot be blank");
        }
        if (request.memberIds() == null || request.memberIds().isEmpty()) {
            throw new ValidationException("Group must contain at least one member");
        }
    }

    private void validate(AddMemberToGroupRequest request) {
        if (request == null) {
            throw new ValidationException("AddMemberToGroupRequest cannot be null");
        }
        if (request.groupId() == null || request.groupId().isBlank()) {
            throw new ValidationException("Group id cannot be blank");
        }
        if (request.userId() == null || request.userId().isBlank()) {
            throw new ValidationException("User id cannot be blank");
        }
    }
}
