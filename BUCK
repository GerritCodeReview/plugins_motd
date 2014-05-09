gerrit_plugin(
  name = 'motd',
  srcs = glob(['src/main/java/**/*.java']),
  manifest_entries = [
    'Gerrit-PluginName: motd',
    'Gerrit-Module: com.googlesource.gerrit.plugins.motd.Module',
    'Implementation-Title: Message of the Day',
    'Implementation-URL: https://gerrit.googlesource.com/plugins/motd'
  ],
  # Deps shared with Gerrit but not in the plugin API.
  provided_deps = [
    '//lib/jgit:jgit',
    '//lib/commons:net',
  ],
)

