/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Result;
import org.junit.Assert;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MockCommandExecutionListener extends AbstractCommandExecutionListener
{
   private boolean pre;
   private boolean post;

   @Override
   public void preCommandExecuted(UICommand command, UIExecutionContext context)
   {
      Assert.assertNotNull(command);
      Assert.assertNotNull(context);
      this.pre = true;
   }

   @Override
   public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
   {
      Assert.assertNotNull(command);
      Assert.assertNotNull(context);
      Assert.assertNotNull(result);
      this.post = true;
   }

   public boolean isPreExecuted()
   {
      return pre;
   }

   public boolean isPostExecuted()
   {
      return post;
   }
}
