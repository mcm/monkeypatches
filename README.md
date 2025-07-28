# Monkey Patches

A NeoForge mod that provides backports and patches for multiple mods to fix bugs and issues that haven't been officially released yet.

## What is Monkey Patches?

**Monkey Patches** is a technical utility mod designed to patch bugs and issues in other mods through the use of runtime mixins. It is built for modpack developers and advanced users who need quick, modular fixes without waiting for upstream changes to be released.

## Features

* **Targeted Mixin-Based Patching**
  Monkey Patches allows you to inject small, scoped fixes into third-party mods by applying mixins at runtime. This enables developers to patch bugs, bypass crash conditions, or correct logic errors in dependencies without modifying the original mod’s source.

* **Non-Invasive and Configurable**
  The mod is designed to be lightweight and non-intrusive. All patches are applied via mixin at load time, without replacing jar files or using coremod techniques. Each patch can be scoped to specific versions of the target mod, minimizing compatibility issues.

* **Focused Patch Modules**
  Monkey Patches uses a modular internal structure, allowing you to clearly organize patches by mod or version. This makes it easy to document, isolate, and manage what you’re overriding.

* **Transparent Debugging Support**
  Optional debug logging provides transparency for applied mixins and helps you verify that patches are correctly loaded and active. This is especially useful when debugging modpack launch issues.

## How This Affects the User Experience

For modpack creators and technical users, Monkey Patches reduces downtime caused by known but unreleased bug fixes in popular mods. Instead of having to fork or wait for upstream patches, you can apply your own temporary fixes in a clean and trackable way.

For players, this results in more stable modpacks, fewer crashes, and fewer gameplay inconsistencies—especially in packs under active development or using snapshot mod versions.

## Use Case Scenarios

* Fixing a crash caused by a version-specific bug in another mod.
* Overriding unsafe assumptions or edge case behavior that affect your modpack.
* Ensuring compatibility between mods that were not originally designed to work together.
* Temporarily patching issues before submitting a pull request or waiting on a new release.

## Included Fixes

These fixes have been implemented in upstream repositories but are not yet available in stable or public builds. Monkey Patches provides a clean way to apply them in your modpack today.

### KubeJS Patches
- **ServerScriptManager Fix**: Fixes assumptions about concrete builder types that could cause ClassCastException
- **KubeJSModEventHandler Fix**: Fixes assumptions about BlockEntityBuilder instances in capability registration

### PortableTanks Patches
- **GH12 Null Compound Fix**: Fixes NullPointerException when accessing fluid data from ItemStacks without proper NBT data

### Subtle Effects Patches
- **EndRemasteredCompat Fix**: Fixes NeoForge crashing with End Remastered installed by changing block ticker registration to use predicate-based registration instead of direct block access (applies to version 1.11.0 only)

### Create Stuff 'N Additions Patches
- **CF59 Fluid Handler Capabilities**: Adds NeoForge fluid handler capabilities to gadgets and tanks, enabling compatibility with Create spouts and other fluid-handling systems

## Installation

1. Download the latest version from Curseforge
2. Place the JAR file in your mods folder
3. Launch Minecraft with NeoForge

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.186 or later

## Configuration

The mod includes a configuration file that can be accessed through the mod menu. Configuration options include:

### Patch Settings
- **KubeJS GH972 Patches**: Enable/disable the KubeJS issue #972 patches
  - `patches.kubejs.gh972_enabled` (default: true)
  - Controls ServerScriptManagerMixin and KubeJSModEventHandlerMixin

- **PortableTanks GH12 Patches**: Enable/disable the PortableTanks issue #12 patches
  - `patches.portabletanks.gh12_enabled` (default: true)
  - Controls PortableTankItemMixin null compound handling

- **Create Stuff 'N Additions CF59 Capabilities**: Enable/disable Create Stuff 'N Additions fluid handler capabilities
  - `patches.create_sa.fluid_handler_capabilities_enabled` (default: true)
  - Controls fluid capability registration for gadgets and tanks
  
Note: Subtle Effects patches only apply to specific versions (currently 1.11.0) where the issue is present, as they load too early in the process to access configuration.

### General Settings
- **Debug logging settings**: Various logging options for development
- **Magic number settings**: Example configuration values

### Configuration File Location
The configuration file is located at `config/monkeypatches-common.toml` in your Minecraft instance folder.

### Runtime Patch Control
Some patches can be enabled or disabled individually through the configuration file. Changes require a game restart to take effect.

## Development

This project uses NeoForge's Gradle template. To set up a development environment:

1. Clone the repository
2. Import into your IDE (IntelliJ IDEA or Eclipse recommended)
3. Run `./gradlew runClient` to test in a development environment

### Contributing

For detailed information about adding new patches, development setup, and contribution guidelines, please see [CONTRIBUTING.md](CONTRIBUTING.md).

## Release Process

This project includes automated publishing to CurseForge via GitHub Actions when a new tag is created. To set up automated releases:

### Setting up CurseForge Publishing

1. **Get a CurseForge API Token**:
   - Go to [CurseForge API Tokens](https://www.curseforge.com/account/api-tokens)
   - Create a new API token with upload permissions

2. **Find your Project ID**:
   - Go to your CurseForge project page
   - The numerical project ID can be found in the sidebar

3. **Add Repository Secrets and Variables**:
   - Go to your GitHub repository settings > Secrets and variables > Actions
   - Add the following **secret**:
     - `CF_API_TOKEN`: Your CurseForge API token
   - Add the following **variable** (in the Variables tab):
     - `CF_PROJECT_ID`: Your CurseForge project ID (numerical)

4. **Create a Release**:
   - Create and push a version tag (e.g., `v0.3.2`)
   - The workflow will automatically build and upload to CurseForge

### Manual Release Commands
```bash
# Tag the current commit with a version
git tag v0.3.2
git push origin v0.3.2

# Or create an annotated tag with a message
git tag -a v0.3.2 -m "Release version 0.3.2"
git push origin v0.3.2
```

## License

This project is licensed under the LGPL license. See the license file for details.

## Support

For issues, questions, or contributions, please use the GitHub issue tracker.
