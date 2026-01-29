package org.example

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.mcp.McpClient
import org.example.mcp.McpConfig

fun main() = runBlocking {
    println("=== MCP Client Example ===\n")

    // Configuration for Context7 MCP server
    val config = McpConfig(
        url = "https://mcp.context7.com/mcp",
        headers = mapOf(
            "CONTEXT7_API_KEY" to "YOUR_API_KEY",
        )
    )

    val client = McpClient(config, debug = true)

    try {
        // Initialize connection
        println("Connecting to MCP server...")
        val initResult = client.initialize()
        println("Server capabilities: ${initResult.capabilities}\n")

        // Example 1: List available tools
        println("--- Listing Tools ---")
        val tools = client.listTools()
        tools.tools.forEach { tool ->
            println("Tool: ${tool.name}")
            println("  Description: ${tool.description ?: "N/A"}")
            println("  Input Schema: ${tool.inputSchema}")
            println()
        }

        // Example 2: List available resources
        println("--- Listing Resources ---")
        try {
            val resources = client.listResources()
            resources.resources.forEach { resource ->
                println("Resource: ${resource.name}")
                println("  URI: ${resource.uri}")
                println("  Description: ${resource.description ?: "N/A"}")
                println()
            }
        } catch (e: Exception) {
            println("Resources not available: ${e.message}\n")
        }

        // Example 3: List available prompts
        println("--- Listing Prompts ---")
        try {
            val prompts = client.listPrompts()
            prompts.prompts.forEach { prompt ->
                println("Prompt: ${prompt.name}")
                println("  Description: ${prompt.description ?: "N/A"}")
                prompt.arguments?.forEach { arg ->
                    println("  Argument: ${arg.name} (required: ${arg.required ?: false})")
                }
                println()
            }
        } catch (e: Exception) {
            println("Prompts not available: ${e.message}\n")
        }

        // Example 4: Call a tool (if available)
        if (tools.tools.isNotEmpty()) {
            println("--- Calling Tool Example ---")
            val firstTool = tools.tools.first()
            println("Calling tool: ${firstTool.name}")
            
            try {
                // Example with Context7's resolve-library-id tool
                if (firstTool.name == "resolve-library-id") {
                    val arguments = buildJsonObject {
                        put("libraryName", "react")
                        put("query", "How to use React hooks?")
                    }
                    val result = client.callTool(firstTool.name, arguments)
                    println("Tool Result:")
                    result.content.forEach { content ->
                        println("  Type: ${content.type}")
                        println("  Text: ${content.text ?: "N/A"}")
                    }
                }
            } catch (e: Exception) {
                println("Tool call failed: ${e.message}")
            }
        }

    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    } finally {
        // Clean up
        client.close()
        println("\nConnection closed.")
    }
}