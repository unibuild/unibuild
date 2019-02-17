package net.unibld.server.web.jsf;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class UnibuildExceptionHandlerFactory extends ExceptionHandlerFactory {

    ExceptionHandlerFactory delegateFactory;

    public UnibuildExceptionHandlerFactory(ExceptionHandlerFactory delegateFactory) {
        this.delegateFactory = delegateFactory;
    }

    public ExceptionHandler getExceptionHandler() {
        return new UnibuildExceptionHandler(delegateFactory.getExceptionHandler());
    }
}