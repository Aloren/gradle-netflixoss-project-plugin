/*
 * Copyright 2014-2019 Netflix, Inc.
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

import nebula.plugin.contacts.ContactsPlugin
import nebula.plugin.dependencylock.DependencyLockPlugin
import nebula.plugin.info.InfoPlugin
import nebula.plugin.netflixossproject.publishing.PublishingPlugin
import nebula.plugin.publishing.maven.MavenPublishPlugin
import nebula.plugin.publishing.publications.JavadocJarPlugin
import nebula.plugin.publishing.publications.SourceJarPlugin
import nebula.test.ProjectSpec
import org.ajoberstar.grgit.Grgit
import org.gradle.api.Project
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.idea.IdeaPlugin

class NetflixOssMultiProjectSpec extends ProjectSpec {
    Project sub1
    Project sub2

    def setup() {
        def git = Grgit.init(dir: projectDir)
        sub1 = addSubproject('sub1')
        sub2 = addSubproject('sub2')
        git.commit(message: 'initial')
    }

    def
    'multi-projects have correct plugins added to root project'() {
        [project, sub1, sub2].each { it.plugins.apply NetflixOssProjectPlugin }

        expect:
        project.plugins.findPlugin(plugin) != null

        where:
        plugin | _
        PublishingPlugin | _
        InfoPlugin | _
        IdeaPlugin | _
        EclipsePlugin | _
    }

    def 'multi-projects have correct plugins added to subproject'() {
        [project, sub1, sub2].each { it.plugins.apply NetflixOssProjectPlugin }

        expect:
        sub1.plugins.findPlugin(plugin) != null

        where:
        plugin | _
        PublishingPlugin | _
        InfoPlugin | _
        IdeaPlugin | _
        EclipsePlugin | _
        DependencyLockPlugin | _
        JavadocJarPlugin | _
        SourceJarPlugin | _
        MavenPublishPlugin | _
        ContactsPlugin | _
    }
}
