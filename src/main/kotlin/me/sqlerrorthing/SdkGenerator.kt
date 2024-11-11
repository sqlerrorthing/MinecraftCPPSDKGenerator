package me.sqlerrorthing

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.sqlerrorthing.generator.impl.CPPGenerator
import me.sqlerrorthing.parser.impl.TinyMappingParser
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import picocli.CommandLine
import picocli.CommandLine.Command
import java.io.File
import java.util.concurrent.Callable
import kotlin.io.path.div

/**
 * Represents the configuration for the SDK generator.
 *
 * @property verbose Indicates whether verbose output is enabled. Default is false.
 * @property generateJson Convert output sdk to .json
 * @property mappings The file containing the .yarn mappings used to generate C++ code.
 * @property outFolder The output folder where the generated Minecraft SDK will be saved. Default is a "sdk" folder in the current directory.
 */
data class Config(
    val verbose: Boolean = false,
    val generateJson: Boolean = false,
    val mappings: File,
    val outFolder: File,
)

/**
 * Represents the command-line interface configuration for the SDK generator.
 *
 * This class implements [Callable] and is responsible for parsing command-line arguments
 * and creating a [Config] object based on those arguments.
 */
@Command(
    name = "generator",
    helpCommand = true,
    mixinStandardHelpOptions = true,
    version = ["1.0.0"],
    description = ["Generates c++ SDK code for minecraft using tiny mappings. to further simplify the development of the chat"],
)
class CLIConfig {
    /**
     * Indicates whether verbose output is enabled.
     */
    @CommandLine.Option(names = ["-v", "--verbose"])
    var verbose: Boolean = false

    /**
     * Convert output sdk to .json
     */
    @CommandLine.Option(names = ["-j", "--json"])
    var generateJson: Boolean = false

    /**
     * The output folder where the generated Minecraft SDK will be saved.
     */
    @CommandLine.Option(
        names = ["-o", "--output"],
        paramLabel = "OUTPUT",
        description = ["the output folder with generated minecraft sdk"],
        required = false,
    )
    var outFolder: File = File("sdk-out")

    /**
     * The file containing the .tiny mappings used to generate C++ code.
     */
    @CommandLine.Parameters(paramLabel = "MAPPING", description = ["path to the .tiny mapping itself to generate c++ code"])
    lateinit var mappings: File

    /**
     * Creates and returns a [Config] object based on the parsed command-line arguments.
     *
     * @return A [Config] object containing the parsed configuration.
     * @throws IllegalStateException if the provided mappings file is not a valid file.
     */
    fun asConfigFile(): Config {
        if (!mappings.isFile) {
            throw IllegalStateException("Provided mappings is not a file")
        }

        outFolder.mkdirs()

        return Config(
            verbose = verbose,
            generateJson = generateJson,
            mappings = mappings,
            outFolder = outFolder,
        )
    }
}

fun main(args: Array<String>) {
    val config = CLIConfig().also { CommandLine(it).parseArgs(*args) }.asConfigFile()

    Configurator.setRootLevel(if (config.verbose) Level.DEBUG else Level.INFO)

    val parsed = TinyMappingParser.parse(config)

    CPPGenerator.generate(config, parsed)

    if (config.generateJson) {
        val out = config.outFolder.toPath() / "mapping.json"
        jacksonObjectMapper().let {
            it.enable(SerializationFeature.INDENT_OUTPUT)

            it.writerWithDefaultPrettyPrinter()
            .writeValue(out.toFile(), parsed)
        }
    }

    println("Generated SDK in ${config.outFolder}")
    for(clazz in parsed) {
        println("#include <${clazz.name.normalName}.hpp>")
    }
}
