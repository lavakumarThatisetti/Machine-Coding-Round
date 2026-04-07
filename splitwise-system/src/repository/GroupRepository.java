package repository;

import model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    void save(Group group);

    Optional<Group> findById(String groupId);

    List<Group> findAll();

    boolean existsById(String groupId);
}
