package net.unibld.server.web.jsf;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnibuildExceptionHandler extends ExceptionHandlerWrapper {
	private static final Logger LOG=LoggerFactory.getLogger(UnibuildExceptionHandler.class);
	private ExceptionHandler wrapped;

    public UnibuildExceptionHandler(ExceptionHandler wrapped) {
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

            //See if it's an exception I'm interested in
            /*if (t instanceof MyCustomException) {
                try {
                    //do something exciting with the exception
                } finally {
                    //remove it if you processed it
                    events.remove();
                }
            }*/
            LOG.error("JSF exception",t);

            //Let the next ExceptionHandler(s) deal with the others
            getWrapped().handle();
        }
    }

}
