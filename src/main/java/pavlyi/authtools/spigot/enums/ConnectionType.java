package pavlyi.authtools.spigot.enums;

public enum ConnectionType {
    YAML,
    MYSQL,
    SQLITE,
    MONGODB;

    public static boolean isValid(String value) {
        return value.equalsIgnoreCase("YAML") || value.equalsIgnoreCase("MYSQL")
                || value.equalsIgnoreCase("SQLITE") || value.equalsIgnoreCase("MONGODB");
    }

    public CharSequence toUpperCase() {
        return this.toString().toUpperCase();
    }

    public CharSequence toLowerCase() {
        return this.toString().toLowerCase();
    }
}
