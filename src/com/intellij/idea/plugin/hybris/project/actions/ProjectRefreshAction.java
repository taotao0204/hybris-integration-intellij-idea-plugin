/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.intellij.idea.plugin.hybris.project.actions;

import com.intellij.ide.actions.ImportModuleAction;
import com.intellij.ide.util.newProjectWizard.AddModuleWizard;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.idea.plugin.hybris.common.services.CommonIdeaService;
import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils;
import com.intellij.idea.plugin.hybris.common.utils.HybrisIcons;
import com.intellij.idea.plugin.hybris.project.HybrisProjectImportProvider;
import com.intellij.idea.plugin.hybris.project.wizard.HybrisWorkspaceRootStep;
import com.intellij.idea.plugin.hybris.project.wizard.SelectHybrisImportedProjectsStep;
import com.intellij.idea.plugin.hybris.settings.HybrisProjectSettings;
import com.intellij.idea.plugin.hybris.settings.HybrisProjectSettingsComponent;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.CompilerProjectExtension;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.projectImport.ProjectImportProvider;

import java.util.List;


/**
 * Created by Martin Zdarsky-Jones on 8/2/17.
 */
public class ProjectRefreshAction extends ImportModuleAction {
    final CommonIdeaService commonIdeaService = ServiceManager.getService(CommonIdeaService.class);

    @Override
    public void update(AnActionEvent event) {
        final Project project = event.getData(PlatformDataKeys.PROJECT);
        final Presentation presentation = event.getPresentation();
        presentation.setIcon(HybrisIcons.HYBRIS_ICON);
        final boolean visible = commonIdeaService.isHybrisProject(project);
        presentation.setVisible(visible);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        try {
            doReImport(getEventProject(anActionEvent));
        } catch (ConfigurationException e) {
            Messages.showErrorDialog(anActionEvent.getProject(),
                                     e.getMessage(),
                                     HybrisI18NBundleUtils.message("hybris.project.import.error.unable.to.proceed")
            );
        }
    }

    public List<Module> doReImport(final Project project) throws ConfigurationException {
        final AddModuleWizard wizard = getWizard(project);
        return createFromWizard(null, wizard);
    }

    @Override
    public boolean displayTextInToolbar() {
        return true;
    }

    private AddModuleWizard getWizard(final Project project) throws ConfigurationException {
        final HybrisProjectImportProvider provider = getHybrisProjectImportProvider();
        final String basePath = project.getBasePath();
        final String projectName = project.getName();
        final Sdk jdk = ProjectRootManager.getInstance(project).getProjectSdk();
        final String compilerOutputUrl = CompilerProjectExtension.getInstance(project).getCompilerOutputUrl();
        final HybrisProjectSettings settings = HybrisProjectSettingsComponent.getInstance(project).getState();

        ProjectManagerEx.getInstanceEx().closeAndDispose(project);

        final AddModuleWizard wizard = new AddModuleWizard(null, basePath, provider) {
            protected void init() {
              // non GUI mode
            }
        };
        final WizardContext wizardContext = wizard.getWizardContext();
        wizardContext.setProjectJdk(jdk);
        wizardContext.setProjectName(projectName);
        wizardContext.setCompilerOutputDirectory(compilerOutputUrl);
        new HybrisWorkspaceRootStep(wizardContext, true).nonGuiModeImport(settings);
        new SelectHybrisImportedProjectsStep(wizardContext, true).nonGuiModeImport();
        return wizard;
    }

    private HybrisProjectImportProvider getHybrisProjectImportProvider() {
        for (ProjectImportProvider provider : Extensions.getExtensions(ProjectImportProvider.PROJECT_IMPORT_PROVIDER)) {
            if (provider instanceof HybrisProjectImportProvider) {
                return (HybrisProjectImportProvider)provider;
            }
        }
        return null;
    }
}