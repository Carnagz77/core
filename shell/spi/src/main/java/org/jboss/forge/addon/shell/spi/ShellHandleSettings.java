/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.spi;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * A parameter object to initialize the shell
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellHandleSettings
{
   private File currentResource;
   private InputStream stdIn;
   private PrintStream stdOut;
   private PrintStream stdErr;
   private Terminal terminal;

   public ShellHandleSettings()
   {
   }

   public File currentResource()
   {
      return currentResource;
   }

   public ShellHandleSettings currentResource(File currentResource)
   {
      this.currentResource = currentResource;
      return this;
   }

   public InputStream stdIn()
   {
      return stdIn;
   }

   public ShellHandleSettings stdIn(InputStream stdIn)
   {
      this.stdIn = stdIn;
      return this;
   }

   public PrintStream stdOut()
   {
      return stdOut;
   }

   public ShellHandleSettings stdOut(PrintStream stdOut)
   {
      this.stdOut = stdOut;
      return this;
   }

   public PrintStream stdErr()
   {
      return stdErr;
   }

   public ShellHandleSettings stdErr(PrintStream stdErr)
   {
      this.stdErr = stdErr;
      return this;
   }

   public Terminal terminal()
   {
      return terminal;
   }

   public ShellHandleSettings terminal(Terminal terminal)
   {
      this.terminal = terminal;
      return this;
   }

}