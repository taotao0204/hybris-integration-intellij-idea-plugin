/*
 * Copyright 2015 Alexander Bartash <AlexanderBartash@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.idea.plugin.hybris.project.settings;

import com.intellij.idea.plugin.hybris.project.exceptions.HybrisConfigurationException;
import com.intellij.idea.plugin.hybris.utils.HybrisConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created 12:46 PM 20 June 2015.
 *
 * @author Alexander Bartash <AlexanderBartash@gmail.com>
 */
public abstract class AbstractHybrisModuleDescriptor implements HybrisModuleDescriptor {

    @NotNull
    protected final File moduleRootDirectory;
    @NotNull
    protected final HybrisProjectDescriptor rootProjectDescriptor;
    @NotNull
    protected final Set<HybrisModuleDescriptor> dependenciesTree = new HashSet<HybrisModuleDescriptor>(0);
    @NotNull
    protected Set<String> springFileSet = new HashSet<String>();

    public AbstractHybrisModuleDescriptor(@NotNull final File moduleRootDirectory,
                                          @NotNull final HybrisProjectDescriptor rootProjectDescriptor
    ) throws HybrisConfigurationException {
        Validate.notNull(moduleRootDirectory);
        Validate.notNull(rootProjectDescriptor);

        this.moduleRootDirectory = moduleRootDirectory;
        this.rootProjectDescriptor = rootProjectDescriptor;

        if (!this.moduleRootDirectory.isDirectory()) {
            throw new HybrisConfigurationException("Can not find module directory using path: " + moduleRootDirectory);
        }
    }

    @Override
    public int compareTo(@NotNull final HybrisModuleDescriptor o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }

    @NotNull
    @Override
    public File getRootDirectory() {
        return moduleRootDirectory;
    }

    @NotNull
    @Override
    public String getRelativePath() {
        final File rootDirectory = this.getRootProjectDescriptor().getRootDirectory();
        if (null != rootDirectory && this.getRootDirectory().getPath().startsWith(rootDirectory.getPath())) {
            return this.getRootDirectory().getPath().substring(rootDirectory.getPath().length());
        }

        return this.getRootDirectory().getPath();
    }

    @NotNull
    @Override
    public HybrisProjectDescriptor getRootProjectDescriptor() {
        return rootProjectDescriptor;
    }

    @NotNull
    @Override
    public File getIdeaModuleFile() {
        if (null != this.rootProjectDescriptor.getModulesFilesDirectory()) {
            return new File(
                this.rootProjectDescriptor.getModulesFilesDirectory(),
                this.getName() + HybrisConstants.NEW_IDEA_MODULE_FILE_EXTENSION
            );
        }

        return new File(this.moduleRootDirectory, this.getName() + HybrisConstants.NEW_IDEA_MODULE_FILE_EXTENSION);
    }

    @NotNull
    @Override
    public Set<HybrisModuleDescriptor> getDependenciesTree() {
        return dependenciesTree;
    }

    @Override
    public void setDependenciesTree(@NotNull final Set<HybrisModuleDescriptor> moduleDescriptors) {
        Validate.notNull(moduleDescriptors);

        this.dependenciesTree.clear();
        this.dependenciesTree.addAll(moduleDescriptors);
    }

    @NotNull
    @Override
    public Set<HybrisModuleDescriptor> getDependenciesPlainList() {
        return Collections.unmodifiableSet(this.recursivelyCollectDependenciesPlainSet(
            this, new TreeSet<HybrisModuleDescriptor>()
        ));
    }

    @NotNull
    protected Set<HybrisModuleDescriptor> recursivelyCollectDependenciesPlainSet(
        @NotNull final HybrisModuleDescriptor descriptor,
        @NotNull final Set<HybrisModuleDescriptor> dependenciesSet
    ) {
        Validate.notNull(descriptor);
        Validate.notNull(dependenciesSet);

        if (CollectionUtils.isEmpty(descriptor.getDependenciesTree())) {
            return dependenciesSet;
        }

        for (HybrisModuleDescriptor moduleDescriptor : descriptor.getDependenciesTree()) {
            if (dependenciesSet.contains(moduleDescriptor)) {
                continue;
            }

            dependenciesSet.add(moduleDescriptor);
            dependenciesSet.addAll(recursivelyCollectDependenciesPlainSet(moduleDescriptor, dependenciesSet));
        }

        return dependenciesSet;
    }

    @Nullable
    @Override
    public Set<String> getSpringFileSet() {
        return springFileSet;
    }

    @Override
    public void addSpringFile(@NotNull final String springFile) {
        this.springFileSet.add(springFile);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(this.getName())
            .append(moduleRootDirectory)
            .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (null == obj || getClass() != obj.getClass()) {
            return false;
        }

        final AbstractHybrisModuleDescriptor other = (AbstractHybrisModuleDescriptor) obj;

        return new org.apache.commons.lang3.builder.EqualsBuilder()
            .append(this.getName(), other.getName())
            .append(moduleRootDirectory, other.moduleRootDirectory)
            .isEquals();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ConfigHybrisModuleDescriptor{");
        sb.append("name='").append(this.getName()).append('\'');
        sb.append(", moduleRootDirectory=").append(moduleRootDirectory);
        sb.append(", moduleFile=").append(this.getIdeaModuleFile());
        sb.append('}');
        return sb.toString();
    }
}
