package net.unibld.server.web.rest.remote;

import net.unibld.server.web.rest.remote.model.BuildRequestDto;


public interface IBuildExecutor
    {
        String executeBuild(BuildRequestDto req);
    }

