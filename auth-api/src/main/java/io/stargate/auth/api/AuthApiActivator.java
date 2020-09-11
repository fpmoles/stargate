package io.stargate.auth.api;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.stargate.auth.AuthenticationService;
import io.stargate.auth.server.AuthApiServer;

public class AuthApiActivator implements BundleActivator, ServiceListener {
    private static final Logger log = LoggerFactory.getLogger(AuthApiActivator.class);

    private BundleContext context;
    private final AuthApiServer apiServer = new AuthApiServer();
    private ServiceReference authenticationServiceReference;

    static String AUTH_IDENTIFIER = System.getProperty("stargate.auth_id", "AuthTableBasedService");

    @Override
    public void start(BundleContext context) throws InvalidSyntaxException {
        this.context = context;
        log.info("Starting apiServer....");
        synchronized (apiServer) {
            try {
                context.addServiceListener(this, String.format("(AuthIdentifier=%s)", AUTH_IDENTIFIER));
            } catch (InvalidSyntaxException ise) {
                throw new RuntimeException(ise);
            }

            ServiceReference[] refs = context.getServiceReferences(AuthenticationService.class.getName(), null);
            if (refs != null) {
                for (ServiceReference ref : refs) {
                    // Get the service object.
                    Object service = context.getService(ref);
                    if (service instanceof AuthenticationService && ref.getProperty("AuthIdentifier") != null
                            && ref.getProperty("AuthIdentifier").equals(AUTH_IDENTIFIER)) {
                        this.apiServer.setAuthService((AuthenticationService) service);
                        this.apiServer.start();
                        log.info("Started authApiServer....");
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void stop(BundleContext context) {
        if (authenticationServiceReference != null) {
            context.ungetService(authenticationServiceReference);
        }
    }

    @Override
    public void serviceChanged(ServiceEvent serviceEvent) {
        int type = serviceEvent.getType();
        String[] objectClass = (String[]) serviceEvent.getServiceReference().getProperty("objectClass");
        synchronized (apiServer) {
            switch (type) {
                case (ServiceEvent.REGISTERED):
                    log.info("Service of type " + objectClass[0] + " registered.");
                    Object service = context.getService(serviceEvent.getServiceReference());

                    if (service instanceof AuthenticationService && serviceEvent.getServiceReference().getProperty("AuthIdentifier") != null
                            && serviceEvent.getServiceReference().getProperty("AuthIdentifier").equals(AUTH_IDENTIFIER)) {
                        log.info("Setting authenticationService in AuthApiActivator");
                        this.apiServer.setAuthService((AuthenticationService) service);
                    }

                    if (this.apiServer.getAuthService() != null) {
                        this.apiServer.start();
                        log.info("Started authApiServer....");
                    }

                    break;
                case (ServiceEvent.UNREGISTERING):
                    log.info("Service of type " + objectClass[0] + " unregistered.");
                    context.ungetService(serviceEvent.getServiceReference());
                    break;
                case (ServiceEvent.MODIFIED):
                    // TODO: [doug] 2020-06-15, Mon, 12:58 do something here...
                    log.info("Service of type " + objectClass[0] + " modified.");
                    break;
                default:
                    break;
            }
        }
    }
}
