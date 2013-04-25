/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.addons;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.container.impl.AddonImpl;
import org.jboss.forge.container.impl.AddonRunnable;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class StopAddonCallable implements Callable<Void>
{
   private static final Logger logger = Logger.getLogger(StopAllAddonsVisitor.class.getName());

   private AddonImpl addon;
   private AddonTree tree;

   public StopAddonCallable(AddonTree tree, AddonImpl addon)
   {
      super();
      this.tree = tree;
      this.addon = addon;
   }

   @Override
   public Void call() throws Exception
   {
      if (addon != null)
      {
         AddonRunnable runnable = ((AddonImpl) addon).getRunnable();
         try
         {
            if (runnable != null)
            {
               runnable.shutdown();
            }
         }
         catch (Exception e)
         {
            logger.log(Level.WARNING, "Failed to shut down addon " + addon, e);
         }
         finally
         {
            Future<Void> future = addon.getFuture();
            if (future != null && !future.isDone())
               future.cancel(true);

            Set<AddonDependency> dependencies = addon.getDependencies();
            ((AddonImpl) addon).reset();
            addon.setDirty(false);

            for (AddonDependency dependency : dependencies)
            {
               tree.reattach(dependency.getDependency());
            }
         }
      }
      return null;
   }

}
