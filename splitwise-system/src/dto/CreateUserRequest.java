package dto;


public record CreateUserRequest(String userId, String name, String email, String mobile) {
}