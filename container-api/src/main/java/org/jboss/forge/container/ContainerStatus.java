/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container;

/**
 * The container status
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public enum ContainerStatus
{
   STARTED, STOPPED;

   public boolean isStarted()
   {
      return this == STARTED;
   }

   public boolean isStopped()
   {
      return this == STOPPED;
   }
}
