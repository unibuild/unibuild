package net.unibld.server.web.jsf;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class ApplicationExceptionHandlerFactory extends ExceptionHandlerFactory {

    ExceptionHandlerFactory delegateFactory;

    public ApplicationExceptionHandlerFactory(ExceptionHandlerFactory delegateFactory) {
        this.delegateFactory = delegateFactory;
    }

    public ExceptionHandler getExceptionHandler() {
        return new ApplicationExceptionHandler(delegateFactory.getExceptionHandler());
    }
}