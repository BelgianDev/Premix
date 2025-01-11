package be.raft.premix.processor;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;

public class MixinCompilerPlugin implements Plugin {

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void init(JavacTask task, String... args) {
        System.out.println(this.getName() + " initialized.");

        task.addTaskListener(new MixinTaskHandler(task));
    }
}
