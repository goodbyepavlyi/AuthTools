package pavlyi.authtools.spigot.enums;

public enum HookType {
    STANDALONE,
    API,
    AUTHME,
    NLOGIN;

    public static boolean isValid(String value) {
        return value.equalsIgnoreCase("STANDALONE") || value.equalsIgnoreCase("API")
                || value.equalsIgnoreCase("AUTHME") || value.equalsIgnoreCase("NLOGIN");
    }
}
