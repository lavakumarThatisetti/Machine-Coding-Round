package dto;

import java.math.BigDecimal;

/**
 * @param groupId nullable for non-group/global settlement
 */
public record SettleUpRequest(String settlementId, String fromUserId, String toUserId, BigDecimal amount,
                              String groupId) {
}