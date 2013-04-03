/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh.commands;

import org.jboss.aesh.console.Console;
import org.jboss.aesh.extensions.harlem.Harlem;
import org.jboss.forge.aesh.ShellContext;
import org.jboss.forge.ui.UICommand;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class HarlemCommand implements UICommand {
    @Override
    public UICommandMetadata getMetadata() {
        return Metadata.forCommand(getClass())
                .name("harlem")
                .description("do you want some harlem?");
    }

    @Override
    public boolean isEnabled(UIContext context) {
        return (context instanceof ShellContext);
    }

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
    }

    @Override
    public void validate(UIValidationContext validator) {
    }

    @Override
    public Result execute(UIContext context) throws Exception {
        if(context instanceof ShellContext) {
            Console console = ((ShellContext) context).getShell().getConsole();
            Harlem harlem = new Harlem(console);
            harlem.attach(((ShellContext) context).getConsoleOutput());
        }
        return Results.success();
    }
}
