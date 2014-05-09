include_defs('//lib/maven.defs')

gerrit_plugin(
  name = 'motd',
  srcs = glob(['src/main/java/**/*.java']),
  deps = [
  ],
  # Deps shared with Gerrit but not in the plugin API.
  provided_deps = [
    '//lib/jgit:jgit',
    '//lib/commons:net',
  ],
  manifest_entries = [
    'Gerrit-PluginName: motd',
    'Gerrit-Module: com.googlesource.gerrit.plugins.motd.Module',
  ],
  visibility = [],
)

