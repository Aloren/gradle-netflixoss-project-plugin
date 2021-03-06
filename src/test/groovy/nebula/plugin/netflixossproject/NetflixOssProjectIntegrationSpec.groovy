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

import nebula.plugin.responsible.NebulaIntegTestPlugin
import nebula.test.IntegrationSpec
import org.ajoberstar.grgit.Grgit
import org.gradle.api.plugins.JavaPlugin

class NetflixOssProjectIntegrationSpec extends IntegrationSpec {
    Grgit grgit;

    def setup() {
        grgit = Grgit.init(dir: projectDir)

        buildFile << """
            group = 'test.nebula'
            ${applyPlugin(NetflixOssProjectPlugin)}
            ${applyPlugin(JavaPlugin)}
            repositories {
                jcenter()
            }
            dependencies {
                testImplementation 'junit:junit:4.12'
            }
            """.stripIndent()

        grgit.add(patterns: ['build.gradle'])
        grgit.commit(message: 'Setup')
    }

    def 'run build'() {
        when:
        runTasksSuccessfully('build')

        then:
        noExceptionThrown()
    }

    def 'verify manifest created' () {
        def result = '''\
            test.nebula:verify-manifest-created:0.1.0-SNAPSHOT
            '''.stripIndent()

        when:
        runTasksSuccessfully('build')

        then:
        new File(projectDir, 'build/netflixoss/netflixoss.txt').text == result
    }

    def 'works with facet plugins'() {
        facetAdditionalSetup()

        when:
        runTasksSuccessfully('build')

        then:
        noExceptionThrown()
        new File(projectDir, "build/reports/tests/test/index.html").exists()
        new File(projectDir, "build/reports/integTest/index.html").exists()
    }

    def 'writes licenses to all files'() {
        facetAdditionalSetup()

        String headerContains = "Copyright ${Calendar.getInstance().get(Calendar.YEAR)} Netflix, Inc."

        when:
        runTasksSuccessfully('tasks', '--all', 'licenseFormat')

        then:
        new File(projectDir, 'src/main/java/test/nebula/netflixoss/HelloWorld.java').text.contains(headerContains)
        new File(projectDir, 'src/test/java/test/nebula/netflixoss/HelloWorldTest.java').text.contains(headerContains)
        new File(projectDir, 'src/integTest/java/test/nebula/netflixoss/HelloWorldTest.java').text.contains(headerContains)
    }

    private void facetAdditionalSetup() {
        buildFile << """\
            ${applyPlugin(NebulaIntegTestPlugin)}
            """.stripIndent()

        writeHelloWorld('test.nebula.netflixoss')
        writeTest('src/test/java/', 'test.nebula.netflixoss', false)
        writeTest('src/integTest/java/', 'test.nebula.netflixoss', false)
    }
}
