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
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.commons.net.util.SubnetUtils;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

@Singleton
public class MotdFileBasedConfig implements MotdConfig {
  static final Logger log = LoggerFactory.getLogger(MotdFileBasedConfig.class);
  private List<Subnet> subnets;
  private String motd;
  private final Config config;

  @Inject
  public MotdFileBasedConfig(@PluginName String pluginName,
      PluginConfigFactory cfg) {
    this.config = cfg.getGlobalPluginConfig(pluginName);
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

  private List<Subnet> allSubnets() {
    String[] motdLines = config.getStringList("gerrit", null, "motd");
    motd = Joiner.on("\n").useForNull("").join(motdLines);

    ImmutableList.Builder<Subnet> subnetlist = ImmutableList.builder();
    for (SubnetConfig c : allSubnets(config)) {
      if (c.getMotd().isEmpty()) {
        log.warn("Subnet configuration for " + c.getSubnet()
            + " has no message");
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

  private static List<SubnetConfig> allSubnets(Config cfg) {
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
}
