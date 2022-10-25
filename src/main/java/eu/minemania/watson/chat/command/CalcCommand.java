package eu.minemania.watson.chat.command;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class CalcCommand extends WatsonCommandBase
{
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        dispatcher.register(literal("calc"));
        dispatcher.register(literal("calc")
                .then(argument("calculation", greedyString()).executes(CalcCommand::calc)));
    }

    private static int calc(CommandContext<FabricClientCommandSource> context)
    {
        String commandLine = getString(context, "calculation");
        StreamTokenizer tokenizer = makeTokenizer(commandLine);
        try
        {
            localOutputT(context.getSource(), "watson.message.calc.calculation", commandLine, calculation(tokenizer));
        }
        catch (Exception e)
        {
            localErrorT(context.getSource(), "watson.error.calc.calculation");
        }
        return 1;
    }

    private static StreamTokenizer makeTokenizer(String args)
    {
        // Currently not supporting variables, but define words for future use.
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(args));
        tokenizer.slashSlashComments(false);
        tokenizer.slashStarComments(false);
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('_', '_');
        // These need to be defined as ordinary or they will be parsed as comment
        // introducers. >.<
        tokenizer.ordinaryChar('*');
        tokenizer.ordinaryChar('/');
        return tokenizer;
    }

    private static double calculation(StreamTokenizer tokenizer) throws IOException
    {
        double result = expr(tokenizer);
        if (tokenizer.nextToken() != StreamTokenizer.TT_EOF)
        {
            throw new IOException();
        }
        return result;
    }

    private static double expr(StreamTokenizer tokenizer) throws IOException
    {
        double result = term(tokenizer);
        for (; ; )
        {
            int token = tokenizer.nextToken();
            if (token == '+')
            {
                result += term(tokenizer);
            }
            else if (token == '-')
            {
                result -= term(tokenizer);
            }
            else
            {
                break;
            }
        } // for
        tokenizer.pushBack();
        return result;
    }

    private static double term(StreamTokenizer tokenizer) throws IOException
    {
        double result = factor(tokenizer);
        for (; ; )
        {
            int token = tokenizer.nextToken();
            if (token == '*')
            {
                result *= factor(tokenizer);
            }
            else if (token == '/')
            {
                result /= factor(tokenizer);
            }
            else
            {
                break;
            }
        } // for
        tokenizer.pushBack();
        return result;
    }

    private static double factor(StreamTokenizer tokenizer) throws IOException
    {
        int token = tokenizer.nextToken();
        if (token == StreamTokenizer.TT_NUMBER)
        {
            return tokenizer.nval;
        }
        else if (token == '(')
        {
            double result = expr(tokenizer);
            if (tokenizer.nextToken() != ')')
            {
                throw new IOException();
            }
            return result;
        }
        else if (token == '-')
        {
            return -factor(tokenizer);
        }
        else
        {
            throw new IOException();
        }
    }
}