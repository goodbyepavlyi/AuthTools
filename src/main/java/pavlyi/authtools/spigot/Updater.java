package pavlyi.authtools.spigot;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Updater {
    private final String pluginVersion;
    private final String USER_AGENT = "AutoUpdater AuthTools";
    private final int id = 90387;
    private final String SPIGET = "https://api.spiget.org/v2/resources/" + id + "/";
    private final String SPIGOT = "https://api.spigotmc.org/legacy/update.php?resource=" + id;

    private Result result = Result.BAD_ID;

    public Updater(String version) {
        this.pluginVersion = version;
    }

    public void checkForUpdate() {
        try {
            URL url = new URL(SPIGOT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("User-Agent", USER_AGENT);
            int code = connection.getResponseCode();

            if (code == 404) {
                connection.disconnect();
                this.result = Result.BAD_ID;
                return;
            }

            Scanner scanner = new Scanner(url.openStream());
            String version = String.valueOf(scanner.nextLine());

            if (!pluginVersion.equalsIgnoreCase(version)) {
                this.result = Result.UPDATE_FOUND;
                return;
            }

            this.result = Result.NO_UPDATE;

            scanner.close();
            connection.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getDownloadURL() {
        try {
            URL url = new URL(SPIGET);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("User-Agent", "AuthTools");

            int code = connection.getResponseCode();

            if (code == 404) {
                connection.disconnect();
                this.result = Result.BAD_ID;
                return null;
            }

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonElement element = new JsonParser().parse(reader);

            try {
                if (element.getAsJsonObject().get("external").getAsBoolean())
                    return element.getAsJsonObject().get("file").getAsJsonObject().get("externalUrl").getAsString();
                else
                    return "https://www.spigotmc.org/" + element.getAsJsonObject().get("file").getAsJsonObject().get("url").getAsString();
            } catch (NullPointerException exception) {
                exception.printStackTrace();
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Result getResult() {
        return result;
    }

    public enum Result {
        UPDATE_FOUND,
        NO_UPDATE,
        BAD_ID
    }
}