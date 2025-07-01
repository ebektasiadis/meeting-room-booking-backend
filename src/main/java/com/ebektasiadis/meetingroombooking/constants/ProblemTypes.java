package com.ebektasiadis.meetingroombooking.constants;

public final class ProblemTypes {
    public static final String VALIDATION_ERROR = "validation-error";
    public static final String INTERNAL_ERROR = "internal-error";

    public static final String USER_NOT_FOUND_ERROR = "user-not-found";
    public static final String USER_USERNAME_EXISTS_ERROR = "user-username-exists";
    public static final String USER_EMAIL_EXISTS_ERROR = "user-email-exists";

    public static final String MEETING_ROOM_NOT_FOUND_ERROR = "meeting-room-not-found";
    public static final String MEETING_ROOM_NAME_EXISTS_ERROR = "meeting-room-name-exists";

    public static final String BOOKING_NOT_FOUND_ERROR = "booking-not-found";
    public static final String BOOKING_DATE_CONFLICT_ERROR = "booking-date-conflict-exists";
    public static final String BOOKING_INVALID_DATE_ERROR = "booking-invalid-date";
    public static final String BOOKING_PAST_START_DATE_ERROR = "booking-past-start-date";
    public static final String BOOKING_PAST_END_DATE_ERROR = "booking-past-end-date";

    private ProblemTypes() {
    }

}
