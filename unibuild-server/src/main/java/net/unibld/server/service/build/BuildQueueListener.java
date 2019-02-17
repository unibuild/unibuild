package net.unibld.server.service.build;

import net.unibld.core.build.BuildEventListener;
import net.unibld.core.build.ProgressListener;

/**
 * Build queue listener interface that extends {@link BuildEventListener} and {@link ProgressListener}.
 * @author andor
 *
 */
public interface BuildQueueListener extends BuildEventListener,ProgressListener {

}
