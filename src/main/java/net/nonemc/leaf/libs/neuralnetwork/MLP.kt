package net.nonemc.leaf.libs.neuralnetwork


import com.google.gson.Gson
import net.nonemc.leaf.file.neuralNetworkModelDir
import net.nonemc.leaf.libs.neuralnetwork.configs.MLPConfig
import net.nonemc.leaf.libs.neuralnetwork.file.getList
import net.nonemc.leaf.libs.neuralnetwork.json.loadNetwork
import java.io.File
import kotlin.math.*
import kotlin.random.Random

val network: NeuralNetwork = loadNetwork(File(neuralNetworkModelDir,"mlp_network.json"))
val modelWeightFile = File(neuralNetworkModelDir,"mlp_train.txt")
val modelDataFile = File(neuralNetworkModelDir,"model-data.txt")
val modelConfigFile = File(neuralNetworkModelDir,"mlp_config.json")

val reverseNetwork: NeuralNetwork = loadNetwork(File(neuralNetworkModelDir,"mlp_reverse_network.json"))
val reverseModelWeightFile = File( neuralNetworkModelDir,"mlp_reverse_train.txt")
val reverseModelDataFile = File(neuralNetworkModelDir,"model_reverse-data.txt")
val reverseModelConfigFile = File(neuralNetworkModelDir,"mlp_reverse_config.json")

private fun sigmoid(x: Double): Double = 1.0 / (1.0 + exp(-x))
private fun sigmoidDerivative(x: Double): Double {
    val s = sigmoid(x)
    return s * (1 - s)
}

private fun tanhActivation(x: Double): Double = tanh(x)
private fun tanhDerivative(x: Double): Double {
    val t = tanh(x)
    return 1 - t * t
}

fun getActivationFunction(name: String): (Double) -> Double = when (name.lowercase()) {
    "tanh", "tanhactivation" -> ::tanhActivation
    "sigmoid" -> ::sigmoid
    else -> throw IllegalArgumentException("Unknown activation: $name")
}

fun getActivationDerivativeFunction(name: String): (Double) -> Double = when (name.lowercase()) {
    "tanh", "tanhderivative" -> ::tanhDerivative
    "sigmoid", "sigmoidderivative" -> ::sigmoidDerivative
    else -> throw IllegalArgumentException("Unknown derivative: $name")
}

class Layer(
    val inputSize: Int,
    val outputSize: Int,
    val activation: (Double) -> Double,
    val activationDerivative: (Double) -> Double
) {
    var weights: Array<DoubleArray> =
        Array(outputSize) { DoubleArray(inputSize) { Random.nextDouble(-1.0, 1.0) } }
    var biases: DoubleArray = DoubleArray(outputSize) { Random.nextDouble(-1.0, 1.0) }

    fun forward(input: DoubleArray): Pair<DoubleArray, DoubleArray> {
        val z = DoubleArray(outputSize)
        val a = DoubleArray(outputSize)
        for (i in 0 until outputSize) {
            var sum = biases[i]
            for (j in 0 until inputSize) {
                sum += weights[i][j] * input[j]
            }
            z[i] = sum
            a[i] = activation(sum)
        }
        return Pair(z, a)
    }
}

