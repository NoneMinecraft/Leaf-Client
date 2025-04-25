package net.nonemc.leaf.libs.neuralnetwork.configs

data class LayerConfig(
    val inputSize: Int,
    val outputSize: Int,
    val activation: String,
    val activationDerivative: String
)