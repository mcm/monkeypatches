package io.mcmaster.monkeypatches.mixin.rhino.PR57;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import dev.latvian.mods.rhino.classfile.ClassFileWriter;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;

/**
 * Mixin for ClassFileWriter to fix sizeOfParameters method to properly parse
 * object signatures in arrays.
 * 
 * This fix addresses the issue where the 'L' case (object signatures) in the
 * sizeOfParameters method
 * did not properly handle object types within array signatures, causing parsing
 * failures.
 * 
 * Related to Rhino PR #57
 * Based on commit: https://github.com/KubeJS-Mods/Rhino/pull/57/files
 * 
 * The fix ensures that when encountering an 'L' character (indicating an object
 * type),
 * the parser correctly finds the terminating semicolon and validates the
 * signature structure.
 */
@Restriction(require = @Condition(value = "rhino", versionPredicates = "<2101.2.7-build.77"))
@Mixin(value = ClassFileWriter.class, remap = false)
public class ClassFileWriterMixin {

    /**
     * Overwrites the sizeOfParameters method to include the object signature
     * parsing fix.
     * 
     * This fix is always applied when the Rhino mod is present, as mixins occur too
     * early
     * in the loading process to be gated by configuration.
     * 
     * @author MonkeyPatches
     * @reason Fix object signature parsing in arrays for Rhino PR #57
     */
    @Overwrite(remap = false)
    private static int sizeOfParameters(String pString) {
        int rightParenthesis = pString.indexOf(')');
        if (rightParenthesis < 0) {
            return -1;
        }

        boolean ok = true;
        int index = 1;
        int count = 0;
        stringLoop: while (index < rightParenthesis) {
            switch (pString.charAt(index)) {
                case 'J':
                case 'D':
                    // fall through
                case 'B':
                case 'S':
                case 'C':
                case 'I':
                case 'Z':
                case 'F':
                    ++count;
                    ++index;
                    continue;
                case '[':
                    ++index;
                    continue;
                case 'L': {
                    // PR #57 fix: properly handle object signatures
                    ++count;
                    ++index;
                    int semicolon = pString.indexOf(';', index);
                    if (!(index + 1 <= semicolon && semicolon < rightParenthesis)) {
                        ok = false;
                        break stringLoop;
                    }
                    index = semicolon + 1;
                    continue;
                }
                default:
                    ok = false;
                    break stringLoop;
            }
        }

        return ok ? count : -1;
    }
}
