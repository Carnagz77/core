/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.templates;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * {@link TemplateProcessor} implementation
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class TemplateProcessorImpl implements TemplateProcessor
{
   private final TemplateGenerator generator;
   private Template template;
   private Resource resource;

   TemplateProcessorImpl(TemplateGenerator generator, Template template)
   {
      super();
      this.generator = generator;
      this.template = template;
   }

   /**
    * @deprecated Deprecated after Forge 2.1.1. Use the {@link Template} based constructor instead.
    */
   TemplateProcessorImpl(TemplateGenerator generator, Resource resource)
   {
      super();
      this.generator = generator;
      this.resource = resource;
   }

   @Override
   public String process(Object dataModel) throws IOException
   {
      StringWriter writer = new StringWriter();
      process(dataModel, writer);
      return writer.toString();
   }

   @Override
   public void process(Object dataModel, Writer output) throws IOException
   {
      if (template != null)
      {
         generator.process(dataModel, template, output);
      }
      else if (resource != null)
      {
         generator.process(dataModel, resource, output);
      }
   }
}