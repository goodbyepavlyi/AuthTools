package pavlyi.authtools.spigot.authentication;

import java.net.InetSocketAddress;

public final class Session {
    private final InetSocketAddress ip;
    private final long endsAt;

    public Session(InetSocketAddress ip, long endsAt) {
        this.ip = ip;
        this.endsAt = endsAt;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object.getClass() == getClass())
            return (object.equals(ip) || object.equals(endsAt));

        return false;
    }

    public long getTime() {
        return endsAt;
    }

    public InetSocketAddress getIP() {
        return ip;
    }
}
