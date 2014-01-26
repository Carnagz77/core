/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.rest.generation;

import java.util.List;

import org.jboss.forge.parser.java.JavaClass;

/**
 * Generates a REST resource based on a JPA entity
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface RestResourceGenerator
{
   /**
    * A readable description for this strategy
    */
   String getName();

   /**
    * A human-readable description for this strategy
    */
   String getDescription();

   /**
    * Generate a REST resource based on a context
    */
   List<JavaClass> generateFrom(RestGenerationContext context) throws Exception;

}
