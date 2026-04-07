package model;

public class BookingResult {
    private final BookingStatus status;
    private final String appointmentId;
    private final String message;

    public BookingResult(BookingStatus status, String appointmentId, String message) {
        this.status = status;
        this.appointmentId = appointmentId;
        this.message = message;
    }

    public static BookingResult confirmed(String appointmentId, String message) {
        return new BookingResult(BookingStatus.CONFIRMED, appointmentId, message);
    }

    public static BookingResult waitlisted(String message) {
        return new BookingResult(BookingStatus.WAITLISTED, null, message);
    }

    public static BookingResult rejected(String message) {
        return new BookingResult(BookingStatus.REJECTED, null, message);
    }

    public BookingStatus getStatus() {
        return status;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "BookingResult{" +
                "status=" + status +
                ", appointmentId='" + appointmentId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}