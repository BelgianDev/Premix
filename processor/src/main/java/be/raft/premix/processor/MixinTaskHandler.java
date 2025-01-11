package be.raft.premix.processor;

import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;

public class MixinTaskHandler implements TaskListener {
    private final JavacTask task;

    public MixinTaskHandler(JavacTask task) {
        this.task = task;
    }

    @Override
    public void started(TaskEvent e) {

    }

    @Override
    public void finished(TaskEvent e) {

    }
}
