# Contributing to Monkey Patches

Thank you for your interest in contributing to Monkey Patches! This document provides guidelines for contributing to the project.

## 🐛 Reporting Issues

When reporting issues, please include:
- Minecraft version
- NeoForge version
- Monkey Patches version
- Target mod version(s) affected
- Complete error logs or crash reports
- Steps to reproduce the issue

## 🔧 Adding New Patches

### Before Creating a Patch

1. **Verify the issue exists** in the target mod and hasn't been fixed upstream
2. **Check if a fix already exists** in the mod's repository (even if unreleased)
3. **Confirm the fix is appropriate** for a mixin-based patch
4. **Consider the scope** - patches should be minimal and targeted

### Patch Guidelines

1. **Naming Convention**:
   - Package: `io.mcmaster.monkeypatches.mixin.<modname>.<issue_id>`
   - Class: `<DescriptiveName>Mixin`
   - Example: `io.mcmaster.monkeypatches.mixin.kubejs.GH972.ServerScriptManagerMixin`

2. **Documentation Requirements**:
   - Add patch documentation in `docs/patches/<mod>_<issue>_<summary>.md`
   - Include link to original issue and upstream fix
   - Explain the problem, solution, and implementation details
   - Update main README.md with patch summary

3. **Mixin Best Practices**:
   - Use `@Restriction(require = @Condition("modid"))` for conditional loading
   - Add version predicates when applicable: `@Condition(value = "modid", versionPredicates = ">=1.0.1 <1.2")`
   - Include detailed JavaDoc comments explaining the patch
   - Use configuration checks where appropriate
   - Prefer minimal, targeted changes over broad modifications

4. **Configuration**:
   - Add configuration option in `Config.java` if the patch should be toggleable
   - Add corresponding check in `MixinConditions.java`
   - Update `example-config.toml` with the new option
   - Add localization labels in `src/main/resources/assets/monkeypatches/lang/en_us.json`

### Step-by-Step Process

1. **Create the Mixin class** in the appropriate package structure:
   - Package: `io.mcmaster.monkeypatches.mixin.<modname>.<issue_name_or_number>`
   - File naming: `<MixinName>.java`
   - Example: `io.mcmaster.monkeypatches.mixin.kubejs.GH972.ServerScriptManagerMixin`

2. **Add conditional loading annotations**:
   - Use `@Restriction(require = @Condition("modid"))` to only load when the target mod is present
   - Optionally add version predicates: `@Condition(value = "modid", versionPredicates = ">=1.0.1 <1.2")`

3. **Implement runtime configuration checks** (optional):
   - Add configuration option in `Config.java` under the appropriate mod section
   - Add check method in `Config.PatchConfig` class
   - Add condition method in `MixinConditions.java`
   - Add localization labels in `src/main/resources/assets/monkeypatches/lang/en_us.json`
   - Use the condition method in your mixin's `@Redirect` or `@Inject` methods

4. **Register the mixin** in `src/main/resources/monkeypatches.mixins.json`:
   - Add the mixin class name using dot notation relative to the base mixin package
   - Example: `"kubejs.GH972.ServerScriptManagerMixin"`

5. **Test the mixin**:
   - Run `./gradlew runClient` to test in development
   - Verify the mixin loads correctly and applies the intended fix
   - Test with and without the target mod installed

6. **Document the patch**:
   - Create documentation in `docs/patches/<mod_name>_<issue_id>_<short_summary>.md`
   - Update the "Included Fixes" section in the main README
   - Add configuration details if applicable

### Mixin Organization

Mixins are organized by:
- **Mod**: The target mod being patched (e.g., `kubejs`)
- **Issue**: The issue id (e.g. Github issue number) being addressed (e.g., `GH972`)
- **Mixin Name**: The specific mixin class name

#### Conditional Loading

All mixins use the [Conditional Mixin](https://github.com/Fallen-Breath/conditional-mixin) library to ensure they only load when the target mod is present. This prevents issues when target mods are missing.

Example structure:
```
io.mcmaster.monkeypatches.mixin.kubejs.GH972.ServerScriptManagerMixin
io.mcmaster.monkeypatches.mixin.kubejs.GH972.KubeJSModEventHandlerMixin
io.mcmaster.monkeypatches.mixin.subtle_effects.GH113.EndRemasteredCompatMixin
```

Each mixin class uses the `@Restriction(require = @Condition("modid"))` annotation to ensure conditional loading. When possible, version predicates should be added as well, such as `@Restriction(require = @Condition(value = "modid", versionPredicates = ">=1.0.1 <1.2"))`.

### Development Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/monkeypatches.git
   cd monkeypatches
   ```

2. **Import into IDE** (IntelliJ IDEA recommended)

3. **Test your changes**:
   ```bash
   ./gradlew runClient
   ```

4. **Build the mod**:
   ```bash
   ./gradlew build
   ```

### Pull Request Process

1. **Create a feature branch** from main
2. **Make your changes** following the guidelines above
3. **Test thoroughly** with the target mod installed
4. **Update documentation** as needed
5. **Submit a pull request** with:
   - Clear description of the patch and what it fixes
   - Link to the original issue in the target mod
   - Testing details and verification steps

## 🧪 Testing

- Test with both the minimum and maximum supported versions of target mods
- Verify the patch only loads when the target mod is present
- Test with the patch disabled via configuration
- Check that no conflicts exist with other mods

## 📋 Code Style

- Follow standard Java conventions
- Use descriptive variable and method names
- Include comprehensive comments for complex mixin logic
- Keep patches minimal and focused

## 🤝 Community Guidelines

- Be respectful and constructive in discussions
- Focus on technical merit when reviewing patches
- Acknowledge upstream authors and link to their work
- Consider the maintenance burden of new patches

## ❓ Questions?

If you have questions about contributing, feel free to:
- Open a discussion on GitHub
- Ask in the project's issue tracker
- Reach out to the maintainers

Thank you for helping improve Monkey Patches!
