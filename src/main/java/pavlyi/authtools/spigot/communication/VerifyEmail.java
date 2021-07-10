package pavlyi.authtools.spigot.communication;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import pavlyi.authtools.spigot.AuthTools;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyEmail {
    private final AuthTools instance = AuthTools.getInstance();

    public org.apache.commons.mail.Email email;

    public VerifyEmail() {
        this.email = new SimpleEmail();

        try {
            this.email.setSocketConnectionTimeout(500);
            this.email.setHostName(instance.getConfigHandler().SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_SMTP);
            this.email.setSmtpPort(instance.getConfigHandler().SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_PORT);
            this.email.setAuthentication(instance.getConfigHandler().SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_ADDRESS, instance.getConfigHandler().SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_PASSWORD);
            this.email.setSSLOnConnect(true);

            this.email.setFrom(instance.getConfigHandler().SETTINGS_RECOVERY_EMAIL_AUTHENTICATION_ADDRESS);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public org.apache.commons.mail.Email getEmail() {
        return email;
    }

    public String getSubject() {
        Pattern pattern = Pattern.compile("<subject=\"([^\"]*)\">");
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(getFile().getAbsolutePath()));
        } catch (FileNotFoundException exception) {
            instance.log("&r  &cError: &fEmail for Verification Code can't be found! Creating a new one...");
            exception.printStackTrace();

            instance.createEmailFile();

            return null;
        }

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);

                if (matcher.find())
                    return matcher.group(0).replace("<subject=\"", "").replace("\">", "");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public String getContent() {
        StringBuilder builder = new StringBuilder();

        if (!getFile().exists()) {
            instance.log("&r  &cError: &fEmail for Verification Code hasn't be found! Creating a new one...");
            instance.createEmailFile();
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(getFile().getAbsolutePath()));

            String content;
            while ((content = reader.readLine()) != null)
                builder.append(content);

            reader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }

        return builder.toString();
    }

    public void sendEmail() throws EmailException {
        email.send();
    }

    public File getFile() {
        return new File(instance.getDataFolder() + File.separator + "verification_code_email.html");
    }
}
