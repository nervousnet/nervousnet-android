package ch.ethz.coss.nervousnet.hub.ui.adapters;

public class NervousnetNode {
    private String nodeName;
    private String ip;

    public NervousnetNode(String nodeName, String ip) {
        this.nodeName = nodeName;
        this.ip = ip;
    }

    public String getName() {
        return nodeName;
    }

    public String getIp() {
        return ip;
    }

}
