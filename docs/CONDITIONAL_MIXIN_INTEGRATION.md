# Conditional Mixin Integration Summary

This document summarizes the integration of the Conditional Mixin library into the Monkey Patches mod, providing automatic mod-dependent mixin loading.

## What Was Implemented

### 1. **Conditional Mixin Library Integration**

- **Added dependency**: `me.fallenbreath:conditional-mixin-neoforge:0.6.3`
- **Added repository**: Fallen-Breath Maven repository
- **Updated mixins.json**: Added conditional mixin plugin configuration

### 2. **Automatic Mod Detection**

All mixins now use the `@Restriction(require = @Condition("modid"))` annotation:

```java
@Restriction(require = @Condition("kubejs"))
@Mixin(value = ServerScriptManager.class, remap = false)
public class ServerScriptManagerMixin {
    // Mixin implementation
}
```

### 3. **Enhanced Status Reporting**

The `PatchInfo` utility now reports three states:
- **ENABLED**: Target mod loaded and patch enabled in config
- **DISABLED**: Target mod loaded but patch disabled in config  
- **MOD NOT LOADED**: Target mod not present

### 4. **Runtime Safety Checks**

Updated `MixinConditions` to include both mod loading and configuration checks:

```java
public static boolean shouldApplyKubeJSGH972() {
    return ModList.get().isLoaded("kubejs") && Config.PatchConfig.isKubeJSGH972Enabled();
}
```

## Key Benefits

### 1. **No Conflicts**
- Mixins only load when target mods are present
- No class loading errors when target mods are missing
- Clean startup when dependencies are absent

### 2. **Automatic Detection**
- No manual configuration needed for mod detection
- Automatic handling of optional dependencies
- Graceful degradation when mods are missing

### 3. **Better User Experience**
- Clear logging of patch status
- Intuitive configuration options
- No confusing errors about missing mods

### 4. **Maintainability**
- Easy to add new mod-specific patches
- Clear separation of concerns
- Consistent pattern for future development

## Configuration Integration

The configuration system works in harmony with conditional loading:

```toml
[patches]
    [patches.kubejs]
        # Only relevant if KubeJS is installed
        gh972_enabled = true
```

## Startup Logging

Example startup logs show the integration working:

```
[INFO] === Monkey Patches Status ===
[INFO]   KubeJS GH972: Fixes assumptions about concrete builder types [ENABLED]
[INFO] === End Patch Status ===
```

Or when KubeJS is not installed:

```
[INFO] === Monkey Patches Status ===
[INFO]   KubeJS GH972: Fixes assumptions about concrete builder types [MOD NOT LOADED]
[INFO] === End Patch Status ===
```

## Future Expansion

Adding new conditional patches is now straightforward:

1. Add the conditional annotation to the mixin
2. Add configuration options
3. Update the condition methods
4. Add to the patch info logging

Example for a hypothetical new mod:

```java
@Restriction(require = @Condition("somemod"))
@Mixin(value = SomeClass.class, remap = false)
public class SomeClassMixin {
    // Implementation
}
```

## Technical Details

### Conditional Mixin Plugin

The plugin is configured in `monkeypatches.mixins.json`:

```json
{
  "plugin": "me.fallenbreath.conditionalmixin.api.mixin.ConditionTesterMixinPlugin"
}
```

### Gradle Dependencies

```gradle
dependencies {
    // Conditional Mixin library for mod-dependent mixins
    implementation "me.fallenbreath:conditional-mixin-neoforge:0.6.3"
}

repositories {
    maven {
        name = 'Fallen-Breath Maven'
        url = 'https://maven.fallenbreath.me/releases'
        content {
            includeGroup "me.fallenbreath"
        }
    }
}
```

## Conclusion

The Conditional Mixin library integration provides a robust foundation for mod-dependent patches, ensuring that Monkey Patches can safely provide fixes for multiple mods without conflicts or dependency issues.
