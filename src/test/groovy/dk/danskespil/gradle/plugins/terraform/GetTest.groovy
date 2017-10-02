package dk.danskespil.gradle.plugins.terraform

import dk.danskespil.gradle.plugins.helpers.DSSpecification
import org.gradle.testkit.runner.TaskOutcome

class GetTest extends DSSpecification {
    def "when no modules are present, task is called"() {
        given:
        buildFile << """
          plugins { 
              id 'dk.danskespil.gradle.plugins.terraform'
          }
          
          task cut(type: dk.danskespil.gradle.plugins.terraform.Get)
        """

        when:
        def result = buildWithTasks('cut')

        then:
        result.output.contains('terraform get')
    }

    def "only when modules are removed, task is executed"() {
        given:
        buildFile << """
          plugins { 
              id 'dk.danskespil.gradle.plugins.terraform'
          }
          
          task cut(type: dk.danskespil.gradle.plugins.terraform.Get) {
            doLast {
              // Since terraform is not executed during test, I am faking creation of the outputfile
              //project.mkdir('.terraform/modules')
              project.file('.terraform/').mkdir()
              project.file('.terraform/modules/').mkdir()
              project.file('.terraform/modules/a-module').createNewFile()
            }
          }
        """

        when:
        def build1 = buildWithTasks('cut')

        def build2 = buildWithTasks('cut')
        new File(testProjectDir.root.getAbsolutePath() + "/.terraform/modules").deleteDir()

        def build3 = buildWithTasks('cut')

        then:
        build1.output.contains('terraform get')
        build1.task(':cut').outcome == TaskOutcome.SUCCESS
        build2.task(':cut').outcome == TaskOutcome.UP_TO_DATE
        build3.task(':cut').outcome == TaskOutcome.SUCCESS
    }
}