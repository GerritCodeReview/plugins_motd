include_defs('//bucklets/gerrit_plugin.bucklet')
include_defs('//bucklets/java_sources.bucklet')

SOURCES = glob(['src/main/java/**/*.java'])
RESOURCES = glob(['src/main/resources/**/*'])

DEPS = GERRIT_PLUGIN_API + [
  ':motd__plugin',
]

gerrit_plugin(
  name = 'motd',
  srcs = SOURCES,
  resources = RESOURCES,
  manifest_entries = [
    'Gerrit-PluginName: motd',
    'Gerrit-Module: com.googlesource.gerrit.plugins.motd.Module',
    'Implementation-Title: Message of the Day',
    'Implementation-URL: https://gerrit.googlesource.com/plugins/motd',
  ],
)

java_library(
  name = 'classpath',
  deps = DEPS,
)

java_sources(
  name = 'websession-flatfile-sources',
  srcs = SOURCES + RESOURCES,
)

