package coordination;

import java.util.Set;

public interface ConsumerMember {
    String getConsumerId();

    void replaceAssignments(Set<Integer> newPartitions);
}
