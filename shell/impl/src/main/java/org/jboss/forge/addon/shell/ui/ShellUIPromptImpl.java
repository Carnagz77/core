/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.ui;

import java.io.PrintStream;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.forge.addon.ui.input.UIPrompt;

/**
 * Implementation of {@link UIPrompt}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUIPromptImpl implements UIPrompt
{
   private final AeshConsole console;
   private final CommandInvocation commandInvocation;

   public ShellUIPromptImpl(AeshConsole console, CommandInvocation commandInvocation)
   {
      this.console = console;
      this.commandInvocation = commandInvocation;
   }

   @Override
   public String prompt(String message)
   {
      PrintStream out = console.getShell().out();
      out.print(message);
      String output;
      try
      {
         output = String.valueOf(commandInvocation.getInput().getInputKey().getAsChar());
      }
      catch (InterruptedException e)
      {
         output = null;
      }
      out.println();
      return output;
   }

   @Override
   public String promptSecret(String message)
   {
      PrintStream out = console.getShell().out();
      out.print(message);
      String output;
      try
      {
         output = String.valueOf(commandInvocation.getInput().getInputKey().getAsChar());
      }
      catch (InterruptedException e)
      {
         output = null;
      }
      out.println();
      return output;
   }

   @Override
   public boolean promptBoolean(String message)
   {
      return "Y".equalsIgnoreCase(prompt(message + " [y/N]"));
   }

}
