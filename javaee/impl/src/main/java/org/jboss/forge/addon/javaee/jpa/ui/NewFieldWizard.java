/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.jpa.FieldOperations;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;

public class NewFieldWizard extends AbstractJavaEECommand implements UIWizard
{
   @Inject
   @WithAttributes(label = "Entity", description = "The entity which the field will be created", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> entity;

   @Inject
   @WithAttributes(label = "Field Name", description = "The field name to be created in the target entity", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Type", description = "The type intended to be used for this field", type = InputType.JAVA_CLASS_PICKER, required = true, defaultValue = "String")
   private UIInput<String> typeName;

   @Inject
   @WithAttributes(label = "Relationship", description = "The type of the relationship", type = InputType.RADIO)
   private UISelectOne<RelationshipType> relationshipType;

   @Inject
   @WithAttributes(label = "Is LOB?", description = "If the relationship is a LOB, in this case, it will ignore the value in the Type field", defaultValue = "false")
   private UIInput<Boolean> lob;

   @Inject
   @WithAttributes(label = "Length", defaultValue = "255", description = "The column length. (Applies only if a string-valued column is used.)")
   private UIInput<Integer> length;

   @Inject
   @WithAttributes(label = "Temporal Type", defaultValue = "DATE", description = "Adds @Temporal only if field is java.util.Date or java.util.Calendar", type = InputType.RADIO, enabled = false)
   private UISelectOne<TemporalType> temporalType;

   @Inject
   private FieldOperations fieldOperations;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("JPA: New Field")
               .description("Create a new field")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "JPA"));
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      setupEntities(builder.getUIContext());
      setupRelationshipType();
      typeName.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String value)
         {
            String[] types = { "byte", "float", "char", "double", "int", "long", "short", "boolean", "String" };
            List<String> options = new ArrayList<String>();
            for (String type : types)
            {
               if (Strings.isNullOrEmpty(value) || type.startsWith(value))
               {
                  options.add(type);
               }
            }
            return options;
         }
      });
      lob.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return relationshipType.getValue() == RelationshipType.BASIC;
         }
      });
      typeName.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return !lob.getValue();
         }
      });
      length.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            return !lob.getValue();
         }
      });
      temporalType.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            String typeValue = typeName.getValue();
            return Date.class.getName().equals(typeValue) || Calendar.class.getName().equals(typeValue);
         }
      });
      builder.add(entity).add(named).add(typeName).add(temporalType).add(length).add(relationshipType).add(lob);
   }

   private void setupEntities(UIContext context)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();
      Project project = getSelectedProject(context);
      final List<JavaResource> entities = new ArrayList<JavaResource>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {

            @Override
            public void visit(VisitContext context, JavaResource resource)
            {
               try
               {
                  if (resource.getJavaSource().hasAnnotation(Entity.class))
                  {
                     entities.add(resource);
                  }
               }
               catch (FileNotFoundException e)
               {
                  // ignore
               }
            }
         });
      }
      entity.setValueChoices(entities);
      int idx = -1;
      if (!selection.isEmpty())
      {
         idx = entities.indexOf(selection.get());
      }
      if (idx == -1)
      {
         idx = entities.size() - 1;
      }
      if (idx != -1)
      {
         entity.setDefaultValue(entities.get(idx));
      }
   }

   private void setupRelationshipType()
   {
      relationshipType.setItemLabelConverter(new Converter<RelationshipType, String>()
      {
         @Override
         public String convert(RelationshipType source)
         {
            return (source == null) ? null : source.getDescription();
         }
      });
      relationshipType.setDefaultValue(RelationshipType.BASIC);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource = entity.getValue();
      String fieldNameStr = named.getValue();
      Field<JavaClass> field;
      JavaClass targetEntity = (JavaClass) javaResource.getJavaSource();
      RelationshipType value = relationshipType.getValue();
      if (value == RelationshipType.BASIC)
      {
         if (lob.getValue())
         {
            String fieldType = byte[].class.getName();
            field = fieldOperations.addFieldTo(targetEntity, fieldType, fieldNameStr, Lob.class.getName());
            field.addAnnotation(Column.class).setLiteralValue("length", String.valueOf(Integer.MAX_VALUE));
         }
         else
         {
            String fieldType = typeName.getValue();
            field = fieldOperations.addFieldTo(targetEntity, fieldType, fieldNameStr,
                     Column.class.getCanonicalName());
         }
         if (length.isEnabled() && length.getValue() != null && length.getValue().intValue() != 255)
         {
            field.getAnnotation(Column.class).setLiteralValue("length", String.valueOf(length.getValue()));
         }
         if (temporalType.isEnabled())
         {
            field.addAnnotation(Temporal.class).setEnumValue(temporalType.getValue());
         }
         Project selectedProject = getSelectedProject(context);
         if (selectedProject != null)
         {
            JavaSourceFacet facet = selectedProject.getFacet(JavaSourceFacet.class);
            facet.saveJavaSource(field.getOrigin());
         }
         context.getUIContext().setSelection(javaResource);
         return Results.success("Field " + named.getValue() + " created");
      }
      else
      {
         // Field creation will occur in NewFieldRelationshipWizardStep
         return Results.success();
      }
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      try
      {
         JavaResource javaResource = entity.getValue();
         if (javaResource != null)
         {
            JavaClass javaClass = (JavaClass) javaResource.getJavaSource();
            if (javaClass.hasField(named.getValue()))
            {
               validator.addValidationError(entity, "Field '" + named.getValue() + "' already exists");
            }
         }
      }
      catch (FileNotFoundException ffe)
      {
         validator.addValidationError(entity, "Entity could not be found");
      }

      if (length.isEnabled())
      {
         if (length.getValue() == null || length.getValue() <= 0)
         {
            validator.addValidationError(length, "Length should be a positive integer");
         }
      }
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      Map<Object, Object> attributeMap = context.getUIContext().getAttributeMap();
      attributeMap.put(JavaResource.class, entity.getValue());
      attributeMap.put("fieldName", named.getValue());
      attributeMap.put("fieldType", typeName.getValue());
      attributeMap.put(RelationshipType.class, relationshipType.getValue());
      if (relationshipType.getValue() == RelationshipType.BASIC)
      {
         return null;
      }
      else
      {
         return Results.navigateTo(NewFieldRelationshipWizardStep.class);
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }
}