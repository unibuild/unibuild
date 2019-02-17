/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.revelc.code.gnome.keyring.lib;

import net.revelc.code.gnome.keyring.GnomeKeyringException;

public class GKResult {
  public static final int OK = 0;

  private final GKLib gklib;
  private int code;

  public GKResult(GKLib gklib, int code) {
    this.gklib = gklib;
    this.code = code;
  }

  public <T> T error() throws GnomeKeyringException {
    throw new GnomeKeyringException(gklib.gnome_keyring_result_to_message(code));
  }

  public boolean success() {
    return code == OK;
  }

}
