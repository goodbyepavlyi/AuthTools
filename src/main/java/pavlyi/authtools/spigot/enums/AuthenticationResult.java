package pavlyi.authtools.spigot.enums;

public enum AuthenticationResult {
    FAILED,
    TFA_LOGGED,
    TFA_ALREADY_LOGGED_IN,
    TFA_REGISTERED,
    TFA_ALREADY_REGISTERED,
    INVALID_CODE,
    SESSION
}
