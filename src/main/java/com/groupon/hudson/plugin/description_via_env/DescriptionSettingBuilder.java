package com.groupon.hudson.plugin.description_via_env;

import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;


import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.tasks.Builder;
import hudson.util.LogTaskListener;

import java.io.PrintStream;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Sets the build's description to be a particular environment variable
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link ExporterBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(Build, Launcher, BuildListener)} method
 * will be invoked. 
 *
 * @author Richie Vos
 */
public class DescriptionSettingBuilder extends Builder {
    
    @DataBoundConstructor
    public DescriptionSettingBuilder() {
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        PrintStream logger = listener.getLogger();
        try {
            EnvVars env = build.getEnvironment(new LogTaskListener(Logger.getLogger(this.getClass().getName()), Level.INFO));
            String buildParam = "branch";//env.get("DESCRIPTION_PARAM");
            if (buildParam != null) {
                String description = env.get(buildParam);
                if (description != null && description != "") {
                    logger.println("Setting build description via " + buildParam + " to " + description);
                    build.setDescription(description);
                } else {
                    logger.println("Could not find build description via " + buildParam);
                }
            }
        } catch (Exception e) {
            logger.println("Error performing description setting " + e.getMessage());
        }
        return true;
    }

    // overrided for better type safety.
    // if your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link ExporterBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>views/hudson/plugins/exporter/ExporterBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // this marker indicates Hudson that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Set description via env";
        }
    }
}

