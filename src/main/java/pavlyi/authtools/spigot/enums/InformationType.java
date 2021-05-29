package pavlyi.authtools.spigot.enums;

public enum InformationType {
    TFA,
    MAIL,
    DISCORD,
    TELEGRAM;

    public static boolean isValid(String value) {
        return value.equalsIgnoreCase("TFA") || value.equalsIgnoreCase("MAIL")
                || value.equalsIgnoreCase("DISCORD") || value.equalsIgnoreCase("TELEGRAM");
    }

    public CharSequence toUpperCase() {
        return this.toString().toUpperCase();
    }

    public CharSequence toLowerCase() {
        return this.toString().toLowerCase();
    }
}
