package net.nonemc.leaf.libs.neuralnetwork.json

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.nonemc.leaf.libs.neuralnetwork.configs.LayerConfig
import net.nonemc.leaf.libs.neuralnetwork.Layer
import net.nonemc.leaf.libs.neuralnetwork.NeuralNetwork
import net.nonemc.leaf.libs.neuralnetwork.getActivationDerivativeFunction
import net.nonemc.leaf.libs.neuralnetwork.getActivationFunction
import java.io.File

fun loadNetwork(file: File): NeuralNetwork {
    val gson = Gson()
    val jsonText = file.readText()
    val listType = object : TypeToken<List<LayerConfig>>() {}.type
    val layerConfigs: List<LayerConfig> = gson.fromJson(jsonText, listType)
    val layers = layerConfigs.map { config ->
        Layer(
            config.inputSize,
            config.outputSize,
            getActivationFunction(config.activation),
            getActivationDerivativeFunction(config.activationDerivative)
        )
    }
    return NeuralNetwork(layers)
}