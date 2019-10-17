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

import com.google.gerrit.entities.Project;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.git.validators.UploadValidationListener;
import com.google.gerrit.server.validators.ValidationException;
import com.google.inject.Inject;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UploadPack;

class DisplayMotd implements UploadValidationListener {
  private final CurrentUser user;
  private final MotdConfig config;

  @Inject
  public DisplayMotd(CurrentUser user, MotdConfig config) {
    this.user = user;
    this.config = config;
  }

  @Override
  public void onPreUpload(
      Repository repository,
      Project project,
      String remote,
      UploadPack up,
      Collection<? extends ObjectId> wants,
      Collection<? extends ObjectId> haves)
      throws ValidationException {
    PrintWriter pw = new PrintWriter(up.getMessageOutputStream());

    if (config.getMotd() != null) {
      pw.print(format(config.getMotd()));
      pw.print("\n");
    }

    try {
      String inet = InetAddress.getByName(remote).getHostAddress();
      for (Subnet m : config.getSubnets()) {
        if (m.isInRange(inet) && m.getMotd() != null) {
          pw.print(format(m.getMotd()));
          pw.print("\n");
          break;
        }
      }
    } catch (UnknownHostException e) {
      // Couldn't look up the hostname; this is non-fatal
    }
    pw.flush();
  }

  private String format(String message) {
    if (user.getUserName().isPresent()) {
      message = message.replace("${user}", user.getUserName().get());
    }
    return message;
  }
}
