package eu.minemania.watson.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import eu.minemania.watson.interfaces.ICommandRemover;

@Mixin(CommandNode.class)
public abstract class MixinCommandNode<S> implements Comparable<CommandNode<S>>, ICommandRemover {
	@Shadow(remap = false)
	private Map<String, CommandNode<S>> children;
	@Shadow(remap = false)
	private Map<String, LiteralCommandNode<S>> literals;
	@Shadow(remap = false)
	private Map<String, ArgumentCommandNode<S, ?>> arguments;

	@SuppressWarnings("unchecked")
	public void removeChild(String command) {
		final CommandNode<S> child = children.get(command);
		if(child != null) {
			if(child instanceof LiteralCommandNode) {
				literals.remove(child.getName(), (LiteralCommandNode<S>) child);
			} else if(child instanceof ArgumentCommandNode) {
				arguments.remove(child.getName(), (ArgumentCommandNode<S, ?>) child);
			}
			children.remove(child.getName(), child);
		}
	}
}
