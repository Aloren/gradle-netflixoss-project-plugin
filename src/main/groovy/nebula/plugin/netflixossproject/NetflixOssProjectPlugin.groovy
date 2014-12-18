/*
 * Copyright 2014 Netflix, Inc.
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
package nebula.plugin.netflixossproject

import nebula.core.GradleHelper
import nebula.core.ProjectType
import nebula.plugin.contacts.ContactsPlugin
import nebula.plugin.dependencylock.DependencyLockPlugin
import nebula.plugin.info.InfoPlugin
import nebula.plugin.netflixossproject.license.OssLicensePlugin
import nebula.plugin.netflixossproject.publishing.PublishingPlugin
import nebula.plugin.publishing.NebulaJavadocJarPlugin
import nebula.plugin.publishing.NebulaPublishingPlugin
import nebula.plugin.publishing.NebulaSourceJarPlugin
import nebula.plugin.release.ReleasePlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin

class NetflixOssProjectPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def gradleHelper = new GradleHelper(project)
        gradleHelper.addDefaultGroup('com.netflix')
        project.description = project.name
        ProjectType type = new ProjectType(project)

        if (type.isLeafProject || type.isRootProject) {
            project.plugins.apply PublishingPlugin
            project.plugins.apply ReleasePlugin
            project.plugins.apply DependencyLockPlugin
        }

        if (type.isRootProject) {
            project.tasks.release.dependsOn project.tasks.bintrayUpload
        }

        if (type.isLeafProject) {
            project.plugins.apply NebulaPublishingPlugin
            project.plugins.apply NebulaJavadocJarPlugin
            project.plugins.apply NebulaSourceJarPlugin
            project.plugins.apply ContactsPlugin

            project.plugins.withType(JavaPlugin) { JavaPlugin javaPlugin ->
                JavaPluginConvention convention = project.convention.getPlugin(JavaPluginConvention)
                convention.sourceCompatibility = JavaVersion.VERSION_1_7
            }

            project.tasks.withType(Javadoc) {
                options {
                    if (JavaVersion.current().isJava8Compatible()) {
                        options.addStringOption('Xdoclint:none', '-quiet')
                    }
                }
            }
        }

        project.plugins.apply OssLicensePlugin
        project.plugins.apply InfoPlugin
        project.plugins.apply IdeaPlugin
        project.plugins.apply EclipsePlugin
    }
}