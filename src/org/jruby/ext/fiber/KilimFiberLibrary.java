package org.jruby.ext.fiber;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.Block;
import org.jruby.runtime.load.Library;
import org.jruby.runtime.builtin.IRubyObject;

public class KilimFiberLibrary implements Library {
  public void load(final Ruby runtime, boolean wrap) {
    RubyClass cFiber = runtime.defineClass("Fiber", runtime.getObject(), new ObjectAllocator() {
      public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
        return new KilimFiber(runtime, klazz);
      }
    });

    cFiber.defineAnnotatedMethods(Fiber.class);
    cFiber.defineAnnotatedMethods(FiberMeta.class);

    Fiber rootFiber = new KilimFiber(runtime, cFiber).makeRootFiber();
    runtime.setRootFiber(rootFiber);
    runtime.getCurrentContext().setFiber(rootFiber);
  }
}
