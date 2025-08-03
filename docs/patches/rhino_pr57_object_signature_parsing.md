# Rhino PR #57

[Link to issue](https://github.com/KubeJS-Mods/Rhino/pull/57)<br>
[Link to fix](https://github.com/KubeJS-Mods/Rhino/pull/57/files)

## Overview

This patch fixes the `sizeOfParameters` method in Rhino's `ClassFileWriter` class to properly parse object signatures in arrays. The original implementation did not handle the 'L' case (object signatures) correctly, causing parsing failures when object types appeared within array signatures.

## Problem

The original `sizeOfParameters` method in `ClassFileWriter.java` had a bug where the 'L' case (indicating an object type in Java bytecode signatures) would fall through to the default case, causing the parser to fail and return an error. This prevented proper parsing of method signatures that contained object types within arrays.

For example, a signature like `([Ljava/lang/String;)V` (an array of String objects) would fail to parse correctly because the 'L' character was not handled properly.

## Solution

The fix adds proper handling for the 'L' case by:

1. Incrementing the parameter count when encountering an 'L' character
2. Finding the terminating semicolon (';') that marks the end of the object type
3. Validating that the semicolon exists and is within the expected bounds
4. Advancing the index to continue parsing after the object type

This allows the parser to correctly handle object signatures in arrays and other complex type scenarios.

## Implementation Details

The fix is implemented as a Mixin that overwrites the `sizeOfParameters` method in `ClassFileWriter`. The implementation:

- Uses `@Overwrite` to completely replace the method behavior with the fixed version
- Uses `@Restriction(require = @Condition("rhino"))` to only load when Rhino mod is present
- Always applies the fix when Rhino is loaded (no configuration gating)

The key change is in the 'L' case handling:

```java
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
```

## Configuration

This patch does not have a configuration option as mixins are applied during early class loading, before the mod configuration system is initialized. The patch is automatically applied whenever the Rhino mod is present.

This ensures the fix is always available when needed and eliminates any potential timing issues with configuration loading.
