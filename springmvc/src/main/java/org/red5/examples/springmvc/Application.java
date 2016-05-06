package org.red5.examples.springmvc;

import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.scope.IScope;

public class Application extends ApplicationAdapter {

    @Override
    public boolean appStart(IScope app) {
        super.appStart(app);
        log.info("springmvc appStart");
        return super.appStart(app);
    }

}
