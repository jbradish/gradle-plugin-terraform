package dk.danskespil.gradle.plugins.terraform

import dk.danskespil.gradle.plugins.helpers.dscommandlineexecutor.DSCommandLineTestExecutor
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*
import org.gradle.process.ExecSpec

class Plan extends DefaultTask {
    @InputFiles
    FileCollection terraformFiles = project.fileTree('.').include('*.tf')
    @InputFiles
    FileCollection templateFiles = project.fileTree('.').include('*.tpl')
    // -out=path           Write a plan file to the given path. This can be used as input to the "apply" command.
    @Optional
    @OutputFile
    File out
    @Optional
    @OutputFile
    File outAsText
    @Internal
    CommandLine commandLine = new CommandLine()

    @TaskAction
    action() {
        commandLine.addToEnd('terraform')
        commandLine.addToEnd('plan')
        if (out) {
            commandLine.addToEnd("out=${out.name}")
        }
        //DSCommandLineExecutorFactory.createExecutor(project).execute("terraform plan ${outAsParam()}")
        new DSCommandLineTestExecutor(project).executeExecSpec(this, { ExecSpec e ->

            e.commandLine this.commandLine
        })
    }
}