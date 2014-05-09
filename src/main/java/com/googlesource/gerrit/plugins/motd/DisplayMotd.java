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

import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.git.validators.UploadValidationException;
import com.google.gerrit.server.git.validators.UploadValidationListener;
import com.google.inject.Inject;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UploadPack;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

class DisplayMotd implements UploadValidationListener {
  @Inject
  private CurrentUser user;

  @Inject
  private MotdConfig config;

  @Override
  public void onPreUpload(Repository repository, Project project, String remote,
    UploadPack up, Collection<? extends ObjectId> wants,
    Collection<? extends ObjectId> haves) throws UploadValidationException {
    PrintWriter pw = new PrintWriter(up.getMessageOutputStream());

    if (config.getMotd() != null) {
      pw.print(format(config.getMotd()));
      pw.print("\n");
    }

    try {
      String inet = InetAddress.getByName(remote).getHostAddress();
      for (Subnet m : config.getSubnets()) {
        if (m.isInRange(inet) && m.getMessage() != null) {
          pw.print(format(m.getMessage()));
          pw.print("\n");
          break;
        }
      }
    } catch ( UnknownHostException e ) {
        // Couldn't look up the hostname; this is non-fatal
    }
    pw.flush();
  }
  private String format(String message) {
    if (user.getUserName() != null) {
      message = message.replace("${user}", user.getUserName());
    }
    return message;
  }
}
