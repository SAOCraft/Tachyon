package net.swordcraft.server.command

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.suggestion.Suggestion
import net.minestom.server.command.builder.suggestion.SuggestionCallback
import net.minestom.server.command.builder.suggestion.SuggestionEntry

class SuggestionListCallback<T>(private val list: List<T>) : SuggestionCallback {

    override fun apply(sender: CommandSender, context: CommandContext, suggestion: Suggestion) {
        list.forEach {
            suggestion.addEntry(SuggestionEntry(it.toString()))
        }
    }

}