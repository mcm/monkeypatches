# Configuration Guide for Monkey Patches

This guide explains how to configure the Monkey Patches mod to control which patches are applied.

## Configuration File Location

The configuration file is automatically created at:
```
<minecraft_instance>/config/monkeypatches-common.toml
```

## Configuration Structure

The configuration is organized into sections:

### Patch Settings (`[patches]`)

This section controls which patches are enabled or disabled.

#### KubeJS Patches (`[patches.kubejs]`)

- **`gh972_enabled`** (default: `true`)
  - Controls the KubeJS issue #972 patches
  - Affects: `ServerScriptManagerMixin` and `KubeJSModEventHandlerMixin`
  - Purpose: Fixes assumptions about concrete builder types that could cause ClassCastException

## Example Configuration

```toml
[patches]
    [patches.kubejs]
        # Enable/disable KubeJS GH972 patches
        gh972_enabled = true
```

## Adding New Patch Groups

When adding new patch groups, follow this pattern:

1. **Add conditional annotation** to the mixin class:
   ```java
   @Restriction(require = @Condition("targetmod"))
   @Mixin(value = TargetClass.class, remap = false)
   public class MyMixin {
   ```

2. **Add configuration option** in `Config.java`:
   ```java
   // In the appropriate mod section
   public static final ModConfigSpec.BooleanValue NEW_PATCH_ENABLED = BUILDER
           .comment("Enable SomeMod issue #123 patches")
           .define("issue123_enabled", true);
   ```

3. **Add condition method** in `Config.PatchConfig`:
   ```java
   public static boolean isNewPatchEnabled() {
       return NEW_PATCH_ENABLED.get();
   }
   ```

4. **Add condition in `util/MixinConditions.java`**:
   ```java
   public static boolean shouldApplyNewPatch() {
       return ModList.get().isLoaded("targetmod") && Config.PatchConfig.isNewPatchEnabled();
   }
   ```

5. **Use in mixin classes**:
   ```java
   @Redirect(...)
   private ReturnType redirectMethod(OriginalClass instance) {
       if (!MixinConditions.shouldApplyNewPatch()) {
           return instance.originalMethod();
       }
       // Apply patch logic
   }
   ```

## Conditional Loading

This mod uses the [Conditional Mixin](https://github.com/Fallen-Breath/conditional-mixin) library to ensure patches only load when the target mod is present. This provides:

- **Automatic mod detection**: Mixins only load if the target mod is installed
- **No conflicts**: Missing target mods don't cause issues
- **Clean logs**: Only relevant patches are logged

### Conditional Status

The mod logs show three possible states for each patch group:
- **ENABLED**: Mod is loaded and patch is enabled in config
- **DISABLED**: Mod is loaded but patch is disabled in config  
- **MOD NOT LOADED**: Target mod is not present

## Restart Required

Changes to the configuration file require a full game restart to take effect, as mixins are applied during the early loading phase.

## Logging

The mod logs the status of all patches during startup. Look for lines like:
```
[INFO] === Monkey Patches Status ===
[INFO]   KubeJS GH972: Fixes assumptions about concrete builder types [ENABLED]
[INFO] === End Patch Status ===
```

## Troubleshooting

1. **Configuration not loading**: Ensure the file is in the correct location and has valid TOML syntax
2. **Patches not applying**: Check that the patch is enabled in the configuration and restart the game
3. **Missing configuration**: Delete the config file and restart - it will be regenerated with defaults
