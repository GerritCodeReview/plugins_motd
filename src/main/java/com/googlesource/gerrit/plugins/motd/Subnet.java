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

import org.apache.commons.net.util.SubnetUtils;

class Subnet {
  private String motd;
  private SubnetUtils subnet;

  public Subnet(SubnetConfig c, SubnetUtils subnet) {
    this.motd = c.getMotd();
    this.subnet = subnet;
  }

  public String getMotd() {
    return motd;
  }

  public boolean isInRange(String addr) {
    return subnet.getInfo().isInRange(addr);
  }
}
