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

import com.google.gerrit.extensions.events.LifecycleListener;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OnStartStop implements LifecycleListener {
  static final Logger log = LoggerFactory.getLogger(OnStartStop.class);

  @Inject
  OnStartStop(MotdConfig config) {
    log.info("Message of the day: " + config.getMotd());
  }

  @Override
  public void start() {
    // Do nothing.
  }

  @Override
  public void stop() {
    // Do nothing.
  }
}
