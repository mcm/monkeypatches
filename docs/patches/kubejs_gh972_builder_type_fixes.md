# KubeJS GH972 Builder Type Assumption Fixes

[Link to issue](https://github.com/KubeJS-Mods/KubeJS/issues/972)<br>
[Link to fix](https://github.com/KubeJS-Mods/KubeJS/commit/c4f0afa53516ba778ddb40e4b8045890f3f1aa5c)

## Overview

This patch fixes assumptions about concrete builder types in KubeJS that could cause ClassCastException when CustomBuilderObject instances are present in registries. The fix includes two mixins that address the same underlying issue in different parts of the KubeJS codebase.

## Problem

The original code in multiple parts of KubeJS made assumptions about concrete builder types, leading to ClassCastException when CustomBuilderObject instances were introduced.

### ServerScriptManager Issue
The code in `ServerScriptManager.loadAdditional()` assumed all objects in the ITEM registry were `ItemBuilder` instances:
```java
for (ItemBuilder builder : ITEM.objects.values()) {
    // Process builder...
}
```

### KubeJSModEventHandler Issue
The code in `KubeJSModEventHandler.registerCapabilities()` assumed all objects in the BLOCK_ENTITY collection were `BlockEntityBuilder` instances:
```java
collection.stream().forEach(builder -> {
    BlockEntityBuilder blockEntityBuilder = (BlockEntityBuilder) builder;
    // Process blockEntityBuilder...
});
```

Both cases would fail with ClassCastException when `CustomBuilderObject` instances were present.

## Solution

The patch provides two mixins that intercept collection access and add proper type filtering before processing:

1. **ServerScriptManager**: Intercepts the iterator call and filters to only include `ItemBuilder` instances
2. **KubeJSModEventHandler**: Intercepts the stream creation and filters to only include `BlockEntityBuilder` instances

Both fixes prevent ClassCastException by ensuring only the expected types are processed.

## Implementation Details

This fix is implemented through two separate mixin classes that address the same underlying issue:

### ServerScriptManagerMixin

**File**: `ServerScriptManagerMixin.java`  
**Package**: `io.mcmaster.monkeypatches.mixin.kubejs.GH972`

**Key Features**:
- Uses `@Redirect` mixin to intercept the `iterator()` call on `RegistryObjectStorage`
- Filters the collection using `stream().filter(ItemBuilder.class::isInstance)`
- **Mixin Target**: `ServerScriptManager.loadAdditional()` method  
- **Injection Point**: `RegistryObjectStorage.iterator()` call

### KubeJSModEventHandlerMixin

**File**: `KubeJSModEventHandlerMixin.java`  
**Package**: `io.mcmaster.monkeypatches.mixin.kubejs.GH972`

**Key Features**:
- Uses `@Redirect` mixin to intercept the `stream()` call on Collection
- Filters the stream using `filter(BlockEntityBuilder.class::isInstance)`
- **Mixin Target**: `KubeJSModEventHandler.registerCapabilities()` method  
- **Injection Point**: `Collection.stream()` call
- Static method targeting for proper capability registration

### Common Features

Both mixins share these characteristics:
- Configurable via `patches.kubejs.gh972_enabled` setting
- Only load when KubeJS mod is present (conditional loading)
- Check configuration setting before applying fixes
- Graceful fallback to original behavior when disabled

## Configuration

Both patches can be controlled via the same configuration setting:
- **Setting**: `patches.kubejs.gh972_enabled`
- **Default**: `true`
- **Type**: Boolean
- **Description**: Enable/disable both KubeJS GH972 patches (ServerScriptManager and KubeJSModEventHandler)

Changes to this setting require a game restart to take effect.
