package net.unibld.core.task.impl.sys;

import net.unibld.core.task.annotations.Task;
import net.unibld.core.task.impl.ExecTask;

@Task(name="sh",runnerClass=ShTaskRunner.class)
public class ShTask extends ExecTask {

}
