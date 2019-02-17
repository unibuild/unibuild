package net.unibld.client.desktop.util.javafx;

import javafx.application.HostServices;

public abstract class JavaFXController {

    private HostServices hostServices ;

    public HostServices getHostServices() {
        return hostServices ;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices ;
    }
   
}