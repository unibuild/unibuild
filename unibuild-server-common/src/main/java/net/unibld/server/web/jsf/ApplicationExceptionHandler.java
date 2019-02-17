package net.unibld.server.web.jsf;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationExceptionHandler extends ExceptionHandlerWrapper {
	private static final Logger LOG=LoggerFactory.getLogger(ApplicationExceptionHandler.class);
	private ExceptionHandler wrapped;

    public ApplicationExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    public javax.faces.context.ExceptionHandler getWrapped() {
        return wrapped;
    }

    public void handle() throws FacesException {
        Iterator events = getUnhandledExceptionQueuedEvents().iterator();

        //Iterate through the queued exceptions
        while (events.hasNext()) {
            ExceptionQueuedEvent event = (ExceptionQueuedEvent) events.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
            Throwable t = context.getException();

            
            LOG.error("JSF handled exception",t);

            //Let the next ExceptionHandler(s) deal with the others
            getWrapped().handle();
        }
    }

}
