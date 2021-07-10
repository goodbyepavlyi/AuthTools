package pavlyi.authtools.spigot.authentication;

import pavlyi.authtools.spigot.enums.SetupPhase;

public class Setup {
    private String email;
    private SetupPhase phase;
    private int verificationCode;

    public Setup() {
        this.phase = SetupPhase.NONE;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SetupPhase getPhase() {
        return phase;
    }

    public void setPhase(SetupPhase phase) {
        this.phase = phase;

        if (!phase.equals(SetupPhase.NONE))
            return;

        setEmail("");
        setVerificationCode(0);
    }

    public int getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(int verificationCode) {
        this.verificationCode = verificationCode;
    }
}
