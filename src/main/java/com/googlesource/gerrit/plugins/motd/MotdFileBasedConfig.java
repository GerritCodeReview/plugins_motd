// Copyright (C) 2014 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.googlesource.gerrit.plugins.motd;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.net.util.SubnetUtils;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MotdFileBasedConfig implements MotdConfig {
  static final Logger log = LoggerFactory.getLogger(MotdFileBasedConfig.class);
  private List<Subnet> subnets;
  private File cfgPath;
  private String motd;
  private final FileBasedConfig config;

  @Inject
  public MotdFileBasedConfig(final SitePaths site) throws ConfigInvalidException, IOException {
    this.cfgPath = new File(site.etc_dir.toFile(), "motd.config");
    log.info("Loading configuration from " + cfgPath);
    this.config = new FileBasedConfig(cfgPath, FS.DETECTED);
    this.subnets = allSubnets();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.googlesource.gerrit.plugins.motd.MotdConfig#getSubnets()
   */
  @Override
  public List<Subnet> getSubnets() {
    return subnets;
  }

  private List<Subnet> allSubnets() throws ConfigInvalidException, IOException {
    if (!config.getFile().exists()) {
      log.warn("Config file " + config.getFile() + " does not exist; not providing messages");
      return Collections.emptyList();
    }
    if (config.getFile().length() == 0) {
      log.info("Config file " + config.getFile() + " is empty; not providing messages");
      return Collections.emptyList();
    }

    try {
      config.load();
    } catch (ConfigInvalidException e) {
      throw new ConfigInvalidException(
          String.format("Config file %s is invalid: %s", config.getFile(), e.getMessage()), e);
    } catch (IOException e) {
      throw new IOException(
          String.format("Cannot read %s: %s", config.getFile(), e.getMessage()), e);
    }

    String[] motdLines = config.getStringList("gerrit", null, "motd");
    motd = Joiner.on("\n").useForNull("").join(motdLines);

    ImmutableList.Builder<Subnet> subnetlist = ImmutableList.builder();
    for (SubnetConfig c : allSubnets(config)) {
      if (c.getMotd().isEmpty()) {
        log.warn("Subnet configuration for " + c.getSubnet() + " has no message");
        continue;
      }

      try {
        SubnetUtils su = new SubnetUtils(c.getSubnet());
        Subnet subnet = new Subnet(c, su);
        subnetlist.add(subnet);
      } catch (IllegalArgumentException e) {
        log.warn("Subnet '" + c.getSubnet() + "' is invalid; skipping");
        continue;
      }
    }

    return subnetlist.build();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.googlesource.gerrit.plugins.motd.MotdConfig#getMotd()
   */
  @Override
  public String getMotd() {
    return motd;
  }

  private static List<SubnetConfig> allSubnets(FileBasedConfig cfg) {
    Set<String> names = cfg.getSubsections("subnet");
    List<SubnetConfig> result = Lists.newArrayListWithCapacity(names.size());
    for (String name : names) {
      result.add(new SubnetConfig(cfg, name));
    }
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.googlesource.gerrit.plugins.motd.MotdConfig#isEmpty()
   */
  @Override
  public boolean isEmpty() {
    return subnets.isEmpty();
  }

  File getCfgPath() {
    return cfgPath;
  }

  FileBasedConfig getConfig() {
    return config;
  }
}
