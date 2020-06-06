package com.elvaliev.k8s_aws_plugin.task

import com.elvaliev.k8s_aws_plugin.PluginConstant
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

open class DeployDefaultTask : DefaultTask() {

    enum class Client {
        kubectl, oc, sam
    }

    fun checkForClient(client: Client, commandArg: String = "--help") {
        val process = ProcessBuilder(createCommandLineArgs("$client $commandArg"))
                .directory(project.projectDir).start()

        val bufferedReader = BufferedReader(InputStreamReader(process.errorStream))
        process.waitFor(3, TimeUnit.SECONDS)
        while (bufferedReader.ready()) {
            process.destroy()
            println("${PluginConstant.ANSI_RED}${bufferedReader.readLine()}${PluginConstant.ANSI_RESET}")
            throw GradleException("Client $client are not recognized. Check that $client installed.")
        }

        process.destroy()
    }

    fun retrieveFile(filePath: String): String {
        if (!project.file(filePath).exists()) {
            if (project.file("build\\$filePath").exists()) {
                return "build/$filePath"
            }
            throw GradleException("File doesn't exist by provided path: $filePath")
        }
        return filePath
    }

    fun executeCommand(command: String, continueOnError: Boolean = false) {
        println(">> ${PluginConstant.ANSI_GREEN}$command${PluginConstant.ANSI_RESET}")
        try {
            project.exec {
                if (Os.isFamily(Os.FAMILY_WINDOWS))
                    commandLine("cmd", "/c", command)
                else
                    commandLine("sh", "-c", command)
            }
        } catch (e: Exception) {
            println(e)
            if (!continueOnError)
                throw GradleException("Error was occupied: $e")
        }
    }

    fun createCommandLineArgs(command: String): List<String> {
        val args = command.split(" ")

        if (Os.isFamily(Os.FAMILY_WINDOWS))
            return listOf("cmd", "/c") + args
        return listOf("sh", "-c") + args
    }

    fun parseValue(valueFromExtension: String?, valueFromCommandLine: String?, parameterName: String): String? {
        return when (valueFromExtension == null) {
            true -> when (valueFromCommandLine == null) {
                true -> throw GradleException("$parameterName was not defined")
                else -> valueFromCommandLine
            }
            else -> valueFromExtension
        }
    }
}