class NeuralNetwork(private val layers: List<Layer>) {
    fun forward(input: DoubleArray): List<Pair<DoubleArray, DoubleArray>> {
        val results = mutableListOf<Pair<DoubleArray, DoubleArray>>()
        var currentInput = input
        for (layer in layers) {
            val (z, a) = layer.forward(currentInput)
            results.add(Pair(z, a))
            currentInput = a
        }
        return results
    }
    fun predict(input: DoubleArray): DoubleArray {
        return forward(input).last().second
    }
    private fun trainSample(input: DoubleArray, target: DoubleArray, learningRate: Double) {
        val outputs = forward(input)
        val deltaList = MutableList(layers.size) { DoubleArray(0) }
        val last = layers.last()
        val (zLast, aLast) = outputs.last()
        val deltaLast = DoubleArray(last.outputSize) { i ->
            (aLast[i] - target[i]) * last.activationDerivative(zLast[i])
        }
        deltaList[deltaList.lastIndex] = deltaLast
        for (l in layers.size - 2 downTo 0) {
            val current = layers[l]
            val next = layers[l + 1]
            val (z, _) = outputs[l]
            val delta = DoubleArray(current.outputSize)
            for (i in 0 until current.outputSize) {
                var sum = 0.0
                for (j in 0 until next.outputSize) {
                    sum += next.weights[j][i] * deltaList[l + 1][j]
                }
                delta[i] = sum * current.activationDerivative(z[i])
            }
            deltaList[l] = delta
        }
        var inputVec = input
        for (l in layers.indices) {
            val layer = layers[l]
            val delta = deltaList[l]
            if (l > 0) inputVec = outputs[l - 1].second
            for (i in 0 until layer.outputSize) {
                layer.biases[i] -= learningRate * delta[i]
                for (j in 0 until layer.inputSize) {
                    layer.weights[i][j] -= learningRate * delta[i] * inputVec[j]
                }
            }
        }
    }
    fun train(inputs: List<DoubleArray>, targets: List<DoubleArray>, learningRate: Double) {
        for (i in inputs.indices) {
            trainSample(inputs[i], targets[i], learningRate)
        }
    }
    fun saveWeights(file: File) {
        file.bufferedWriter().use { writer ->
            for (layer in layers) {
                writer.write("weights\n")
                for (row in layer.weights) {
                    writer.write(row.joinToString(",") + "\n")
                }
                writer.write("biases\n")
                writer.write(layer.biases.joinToString(",") + "\n")
            }
        }
    }
    fun loadWeights(file: File) {
        val lines = file.readLines().iterator()
        for (layer in layers) {
            check(lines.next().trim() == "weights")
            for (i in 0 until layer.outputSize) {
                val parts = lines.next().split(",").map { it.toDouble() }
                layer.weights[i] = parts.toDoubleArray()
            }
            check(lines.next().trim() == "biases")
            val biases = lines.next().split(",").map { it.toDouble() }
            layer.biases = biases.toDoubleArray()
        }
    }
}

private fun learn(network: NeuralNetwork, data: List<Double>, epochs: Int, learningRate: Double) {
    val min = data.minOrNull() ?: -180.0
    val max = data.maxOrNull() ?: 180.0
    val range = max - min
    val normalized = data.map { (it - min) / range }
    val inputs = normalized.indices.map { i -> doubleArrayOf(i.toDouble() / (normalized.size - 1)) }
    val targets = normalized.map { doubleArrayOf(it) }

    for (epoch in 1..epochs) {
        network.train(inputs, targets, learningRate)
        if (epoch % 500 == 0) {
            val mse = inputs.indices.sumOf { i ->
                val output = network.predict(inputs[i])[0]
                val error = output - targets[i][0]
                error * error
            } / inputs.size
            println("Epoch $epoch, MSE: $mse")
        }
    }
}

private fun get(network: NeuralNetwork, start: Double, end: Double, size: Int): List<Double> {
    return List(size) { i ->
        val t = i.toDouble() / (size - 1)
        val norm = network.predict(doubleArrayOf(t))[0]
        start + (end - start) * norm
    }
}

fun getNetwork(current: Float, target: Float, point: Int): List<Float> {
    return get(network, current.toDouble(), target.toDouble(), point).map { it.toFloat() }
}
fun learnNetwork() {
    if (modelWeightFile.exists()) {
        network.loadWeights(modelWeightFile)
    } else {
        val jsonConfig = modelConfigFile.readText(Charsets.UTF_8)
        val config = Gson().fromJson(jsonConfig, MLPConfig::class.java)
        learn(network, getList(modelDataFile), config.iterations, config.learningRate)
        network.saveWeights(modelWeightFile)
    }
}


fun getReverseNetwork(current: Float, target: Float, point: Int): List<Float> {
    return get(reverseNetwork, current.toDouble(), target.toDouble(), point).map { it.toFloat() }
}
fun learnReverseNetwork() {
    if (reverseModelWeightFile.exists()) {
        reverseNetwork.loadWeights(reverseModelWeightFile)
    } else {
        val jsonConfig = reverseModelConfigFile.readText(Charsets.UTF_8)
        val config = Gson().fromJson(jsonConfig, MLPConfig::class.java)
        learn(reverseNetwork, getList(reverseModelDataFile), config.iterations, config.learningRate)
        reverseNetwork.saveWeights(reverseModelWeightFile)
    }
}