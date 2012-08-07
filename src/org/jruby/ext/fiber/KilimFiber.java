package org.jruby.ext.fiber;


import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyLocalJumpError.Reason;
import org.jruby.exceptions.JumpException;
import org.jruby.exceptions.RaiseException;
import org.jruby.anno.JRubyClass;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import kilim.*;


@JRubyClass(name = "Fiber")
public class KilimFiber extends Fiber {
  private volatile IRubyObject slot;
  private kilim.Task task;
  private kilim.Mailbox<String> mb = new Mailbox<String>();
  public boolean started = false;


  public KilimFiber(Ruby runtime, RubyClass type) {
    super(runtime, type);
  }

  protected void initFiber(ThreadContext context) {
    final Ruby runtime = context.runtime;

    task = new kilim.Task() {

      public void execute() throws Pausable {
        ThreadContext context = runtime.getCurrentContext();
        context.setFiber(KilimFiber.this);
        context.setThread(context.getThread());
        slot = context.nil;

        while (true) {
          String s = mb.get();

          if (s.equals("resume")) {
            slot = block.yieldArray(context, slot, null, null);
          }
        }
      };
    };
    task.start();
  }

  protected IRubyObject resumeOrTransfer(ThreadContext context, IRubyObject arg, boolean transfer) {
    mb.putnb("resume");
    return slot;
  }

  public IRubyObject yield(ThreadContext context, IRubyObject arg) {
    return arg;
  }

  public boolean isAlive() {
    return true;
  }
}
