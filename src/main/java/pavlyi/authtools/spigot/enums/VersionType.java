package pavlyi.authtools.spigot.enums;

import pavlyi.authtools.spigot.AuthTools;

public enum VersionType {
    ONE_NINE,
    ONE_TEN,
    ONE_ELEVEN,
    ONE_TWELVE,
    ONE_THIRTEEN;

    public static VersionType getVersion() {
        String version = AuthTools.getInstance().getServer().getClass().getPackage().getName().substring(AuthTools.getInstance().getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);

        switch (version) {
            case "v1_9_R1":
                return ONE_NINE;
            case "v1_10_R1":
                return ONE_TEN;
            case "v1_11_R1":
                return ONE_ELEVEN;
            case "v1_12_R1":
                return ONE_TWELVE;
            case "v1_13_R1":
                return ONE_THIRTEEN;
        }

        return null;
    }
}
